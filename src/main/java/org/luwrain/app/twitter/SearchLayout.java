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
    private final App2 app;
    private final ConsoleArea searchArea;

    private Tweet[] tweets = new Tweet[0];

    SearchLayout(App2 app)
    {
	NullCheck.notNull(app, "aapp");
	this.app = app;
	final Runnable closing = ()->app.layouts().main();
	this.searchArea = new ConsoleArea(getSearchAreaParams()){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event, closing))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
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
	final App2.TaskId taskId = app.newTaskId();
	return app.runTask(()->{
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
	Query query = new Query(text);
	QueryResult result;
	int pageNum = 1;
	do {
	    result = app.getTwitter().search(query);
	    List<Status> statuses = result.getTweets();
	    for (Status tweet : statuses) 
		tweets.add(new Tweet(tweet));
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
	params.areaName = app.getStrings().searchAreaName();
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
	    app.getLuwrain().setEventResponse(DefaultEventResponse.text(item.toString()));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
    }

    private final class SearchAreaModel implements ConsoleArea.Model
    {
        @Override public int getConsoleItemCount()
	{
	    return tweets.length;
	}
	@Override public Object getConsoleItem(int index)
	{
	    if (index < 0 || index >= tweets.length)
		throw new IllegalArgumentException("index (" + index + ") must be greater or equal to zero and less than " + String.valueOf(tweets.length));
	    return tweets[index];
	}
    }
}
