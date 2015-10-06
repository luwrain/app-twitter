/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.util.RegistryAutoCheck;

import twitter4j.*;

class TwitterApp implements Application, Actions
{
    static private final String STRINGS_NAME = "luwrain.twitter";

    private Luwrain luwrain;
    private Strings strings;
    private final Base base = new Base();
    Twitter twitter = null;

    private SectionsModel sectionsModel;
    private TweetsModel tweetsModel;
    private ListArea sectionsArea;
    private ListArea tweetsArea;
    private Work work = null;
    private String[] allowedAccounts;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	createAreas();
	allowedAccounts = allowedAccounts();
	return true;
    }

    @Override public void search()
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

    @Override public void userTweets()
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

    @Override public void post()
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

    @Override public void homeTweets()
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

    @Override public void activateAccount(Account account)
    {
	if (work != null && !work.finished)
	    return;
	twitter = base.createTwitter(account.consumerKey,
				     account.consumerSecret,
				     account.accessToken,
				     account.accessTokenSecret);
	if (twitter != null)
	{
	    luwrain.playSound(Sounds.MESSAGE_DONE);
	    sectionsModel.setActiveAccount(account);
	} else
	{
	    sectionsModel.noActiveAccount();
	    luwrain.message(strings.problemConnecting(), Luwrain.MESSAGE_ERROR);
	}
    }

    private void createAreas()
    {
	final Actions a = this;
	final Strings s = strings;

	sectionsModel = new SectionsModel(luwrain, strings);
	tweetsModel = new TweetsModel(luwrain);

	final ListClickHandler sectionsClickHandler = new ListClickHandler(){
		private Actions actions = a;
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
		    if (index < 0 || item == null)
			return false;
		    switch (index)
		    {
		    case 0:
			actions.search();
			return true;
		    case 1:
			actions.userTweets();
			return true;
		    case 2:
			actions.homeTweets();
			return true;
		    case 3:
			actions.post();
			return true;
		    default:
			if (item instanceof Account)
			{
			    actions.activateAccount((Account)item);
			    return true;
			}
			return false;
		    }
		}
	    };

	final ListClickHandler tweetsClickHandler = new ListClickHandler(){
		private Actions actions = a;
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
		    return false;
		}
	    };

	sectionsArea = new ListArea(new DefaultControlEnvironment(luwrain), 
				    sectionsModel,
				    new SectionsAppearance(luwrain, strings),
				    sectionsClickHandler,
				    strings.appName()) {
		private Strings strings = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isCommand() &&! event.isModified())
			switch(event.getCommand())
			{
			case KeyboardEvent.TAB:
			    actions.gotoTweets();
			    return super.onKeyboardEvent(event);
			}
			    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch (event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };

	tweetsArea = new ListArea(new DefaultControlEnvironment(luwrain), 
				    tweetsModel,
				  new TweetsAppearance(luwrain, strings),
				  tweetsClickHandler,
				  strings.tweetsAreaName()) {
		private Strings strings = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isCommand() &&! event.isModified())
			switch(event.getCommand())
			{
			case KeyboardEvent.TAB:
			    actions.gotoSections();
			    return true;
			}
			    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch (event.getCode())
		    {
		    case EnvironmentEvent.THREAD_SYNC:
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
			    tweetsArea.resetState(false);
			    actions.gotoTweets();
			    refresh();
			    return true;
			}
			return true;
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
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

    @Override public void gotoSections()
    {
	luwrain.setActiveArea(sectionsArea);
    }

    @Override public void gotoTweets()
    {
	luwrain.setActiveArea(tweetsArea);
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_RIGHT, sectionsArea, tweetsArea);
    }

    @Override public void closeApp()
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
