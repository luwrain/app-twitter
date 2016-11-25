
package org.luwrain.app.twitter;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.util.RegistryAutoCheck;

import twitter4j.*;

class TwitterApp implements Application
{
    private Luwrain luwrain;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;
    Twitter twitter = null;

    private TweetsModel tweetsModel;
    private ListArea sectionsArea;
    private StatusArea statusArea;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	this.actions = new Actions(luwrain, strings);
	this.base = new Base(luwrain);
	createAreas();
	return true;
    }


    private void userTweets()
    {
	/*
	if (work != null && !work.finished)
	    return;
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final String user = Popups.simple(luwrain, strings.userTweetsPopupName(), strings.userTweetsPopupPrefix(), "");
	if (user == null || user.trim().isEmpty())
	    return;
	if (allowedAccounts != null && allowedAccounts.length > 0)
	{
	    boolean permitted = false;
	    for(String s: allowedAccounts)
		if (s.toLowerCase().equals(user.toLowerCase()))
		    permitted = true;
	    if (!permitted)
	    {
		luwrain.message(strings.problemUserTweets(), Luwrain.MESSAGE_ERROR);
		return;
	    }
	}
	final Strings s = strings;
	work = new Work(luwrain, tweetsArea){
		private Strings strings = s;
		@Override public void work()
		{
		    TweetWrapper[] wrappers = base.userTweets(twitter, user);
		    if (wrappers == null)
		    {
			message(strings.problemUserTweets(), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    if (wrappers.length < 0)
		    {
			message(strings.noUserTweets(), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    showTweets(wrappers);
		}
	    };
	new Thread(work).start();
	*/
    }

    private void post()
    {
	/*
	if (work != null && !work.finished)
	    return;
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final String text = Popups.simple(luwrain, strings.postPopupName(), strings.postPopupPrefix(), "");
	if (text == null || text.trim().isEmpty())
	    return;
	final Strings s = strings;
	work = new Work(luwrain, tweetsArea){
		private Strings strings = s;
		@Override public void work()
		{
		    if (base.postTweet(twitter, text))
		    {
			message(strings.postingSuccess(), Luwrain.MESSAGE_DONE);
		    } else 
		    {
			message(strings.problemPosting(), Luwrain.MESSAGE_ERROR);
		    }
		}
	    };
	new Thread(work).start();
	*/
    }

    private void homeTweets()
    {
	/*
	if (work != null && !work.finished)
	    return;
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final Strings s = strings;
	work = new Work(luwrain, tweetsArea){
		private Strings strings = s;
		@Override public void work()
		{
		    TweetWrapper[] wrappers = base.homeTweets(twitter);
		    if (wrappers == null)
		    {
			message(strings.problemHomeTweets(), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    showTweets(wrappers);
		}
	    };
	new Thread(work).start();
	*/
    }

    private void createAreas()
    {

	tweetsModel = new TweetsModel(luwrain);

	final ListClickHandler tweetsClickHandler = new ListClickHandler(){
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
		    return false;
		}
	    };

	final ListArea.Params sectionsParams = new ListArea.Params();
	sectionsParams.environment = new DefaultControlEnvironment(luwrain);
	sectionsParams.model = new FixedListModel(base.getAccounts());
	sectionsParams.appearance = new SectionsAppearance(luwrain, strings);
	sectionsParams.name = strings.appName();

	sectionsArea = new ListArea(sectionsParams) {
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() &&! event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    gotoStatus();
			    return super.onKeyboardEvent(event);
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch (event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };

	final ListArea.Params tweetsParams = new ListArea.Params();
	tweetsParams.environment = new DefaultControlEnvironment(luwrain);
	tweetsParams.model = tweetsModel;
	tweetsParams.appearance = new TweetsAppearance(luwrain, strings);
	tweetsParams.clickHandler = tweetsClickHandler;
	tweetsParams.name = strings.tweetsAreaName();

	statusArea = new StatusArea(new DefaultControlEnvironment(luwrain)) {

		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() &&! event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
gotoSections();
			    return true;
			}
			    return super.onKeyboardEvent(event);
		}

		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onEnvironmentEvent(event);
		    switch (event.getCode())
		    {
		    case CLOSE:
closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}

	    };

	sectionsArea.setClickHandler((area, index, obj)->actions.onAccountsClick(base, statusArea, obj));
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

private void gotoSections()
    {
	luwrain.setActiveArea(sectionsArea);
    }

    private void gotoStatus()
    {
	luwrain.setActiveArea(statusArea);
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_RIGHT, sectionsArea, statusArea);
    }

    private void closeApp()
    {
	if (base.isBusy())
	    return;
	luwrain.closeApp();
    }
}
