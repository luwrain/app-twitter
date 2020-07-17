/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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
import org.luwrain.template.*;

final class SearchLayout extends LayoutBase implements ConsoleArea.ClickHandler, ConsoleArea.InputHandler
{
    private final App app;
    private final ConsoleArea searchArea;
    
    private Tweet[] tweets = new Tweet[0];

    SearchLayout(App app)
    {
	NullCheck.notNull(app, "aapp");
	this.app = app;
	final Runnable closing = ()->app.layouts().main();
	this.searchArea = new ConsoleArea(getSearchAreaParams()){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event, closing))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
	    };
	searchArea.setConsoleInputHandler(this);
	searchArea.setConsoleInputHandler(this);
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
			tweets = res;
			searchArea.refresh();
			app.getLuwrain().playSound(Sounds.OK);
		    });
	    });
    }

    private Tweet[] searchQuery(String text, int pageCount) throws TwitterException
    {
	NullCheck.notEmpty(text, "text");
	final List<Tweet> tweets = new LinkedList();
	final Set<String> texts = new HashSet();
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

    ConsoleArea.Params getSearchAreaParams()
    {
	final ConsoleArea.Params params = new ConsoleArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.model = new SearchAreaModel();
	params.appearance = new SearchAreaAppearance();
	params.name = app.getStrings().searchAreaName();
	params.inputPos = ConsoleArea.InputPos.TOP;
	params.inputPrefix = app.getStrings().search() + ">";
	return params;
    }

    AreaLayout getLayout()
    {
	return new AreaLayout(searchArea);
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

    private final class SearchAreaModel implements ConsoleArea.Model
    {
        @Override public int getItemCount()
	{
	    return tweets.length;
	}
	@Override public Object getItem(int index)
	{
	    if (index < 0 || index >= tweets.length)
		throw new IllegalArgumentException("index (" + index + ") must be greater or equal to zero and less than " + String.valueOf(tweets.length));
	    return tweets[index];
	}
    }
}
