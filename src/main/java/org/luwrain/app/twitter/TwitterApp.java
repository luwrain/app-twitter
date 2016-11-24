
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
    private Strings strings;
    private final Base base = new Base();
    private Actions actions = null;
    Twitter twitter = null;

    private SectionsModel sectionsModel;
    private TweetsModel tweetsModel;
    private ListArea sectionsArea;
    private ListArea tweetsArea;
    private Work work = null;
    private String[] allowedAccounts;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	this.actions = new Actions(luwrain);
	createAreas();
	allowedAccounts = allowedAccounts();
	return true;
    }

    private void search()
    {
	if (work != null && !work.finished)
	    return;
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final String query = Popups.simple(luwrain, strings.searchPopupName(), strings.searchPopupPrefix(), "");
	if (query == null || query.trim().isEmpty())
	    return;
	final Strings s = strings;
	work = new Work(luwrain, tweetsArea){
		private Strings strings = s;
		@Override public void work()
		{
		    TweetWrapper[] wrappers = base.search(twitter, query, 10);
		    if (wrappers == null)
		    {
			message(strings.problemSearching(), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    if (wrappers.length < 0)
		    {
			message(strings.nothingFound(), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    showTweets(wrappers);
		}
	    };
	new Thread(work).start();
    }

    private void userTweets()
    {
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
    }

    private void post()
    {
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
    }

    private void homeTweets()
    {
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
    }

    private void activateAccount(Account account)
    {
	if (work != null && !work.finished)
	    return;
	twitter = base.createTwitter(account.consumerKey,
				     account.consumerSecret,
				     account.accessToken,
				     account.accessTokenSecret);
	if (twitter != null)
	{
	    luwrain.playSound(Sounds.DONE);
	    sectionsModel.setActiveAccount(account);
	} else
	{
	    sectionsModel.noActiveAccount();
	    luwrain.message(strings.problemConnecting(), Luwrain.MESSAGE_ERROR);
	}
    }

    private void createAreas()
    {

	sectionsModel = new SectionsModel(luwrain, strings);
	tweetsModel = new TweetsModel(luwrain);

	final ListClickHandler sectionsClickHandler = new ListClickHandler(){
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
		    if (index < 0 || item == null)
			return false;
		    switch (index)
		    {
		    case 0:
			search();
			return true;
		    case 1:
			userTweets();
			return true;
		    case 2:
			homeTweets();
			return true;
		    case 3:
			post();
			return true;
		    default:
			if (item instanceof Account)
			{
			    activateAccount((Account)item);
			    return true;
			}
			return false;
		    }
		}
	    };

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
	sectionsParams.model = sectionsModel;
	sectionsParams.appearance = new SectionsAppearance(luwrain, strings);
	sectionsParams.clickHandler = sectionsClickHandler;
	sectionsParams.name = strings.appName();

	sectionsArea = new ListArea(sectionsParams) {
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() &&! event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    gotoTweets();
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

	tweetsArea = new ListArea(tweetsParams) {
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
		    switch (event.getCode())
		    {
		    case THREAD_SYNC:
			if (event instanceof MessageEvent)
			{
			    final MessageEvent messageEvent = (MessageEvent)event;
			    luwrain.message(messageEvent.text, messageEvent.type);
			    return true;
			}
			if (event instanceof ShowTweetsEvent)
			{
			    final ShowTweetsEvent showTweetsEvent = (ShowTweetsEvent)event;
			    final TweetsModel tweetsModel = (TweetsModel)model();
			    tweetsModel.setTweets(showTweetsEvent.tweets);
			    tweetsArea.reset(false);
gotoTweets();
			    refresh();
			    return true;
			}
			return true;
		    case CLOSE:
closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

private void gotoSections()
    {
	luwrain.setActiveArea(sectionsArea);
    }

    private void gotoTweets()
    {
	luwrain.setActiveArea(tweetsArea);
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_RIGHT, sectionsArea, tweetsArea);
    }

    private void closeApp()
    {
	if (work != null && !work.finished)
	    return;
	luwrain.closeApp();
    }

    private String[] allowedAccounts()
    {
    RegistryAutoCheck check = new RegistryAutoCheck(luwrain.getRegistry());
    final String value = check.stringAny("/org/luwrain/app/twitter/allowed-accounts", "");
    if (value.trim().isEmpty())
	return null;
    return value.split(":");
    }
}
