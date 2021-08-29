/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.twitter;

import java.util.*;
import java.io.*;

import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

import static org.luwrain.controls.ConsoleUtils.*;

final class SearchLayout extends LayoutBase implements ConsoleArea.ClickHandler, ConsoleArea.InputHandler
{
    private final App app;
    final ConsoleArea searchArea;
    private List<Tweet> tweets = new ArrayList<>();

    SearchLayout(App app)
    {
	super(app);
	this.app = app;
	this.searchArea = new ConsoleArea(consoleParams((params)->{
		    params.context = new DefaultControlContext(app.getLuwrain());
		    params.model = new ListModel(tweets);
		    params.appearance = new SearchAreaAppearance();
		    params.name = app.getStrings().searchAreaName();
		    params.inputPos = ConsoleArea.InputPos.TOP;
		    params.inputPrefix = app.getStrings().search() + ">";
		}));
	searchArea.setConsoleInputHandler(this);
	searchArea.setConsoleInputHandler(this);
	final Actions searchActions = actions(
					      action("status", app.getStrings().actionStatus(), App.HOTKEY_MAIN, app.layouts()::main),
					      action("search", app.getStrings().actionSearch(), App.HOTKEY_SEARCH, app.layouts()::search),
					      action("search-users", app.getStrings().actionSearchUsers(), App.HOTKEY_SEARCH_USERS, app.layouts()::searchUsers),
					      action("following", "Подписки и подписчики", App.HOTKEY_FOLLOWING, app.layouts()::following)
					      );
	setAreaLayout(searchArea, searchActions);
    }

    boolean search(String query)
    {
	NullCheck.notNull(query, "query");
	if (app.isBusy())
	    return false;
	if (query.trim().isEmpty())
	    return false;
	final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final Tweet[] res;
		try {
		    res = searchQuery(query, 1);
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->{
			tweets.clear();
			tweets.addAll(Arrays.asList(res));
			searchArea.refresh();
			app.getLuwrain().playSound(Sounds.OK);
		    });
	    });
    }

    private Tweet[] searchQuery(String text, int pageCount) throws TwitterException
    {
	NullCheck.notEmpty(text, "text");
	final List<Tweet> tweets = new ArrayList<>();
	final Set<String> texts = new HashSet<>();
	Query query = new Query(text);
	QueryResult result;
	int pageNum = 1;
	do {
	    result = app.getTwitter().search(query);
	    List<Status> statuses = result.getTweets();
	    for (Status tw : statuses)
	    {
		final Tweet tweet = new Tweet(tw);
		if (texts.contains(tweet.getReducedText().toUpperCase()))
		    continue;
		tweets.add(tweet);
		texts.add(tweet.getReducedText().toUpperCase());
	    }
	    if (pageNum >= pageCount)
		return tweets.toArray(new Tweet[tweets.size()]);
	    ++pageNum;
	} while ((query = result.nextQuery()) != null);
	return tweets.toArray(new Tweet[tweets.size()]);
    }

    @Override public boolean onConsoleClick(ConsoleArea area, int index, Object obj)
    {
	return false;
    }

    @Override public ConsoleArea.InputHandler.Result onConsoleInput(ConsoleArea area, String text)
    {
	NullCheck.notNull(text, "text");
	return search(text)?ConsoleArea.InputHandler.Result.OK:ConsoleArea.InputHandler.Result.REJECTED;
    }

    void onActivation()
    {
	app.getLuwrain().setActiveArea(searchArea);
    }

    private final class SearchAreaAppearance implements ConsoleArea.Appearance
    {
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (!(item instanceof Tweet))
	    {
	    app.getLuwrain().setEventResponse(DefaultEventResponse.text(item.toString()));
	    return;
	    }
	    final Tweet tweet = (Tweet)item;
	    final StringBuilder b = new StringBuilder();
	    b.append(app.getLuwrain().getSpeakableText(tweet.getReducedText(), Luwrain.SpeakableTextType.NATURAL))
	    .append(", ")
	    .append(tweet.getTimeMark(app.getI18n()))
	    .append(", ")
	    .append(tweet.getUserName());
	    app.getLuwrain().setEventResponse(DefaultEventResponse.text(new String(b)));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
    }
}
