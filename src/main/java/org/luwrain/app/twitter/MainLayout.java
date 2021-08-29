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

import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

import static org.luwrain.controls.EditUtils.*;
import static org.luwrain.controls.ListUtils.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    final ListArea statusArea;
    final EditArea postArea;

    private Tweet[] homeTimeline = new Tweet[0];

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.statusArea = new ListArea(listParams((params)->{
		    params.model = new ArrayModel(()->{ return homeTimeline; });
		    params.appearance = new TweetListAppearance(app.getLuwrain(), app.getStrings());
		    params.name = app.getStrings().statusAreaName();
		})){
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() == SystemEvent.Type.REGULAR)
			switch(event.getCode())
			{
			case SAVE:
			    return actLike();
			}
		    return super.onSystemEvent(event);
		}
	    };
	final Actions statusActions = actions(
					      action("like", app.getStrings().actionLike(), this::actLike),
					      action("delete-tweet", app.getStrings().actionDeleteTweet(), new InputEvent(InputEvent.Special.DELETE), this::actDelete),
					      action("search", app.getStrings().actionSearch(), App.HOTKEY_SEARCH, app.layouts()::search),
					      action("search-users", app.getStrings().actionSearchUsers(), App.HOTKEY_SEARCH_USERS, app.layouts()::searchUsers),
					      action("following", "Подписки и подписчики", App.HOTKEY_FOLLOWING, app.layouts()::following)
					      );
	this.postArea = new EditArea(editParams((params)->{
		    params.appearance = new DefaultEditAreaAppearance(getControlContext(), Luwrain.SpeakableTextType.PROGRAMMING);
		    params.name = app.getStrings().postAreaName();
		})){
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() == SystemEvent.Type.REGULAR)
			switch(event.getCode())
			{
			case OK:
			    return actPost();
			}
		    return super.onSystemEvent(event);
		}
	    };
	final Actions postActions = actions(
					    action("search", app.getStrings().actionSearch(), App.HOTKEY_SEARCH, app.layouts()::search),
					    action("search-users", app.getStrings().actionSearchUsers(), App.HOTKEY_SEARCH_USERS, app.layouts()::searchUsers),
					    action("following", "Подписки и подписчики", App.HOTKEY_FOLLOWING, app.layouts()::following)
					    );
	setAreaLayout(AreaLayout.TOP_BOTTOM, statusArea, statusActions, postArea, postActions);
    }

    boolean updateHomeTimelineBkg()
    {
	if (app.isBusy())
	    return false;
	final AppBase.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final Tweet[] result;
		try {
		    result = Tweet.create(app.getTwitter().getHomeTimeline());
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->{
			homeTimeline = result;
			statusArea.refresh();
			app.getLuwrain().setActiveArea(statusArea);
		    });
	    });
    }

        boolean actPost()
    {
	if (app.isBusy())
	    return false;
	final String text = makeTweetText(postArea.getText());
	if (text.isEmpty())
	    return false;
	final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final Tweet[] result;
		try {
		    app.getTwitter().updateStatus(text);
		    		    result = Tweet.create(app.getTwitter().getHomeTimeline());
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->{
			homeTimeline = result;
			statusArea.reset(false);
			statusArea.refresh();
						postArea.clear();
						app.getLuwrain().setActiveArea(statusArea);
		    });
	    });
	    }

        private String makeTweetText(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	if (lines.length == 0)
	    return "";
	final List<String> validLines = new ArrayList<>();
	for(String s: lines)
	    if (!s.trim().isEmpty())
		validLines.add(s.trim());
	if (validLines.isEmpty())
	    return "";
	final StringBuilder b = new StringBuilder();
	for(String s: validLines)
	    b.append(s).append(" ");
	return new String(b).toString();
    }

    boolean actDelete()
    {
	final Object obj = statusArea.selected();
	if (obj == null || !(obj instanceof Tweet))
	    return true;
	final Tweet tweet = (Tweet)obj;
	if (app.isBusy())
	    return false;
	if (!app.conv().confirmTweetDeleting(tweet))
	    return true;
	final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final Tweet[] result;
		try {
		    app.getTwitter().destroyStatus(tweet.tweet.getId());
		    		    		    result = Tweet.create(app.getTwitter().getHomeTimeline());
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->{
			homeTimeline = result;
			statusArea.refresh();
			app.getLuwrain().playSound(Sounds.DONE);
		    });
	    });
	    }

    private boolean actLike()
    {
	if (app.isBusy())
	    return false;
	final Object obj = statusArea.selected();
	if (obj == null || !(obj instanceof Tweet))
	    return false;
	final Tweet tweet = (Tweet)obj;
	final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		try {
app.getTwitter().createFavorite(tweet.tweet.getId());
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
app.finishedTask(taskId, ()->app.getLuwrain().playSound(Sounds.DONE));
	    });
	        }
}
