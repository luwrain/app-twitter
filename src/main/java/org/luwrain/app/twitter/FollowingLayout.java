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

final class FollowingLayout extends LayoutBase
{
    private final App app;
    final ListArea followingsArea;
    ListArea followersArea;

    private UserWrapper[] followings = new UserWrapper[0];
    private UserWrapper[] followers = new UserWrapper[0];

    FollowingLayout(App app)
    {
	super(app);
	this.app = app;
	setCloseHandler(()-> {app.layouts().main(); return true; });

	{
	    final ListArea.Params params = new ListArea.Params();
	    params.context = getControlContext();
	    params.model = new ListUtils.ArrayModel(()->{ return followings; });
	    params.appearance = new ListUtils.DefaultAppearance(params.context);
	    params.name = "Последователи";
	    params.clickHandler = (area, index, obj)->{
		if (obj == null || !(obj instanceof UserWrapper))
		    return false;
		final UserWrapper user = (UserWrapper)obj;
		return showUserLayout(user.user.getScreenName());
	    };
	    this.followingsArea = new ListArea(params);
	}
	final Actions followingActions = actions(
						 action("delete-following", app.getStrings().actionDeleteFollowing(), new InputEvent(InputEvent.Special.DELETE), FollowingLayout.this::actDeleteFollowing)
						 );

	{
	    final ListArea.Params params = new ListArea.Params();
	    params.context = getControlContext();
	    params.model = new ListUtils.FixedModel();
	    params.appearance = new ListUtils.DefaultAppearance(params.context);
	    params.name = "Другие последователи";
	    this.followersArea = new ListArea(params);
	}
	final Actions followersActions = actions();
	setAreaLayout(AreaLayout.LEFT_RIGHT, followingsArea, followingActions, followersArea, followersActions);
    }

    private boolean actDeleteFollowing()
    {
	final Object obj = followingsArea.selected();
	if (obj == null || !(obj instanceof UserWrapper))
	    return false;
	app.getLuwrain().message(obj.getClass().getName());
	final UserWrapper user = (UserWrapper)obj;
	final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		try {
		    app.getTwitter().destroyFriendship(user.getName());
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
	    });
    }

    boolean updateFollowing()
    {
	if (app.isBusy())
	    return false;
	final AppBase.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final List<User> followingsList;
		try {
		    followingsList = app.getTwitter().getFriendsList(app.getTwitter().getId(), -1, 100);
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->{
			followings = UserWrapper.create(followingsList.toArray(new User[followingsList.size()]));
			followingsArea.refresh();
			app.getLuwrain().setActiveArea(followingsArea);
		    });
	    });
    }

    private boolean showUserLayout(String userName)
    {
	NullCheck.notEmpty(userName, "userName");
	final UserLayout userLayout = new UserLayout(app, userName, ()->{
		app.layouts().custom(this.getAreaLayout());
		app.getLuwrain().setActiveArea(followingsArea);
	    });
	app.layouts().custom(userLayout.getLayout());
	userLayout.update();
	return true;
    }
}
