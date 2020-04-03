
package org.luwrain.app.twitter;

import java.util.*;
import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.template.*;

final class FollowingLayout extends LayoutBase
{
    private final App2 app;
    private final ListArea followingsArea;
    private ListArea followersArea;

    private UserWrapper[] followings = new UserWrapper[0];
    private UserWrapper[] followers = new UserWrapper[0];

    FollowingLayout(App2 app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.followingsArea = new ListArea(createFollowingsAreaParams()) {
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
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
	this.followersArea = new ListArea(createFollowersAreaParams()) {
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
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
    }

    boolean updateFollowing()
    {
	if (app.isBusy())
	    return false;
	final AppBase.TaskId taskId = app.newTaskId();
	return app.runTask(()->{
		final List<User> followingsList;
		try {
		    followingsList = app.getTwitter().getFriendsList(app.getTwitter().getId(), -1);
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

    private ListArea.Params createFollowingsAreaParams()
    {
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.model = new FollowingsModel();
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "Последователи";
	return params;
    }

    private ListArea.Params createFollowersAreaParams()
    {
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.model = new ListUtils.FixedModel();
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "Другие последователи";
	return params;
    }

    AreaLayout getLayout()
    {
	return new AreaLayout(AreaLayout.LEFT_RIGHT, followingsArea, followersArea);
    }

    private final class FollowingsModel implements ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return followings.length;
	}
	@Override public Object getItem(int index)
	{
	    return followings[index];
	}
	@Override public void refresh()
	{
	}
    }

    private final class Followers implements ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return followers.length;
	}
	@Override public Object getItem(int index)
	{
	    return followers[index];
	}
	@Override public void refresh()
	{
	}
    }
}
