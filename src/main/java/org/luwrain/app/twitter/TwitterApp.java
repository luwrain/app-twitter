/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.twitter;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;

import twitter4j.*;

class TwitterApp implements Application, Actions
{
    public static final String STRINGS_NAME = "luwrain.twitter";

    private Luwrain luwrain;
    private Strings strings;
    private Base base = new Base();
    Twitter twitter = null;

    private SectionsModel sectionsModel;
    private TweetsModel tweetsModel;
    private ListArea sectionsArea;
    private ListArea tweetsArea;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	createAreas();
	return true;
    }

    @Override public void search()
    {
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final String query = Popups.simple(luwrain, strings.searchPopupName(), strings.searchPopupPrefix(), "");
	if (query == null || query.trim().isEmpty())
	    return;
	TweetWrapper[] wrappers = base.search(twitter, query, 10);
	if (wrappers == null)
	{
	    luwrain.message(strings.problemSearching(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	if (wrappers.length < 0)
	{
	    luwrain.message(strings.nothingFound(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	tweetsModel.setTweets(wrappers);
	tweetsArea.refresh();
	luwrain.setActiveArea(tweetsArea);
    }

    @Override public void userTweets()
    {
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final String user = Popups.simple(luwrain, strings.userTweetsPopupName(), strings.userTweetsPopupPrefix(), "");
	if (user == null || user.trim().isEmpty())
	    return;
	TweetWrapper[] wrappers = base.userTweets(twitter, user);
	if (wrappers == null)
	{
	    luwrain.message(strings.problemUserTweets(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	if (wrappers.length < 0)
	{
	    luwrain.message(strings.noUserTweets(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	tweetsModel.setTweets(wrappers);
	tweetsArea.refresh();
	luwrain.setActiveArea(tweetsArea);
    }


    @Override public void post()
    {
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final String text = Popups.simple(luwrain, strings.postPopupName(), strings.postPopupPrefix(), "");
	if (text == null || text.trim().isEmpty())
	    return;
	if (base.postTweet(twitter, text))
	{
	    luwrain.message(strings.postingSuccess(), Luwrain.MESSAGE_OK);
	} else 
	{
	    luwrain.message(strings.problemPosting(), Luwrain.MESSAGE_ERROR);
	}
    }

    @Override public void homeTweets()
    {
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	TweetWrapper[] wrappers = base.homeTweets(twitter);
	if (wrappers == null)
	{
	    luwrain.message(strings.problemHomeTweets(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	tweetsModel.setTweets(wrappers);
	tweetsArea.refresh();
	luwrain.setActiveArea(tweetsArea);
    }

    @Override public void activateAccount(Account account)
    {
	twitter = base.createTwitter(account.consumerKey,
				     account.consumerSecret,
				     account.accessToken,
				     account.accessTokenSecret);
	if (twitter != null)
	{
	    luwrain.playSound(Sounds.GENERAL_OK);
	} else
	{
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
				    new DefaultListItemAppearance(new DefaultControlEnvironment(luwrain)),
				    sectionsClickHandler,
				    strings.appName()) {
		private Strings strings = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isCommand() &&! event.isModified())
			switch(event.getCommand())
			{
			case KeyboardEvent.TAB:
			    actions.gotoTweets();
			    return super.onKeyboardEvent(event);
			default:
			    return super.onKeyboardEvent(event);
			}
		    return false;
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch (event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
			return true;
		    default:
			return false;
		    }
		}
	    };

	tweetsArea = new ListArea(new DefaultControlEnvironment(luwrain), 
				    tweetsModel,
				    new DefaultListItemAppearance(new DefaultControlEnvironment(luwrain)),
				  tweetsClickHandler,
				  "Твиты") { //FIXME:
		private Strings strings = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isCommand() &&! event.isModified())
			switch(event.getCommand())
			{
			case KeyboardEvent.TAB:
			    actions.gotoSections();
			    return true;
			default:
			    return super.onKeyboardEvent(event);
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch (event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
			return true;
		    default:
			return false;
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
	luwrain.closeApp();
    }
}
