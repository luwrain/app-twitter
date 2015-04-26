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

    @Override public void activateAccount(Account account)
    {
	twitter = base.createTwitter(account.consumerKey,
				     account.consumerSecret,
				     account.accessToken,
				     account.accessTokenSecret);


	if (twitter == null)
	{
	    luwrain.message("Проблемка", Luwrain.MESSAGE_ERROR);
	    return;
	}
	TweetWrapper[] wrappers = base.search(twitter, "", 10);
	tweetsModel.setTweets(wrappers);
	tweetsArea.refresh();
    }

    private void createAreas()
    {
	final Actions a = this;
	final Strings s = strings;

	sectionsModel = new SectionsModel(luwrain);
	tweetsModel = new TweetsModel(luwrain);

	final ListClickHandler sectionsClickHandler = new ListClickHandler(){
		private Actions actions = a;
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
		    if (index < 0 || item == null)
			return false;
		    if (item instanceof Account)
		    {
			actions.activateAccount((Account)item);
			return true;
		    }
		    return false;
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
