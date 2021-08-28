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

final class SearchUsersLayout extends LayoutBase implements ConsoleArea.ClickHandler, ConsoleArea.InputHandler
{
    private final App app;
    private final ConsoleArea searchArea;

    private UserWrapper[] users = new UserWrapper[0];

    SearchUsersLayout(App app)
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
		final UserWrapper[] res;
		try {
		    res = searchQuery(query, 1);
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->{
			users = res;
			searchArea.refresh();
			app.getLuwrain().playSound(Sounds.OK);
		    });
	    });
    }

    private UserWrapper[] searchQuery(String query, int pageCount) throws TwitterException
    {
	NullCheck.notEmpty(query, "query");
	final List<UserWrapper> users = new ArrayList<>();
	//	Query query = new Query(text);
	//		QueryResult result;
ResponseList<User> result = null;	
	int pageNum = 1;
	do {
result = app.getTwitter().searchUsers(query, pageNum);
	    //	    List<Status> statuses = result.getTweets();
	    for (User u: result)
		users.add(new UserWrapper(u));
	    if (pageNum >= pageCount)
		return users.toArray(new UserWrapper[users.size()]);
	    ++pageNum;
	}
	while (result.size() > 0);
	return users.toArray(new UserWrapper[users.size()]);
    }

    @Override public boolean onConsoleClick(ConsoleArea area, int index, Object obj)
    {
	if (obj == null || !(obj instanceof UserWrapper))
	    return false;
	final UserWrapper user = (UserWrapper)obj;
	final UserLayout userLayout = new UserLayout(app, user.user.getScreenName(), ()->{
		app.layouts().custom(this.getLayout());
		app.getLuwrain().setActiveArea(searchArea);
	    });
		app.layouts().custom(userLayout.getLayout());
			userLayout.update();
	return true;
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
	params.name = app.getStrings().searchUsersAreaName();
	params.inputPos = ConsoleArea.InputPos.TOP;
	params.inputPrefix = app.getStrings().searchUsersInputPrefix() + ">";
	params.clickHandler = this;
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
	    if (!(item instanceof UserWrapper))
	    {
	    app.getLuwrain().setEventResponse(DefaultEventResponse.text(item.toString()));
	    return;
	    }
	    final UserWrapper user = (UserWrapper)item;
	    	    	    final String scrName = user.user.getScreenName() != null?user.user.getScreenName().trim():"";
	    	    final String location = user.user.getLocation() != null?user.user.getLocation().trim():"";

		    	    	    final String descr = user.user.getDescription() != null?user.user.getDescription().trim():"";
	    final StringBuilder b = new StringBuilder();
	    b.append(app.getLuwrain().getSpeakableText(user.user.getName(), Luwrain.SpeakableTextType.NATURAL));
	    if (!scrName.isEmpty())
		b.append(", at ").append(scrName);
	    if (!location.isEmpty())
		b.append(", ").append(location);
	    	    if (!descr.isEmpty())
		b.append(", ").append(descr);
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
	    return users.length;
	}
	@Override public Object getItem(int index)
	{
	    if (index < 0 || index >= users.length)
		throw new IllegalArgumentException("index (" + index + ") must be greater or equal to zero and less than " + String.valueOf(users.length));
	    return users[index];
	}
    }
}
