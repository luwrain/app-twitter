
package org.luwrain.app.twitter;

import java.util.*;
import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.template.*;

final class MainLayout extends LayoutBase
{
    private final App2 app;
    private final ListArea statusArea;
    private final EditArea postArea;

    private Tweet[] homeTimeline = new Tweet[0];

    MainLayout(App2 app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.statusArea = new ListArea(createStatusListParams()){
		private final Actions actions = actions(
							action("following", "Подписки и подписчики", new KeyboardEvent(KeyboardEvent.Special.F5), MainLayout.this::actFollowing),
							action("delete-tweet", app.getStrings().actionDeleteTweet(), new KeyboardEvent(KeyboardEvent.Special.DELETE), MainLayout.this::actDelete)
							);
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
	this.postArea = new EditArea(createPostEditParams()){
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
		    if (event.getType() == EnvironmentEvent.Type.REGULAR)
			switch(event.getCode())
			{
			case OK:
			    return actPost();
			}
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

    boolean updateHomeTimelineBkg()
    {
	if (app.isBusy())
	    return false;
	final AppBase.TaskId taskId = app.newTaskId();
	return app.runTask(()->{
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
	final String text = makeTweetText(postArea.getLines());
	if (text.isEmpty())
	    return false;
	final App2.TaskId taskId = app.newTaskId();
	return app.runTask(()->{
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
			app.getLuwrain().playSound(Sounds.DONE);
		    });
	    });
	    }

        private String makeTweetText(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	if (lines.length == 0)
	    return "";
	final List<String> validLines = new LinkedList();
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
	final App2.TaskId taskId = app.newTaskId();
	return app.runTask(()->{
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


    private boolean actFollowing()
    {
	app.layouts().following();
	return true;
    }

    private ListArea.Params createStatusListParams()
    {
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.model = new StatusModel();
	params.appearance = new ListUtils.DefaultAppearance(params.context){
		@Override public void announceItem(Object obj, Set<Flags> flags)
		{
		    NullCheck.notNull(obj, "obj");
		    app.getLuwrain().setEventResponse(DefaultEventResponse.listItem(app.getLuwrain().getSpeakableText(obj.toString(), Luwrain.SpeakableTextType.NATURAL)));
		}
	    };
	params.name = app.getStrings().statusAreaName();
	return params;
    }

    private EditArea.Params createPostEditParams()
    {
	final EditArea.Params params = new EditArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.appearance = new EditUtils.DefaultEditAreaAppearance(params.context);
	params.name = app.getStrings().postAreaName();
	return params;
    }

    AreaLayout getLayout()
    {
	return new AreaLayout(AreaLayout.TOP_BOTTOM, statusArea, postArea);
    }

    private class StatusModel implements ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return homeTimeline.length;
	}
	@Override public Object getItem(int index)
	{
	    return homeTimeline[index];
	}
	@Override public void refresh()
	{
	}
    }
}
