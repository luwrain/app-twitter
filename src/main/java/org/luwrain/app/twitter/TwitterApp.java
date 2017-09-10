/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import org.luwrain.popups.*;

class TwitterApp implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;

    private StatusArea statusArea;
    private AreaLayoutHelper layout = null;

    //For account auth procedure
    private Account accountToAuth = null;
    private AccessTokenForm accessTokenForm;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	/*
	  final Object o = luwrain.i18n().getStrings(Strings.NAME);
	  if (o == null || !(o instanceof Strings))
	  return false;
	  strings = (Strings)o;
	*/
	strings = new Strings();
	this.luwrain = luwrain;
	this.base = new Base(luwrain);
	this.actions = new Actions(luwrain, base, strings);
	createAreas();
	layout = new AreaLayoutHelper(()->{
		luwrain.onNewAreaLayout();
		luwrain.announceActiveArea();
	    }, statusArea);
	final Settings.Main sett = Settings.createMain(luwrain.getRegistry());
	final String defaultAccountName = sett.getDefaultAccount("");
	if (!defaultAccountName.trim().isEmpty())
	{
	    final Account defaultAccount = base.findAccount(base.getAccounts(), defaultAccountName);
	    if (defaultAccount != null && defaultAccount.isReadyToConnect())
		actions.activateAccount(statusArea, defaultAccount); else
		tryToConnectFirstAccount();
	} else
	    tryToConnectFirstAccount();
	return new InitResult();
    }

    private void tryToConnectFirstAccount()
    {
	final Account[] accounts = base.getAccounts();
	for(Account a: accounts)
	    if (a.isReadyToConnect() && actions.activateAccount(statusArea, a))
		return;
    }

    private void createAreas()
    {
	final ConsoleArea2.ClickHandler clickHandler = (area,index,obj)->{
	    return false;
	};
	final ConsoleArea2.InputHandler inputHandler = (area,text)->actions.onUpdateStatus(text, area);
	statusArea = new StatusArea(new DefaultControlEnvironment(luwrain), base.statusModel, clickHandler, inputHandler) {
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() &&! event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    if (!layout.hasAdditionalArea())
				return false;
			    luwrain.setActiveArea(layout.getAdditionalArea());
			    return true;
			case ESCAPE:
			    closeApp();
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
		    case ACTION:
			if (ActionEvent.isAction(event, "retweet"))
			{
			    final Object obj = selected();
			    if (obj == null || !(obj instanceof TweetWrapper))
				return false;
			    return actions.onRetweetStatus((TweetWrapper)obj, this);
			}
			if (ActionEvent.isAction(event, "like"))
			{
			    final Object obj = selected();
			    if (obj == null || !(obj instanceof TweetWrapper))
				return false;
			    return actions.onCreateFavourite((TweetWrapper)obj, this);
			}
			if (ActionEvent.isAction(event, "delete-tweet"))
			{
			    final Object obj = selected();
			    if (obj == null || !(obj instanceof TweetWrapper))
				return false;
			    return actions.onDestroyStatus((TweetWrapper)obj, this);
			}
			if (ActionEvent.isAction(event, "user-timeline"))
			    return actions.onShowUserTimeline(TwitterApp.this);
			if (ActionEvent.isAction(event, "show-friends"))
			    return onShowFriends();
			if (ActionEvent.isAction(event, "search"))
			    return actions.onSearch(TwitterApp.this);
			if (ActionEvent.isAction(event, "change-account"))
			    return onChangeAccount();
			return false;
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public Action[] getAreaActions()
		{
		    if (!base.isAccountActivated())
			return new Action[0];
		    return ActionLists.getHomeTimelineActions(true);
		}
	    };

	accessTokenForm = new AccessTokenForm(luwrain, this, strings, base) {
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onEnvironmentEvent(event);
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
    }

    private boolean onChangeAccount()
    {
	final Account newAccount = actions.conv.chooseAnotherAccount();
	if (newAccount == null)
	    return true;
	if (base.isAccountActivated())
	    base.closeAccount();
	actions.activateAccount(statusArea, newAccount);
	return true;
    }

    void showTweetsArea(String title, TweetWrapper[] wrappers)
    {
	NullCheck.notEmpty(title, "title");
	NullCheck.notNullItems(wrappers, "wrappers");
	final ListArea.Params tweetsParams = new ListArea.Params();
	tweetsParams.context = new DefaultControlEnvironment(luwrain);
	tweetsParams.model = new ListUtils.FixedModel(wrappers);
	tweetsParams.appearance = new TweetsAppearance(luwrain, strings);
	tweetsParams.name = title;
	final ListArea area = new ListArea(tweetsParams) {
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    layout.closeAdditionalArea();
			    return true;
			case TAB:
			    luwrain.setActiveArea(statusArea);
			    return true;
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onEnvironmentEvent(event);
		    switch(event.getCode())
		    {
		    case ACTION:
			if (ActionEvent.isAction(event, "follow-author"))
			    return actions.onFollowAuthor(this);
			if (ActionEvent.isAction(event, "search"))
			    return actions.onSearch(TwitterApp.this);
			if (ActionEvent.isAction(event, "user-timeline"))
			    return actions.onShowUserTimeline(TwitterApp.this);
			return false;
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public Action[] getAreaActions()
		{
		    if (!base.isAccountActivated())
			return new Action[0];
		    return ActionLists.getTweetsActions();
		}
	    };
	layout.openAdditionalArea(area, AreaLayoutHelper.Position.BOTTOM);
	luwrain.setActiveArea(area);
    }

    private boolean onShowFriends()
    {
	final List<User> friends;
	try {
	    friends = (List)base.call(()->base.getTwitter().getFriendsList(base.getTwitter().getId(), -1));
	}
	catch(java.util.concurrent.ExecutionException e)
	{
	    luwrain.crash(e);
	    return true;
	}
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = new ListUtils.FixedModel(UserWrapper.create(friends)){
		@Override public void refresh()
		{
		    //FIXME:
		}
	    };
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "Друзья";//FIXME:
	final ListArea area = new ListArea(params) {
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    layout.closeAdditionalArea();
			    return true;
			case TAB:
			case BACKSPACE:
			    luwrain.setActiveArea(statusArea);
			    return true;
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onEnvironmentEvent(event);
		    switch(event.getCode())
		    {
		    case ACTION:
			if (ActionEvent.isAction(event, "delete-friendship"))
			    return actions.onDeleteFriendship(this);
			return false;
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public Action[] getAreaActions()
		{
		    return ActionLists.getFriendsActions(selected());
		}
	    };
	layout.openAdditionalArea(area, AreaLayoutHelper.Position.BOTTOM);
	luwrain.setActiveArea(area);
	return true;
    }

    private boolean startAccountAuth(Account account)
    {
	NullCheck.notNull(account, "account");
	if (!Popups.confirmDefaultYes(luwrain, strings.accountAuthPopupName(), strings.accountAuthPopupText(account.name)))
	    return true;
	accountToAuth = account;
	accessTokenForm.reset();
	layout.openTempArea(accessTokenForm);
	return true;
    }

    void endAccountAuth(boolean success, String errorMsg,
			String accessToken, String accessTokenSecret)
    {
	layout.closeTempLayout();
	if (!success)
	{
	    if (errorMsg != null && !errorMsg.isEmpty())
		luwrain.message(errorMsg, Luwrain.MESSAGE_ERROR);
	    return;
	}
	if (accountToAuth == null)
	    return;
	NullCheck.notNull(accessToken, "accessToken");
	NullCheck.notNull(accessTokenSecret, "accessTokenSecret");
	accountToAuth.accessToken = accessToken;
	accountToAuth.accessTokenSecret = accessTokenSecret;
	accountToAuth.sett.setAccessToken(accountToAuth.accessToken);
	accountToAuth.sett.setAccessTokenSecret(accountToAuth.accessTokenSecret);
	luwrain.message(strings.accountAuthCompleted (), Luwrain.MESSAGE_OK);
    }

    @Override public void closeApp()
    {
	if (base.isBusy())
	    return;
	luwrain.closeApp();
    }

    @Override public AreaLayout getAreaLayout()
    {
	return layout.getLayout();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }
}
