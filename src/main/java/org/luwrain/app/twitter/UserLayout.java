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
import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

final class UserLayout extends LayoutBase
{
    private final App app;
    private final String userName;
    private final Runnable closing;
    private final ListArea tweetsArea;

    private Tweet[] tweets = new Tweet[0];

    UserLayout(App app, String userName, Runnable closing)
    {
	NullCheck.notNull(app, "app");
	NullCheck.notNull(userName, "userName");
	NullCheck.notNull(closing, "closing");
	this.app = app;
	this.userName = userName;
	this.closing = closing;
	final ActionInfo follow = action("follow", app.getStrings().actionFollow(), new InputEvent(InputEvent.Special.F5, EnumSet.of(InputEvent.Modifiers.SHIFT)), UserLayout.this::actFollow);
	this.tweetsArea = new ListArea(createTweetsAreaParams()) {
		final Actions actions = actions(
						follow
						);
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
		    if (app.onSystemEvent(this, event, actions))
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
		@Override public Action[] getAreaActions()
		{
		    return actions.getAreaActions();
		}
	    };
	}

    boolean update()
    {
	if (app.isBusy())
	    return false;
	final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final Tweet[] res;
		try {
		    res = getUserTimeline(userName);
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->{
			tweets = res;
			tweetsArea.refresh();
			app.getLuwrain().setActiveArea(tweetsArea);
		    });
	    });
    }

    private Tweet[] getUserTimeline(String userName) throws TwitterException
    {
	NullCheck.notEmpty(userName, "userName");
	final List<Status> result = app.getTwitter().getUserTimeline(userName);
	if (result == null)
	    return new Tweet[0];
	final List<Tweet> tweets = new ArrayList<>();
	for(Status s: result)
	    tweets.add(new Tweet(s));
	return tweets.toArray(new Tweet[tweets.size()]);
    }

    private boolean actFollow()
    {
	if (app.isBusy())
	    return false;
	final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		try {
		    app.getTwitter().createFriendship(userName);
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->app.getLuwrain().playSound(Sounds.DONE));
	    });
    }

    private ListArea.Params createTweetsAreaParams()
    {
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.model = new TweetsModel();
	params.appearance = new TweetListAppearance(app.getLuwrain(), app.getStrings());
	params.name = "name";
	return params;
    }

    AreaLayout getLayout()
    {
	return new AreaLayout(tweetsArea);
    }

    private final class TweetsModel implements ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return tweets.length;
	}
	@Override public Object getItem(int index)
	{
	    return tweets[index];
	}
	@Override public void refresh()
	{
	}
    }
}
