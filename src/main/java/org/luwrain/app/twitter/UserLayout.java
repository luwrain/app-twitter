
package org.luwrain.app.twitter;

import java.util.*;
import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.template.*;

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
	this.tweetsArea = new ListArea(createTweetsAreaParams()) {
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
		@Override public Action[] getAreaActions()
		{
		    return new Action[0];
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
	final List<Tweet> tweets = new LinkedList();
	for(Status s: result)
	    tweets.add(new Tweet(s));
	return tweets.toArray(new Tweet[tweets.size()]);
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
