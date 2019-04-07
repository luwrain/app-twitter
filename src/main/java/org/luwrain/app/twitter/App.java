/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;

final class App implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;

    private ListArea statusArea = null;
    private EditArea postArea = null;
    private AreaLayoutHelper layout = null;

    //For account auth procedure
    private Account accountToAuth = null;
    private AccessTokenForm accessTokenForm;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	  this.strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain, strings);
	this.actions = new Actions(base);
	createAreas();


					final Account account = findInitialAccount();
					if (account != null)
					{
					    actions.activateAccount(account, ()->{
						    statusArea.refresh();
						    luwrain.announceActiveArea();
						});

					    	layout = new AreaLayoutHelper(()->{
					base.setVisibleAreas(layout.getLayout().getAreas());
		luwrain.onNewAreaLayout();
		luwrain.announceActiveArea();
	    }, new AreaLayout(AreaLayout.TOP_BOTTOM, statusArea, postArea));


					} else
					{

					    	layout = new AreaLayoutHelper(()->{
					base.setVisibleAreas(layout.getLayout().getAreas());
		luwrain.onNewAreaLayout();
		luwrain.announceActiveArea();
	    }, new AreaLayout(accessTokenForm));

						
					}

									base.setVisibleAreas(layout.getLayout().getAreas());


	return new InitResult();
    }


    private void createAreas()
    {
	final ListArea.Params statusParams = new ListArea.Params();
	statusParams.context = new DefaultControlContext(luwrain);
	statusParams.model = base.statusModel;
	statusParams.appearance = new ListUtils.DefaultAppearance(statusParams.context);
	statusParams.name = strings.statusAreaName();
	this.statusArea = new ListArea(statusParams){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() &&! event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    /*
			    if (!layout.hasAdditionalArea())
				return false;
			    luwrain.setActiveArea(layout.getAdditionalArea());
			    */
			    luwrain.setActiveArea(postArea);
			    return true;
			case ESCAPE:
			    closeApp();
			    return true;
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch (event.getCode())
		    {
		    case ACTION:
			if (ActionEvent.isAction(event, "retweet"))
			{
			    final Object obj = selected();
			    if (obj == null || !(obj instanceof Tweet))
				return false;
			    return actions.onRetweetStatus((Tweet)obj, ()->statusArea.refresh());
			}
			if (ActionEvent.isAction(event, "like"))
			{
			    final Object obj = selected();
			    if (obj == null || !(obj instanceof Tweet))
				return false;
			    return actions.onCreateFavourite((Tweet)obj, ()->statusArea.refresh());
			}
			if (ActionEvent.isAction(event, "delete-tweet"))
			{
			    final Object obj = selected();
			    if (obj == null || !(obj instanceof Tweet))
				return false;
			    return actions.onDestroyStatus((Tweet)obj, ()->statusArea.refresh());
			}
			if (ActionEvent.isAction(event, "user-timeline"))
			    return actions.onShowUserTimeline((pager)->{showTweetsArea("FIXME", (TweetsPager)pager);});
			if (ActionEvent.isAction(event, "show-friends"))
			    return onShowFriends();
			if (ActionEvent.isAction(event, "show-likes"))
			    return onShowLikes();
			if (ActionEvent.isAction(event, "search"))
			    return actions.search(/*App.this*/null);
			if (ActionEvent.isAction(event, "change-account"))
			    return onChangeAccount();
			return false;
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
		    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	switch(query.getQueryCode())
	{
	case AreaQuery.BACKGROUND_SOUND:
	    if (base.isBusy())
	    {
		((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.FETCHING));
		return true;
	    }
	    return false;
	default:
	    return super.onAreaQuery(query);
	}
    }
		@Override public Action[] getAreaActions()
		{
		    if (!base.isAccountActivated())
			return new Action[0];
		    return ActionLists.getHomeTimelineActions(true);
		}
	    };

	final EditArea.Params postParams = new EditArea.Params();
	postParams.context = new DefaultControlContext(luwrain);
	postParams.name = "Новый твит";
	this.postArea = new EditArea(postParams){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial())
			switch(event.getSpecial())
			{
			case TAB:
			    luwrain.setActiveArea(statusArea);
			    return true;
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case OK:
			return actions.onUpdateStatus(getLines(), ()->statusArea.refresh());
		    case CLOSE:
			closeApp();
			return true;
		    default:
		    return super.onSystemEvent(event);
		    }
		}
	    };

	accessTokenForm = new AccessTokenForm(luwrain, this, strings, base) {
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
    }

    private Account findInitialAccount()
    {
		final Settings.Main sett = Settings.createMain(luwrain.getRegistry());
	final String defaultAccountName = sett.getDefaultAccount("");
	if (!defaultAccountName.trim().isEmpty())
	{
	    final Account defaultAccount = base.findAccount(base.getAccounts(), defaultAccountName);
	    if (defaultAccount != null && defaultAccount.isReadyToConnect())
		return defaultAccount;
	}
	final Account[] accounts = base.getAccounts();
	for(Account a: accounts)
	    if (a.isReadyToConnect())
		return a;
	return null;
    }


    private boolean onChangeAccount()
    {
	final Account newAccount = actions.conv.chooseAnotherAccount();
	if (newAccount == null)
	    return true;
	if (base.isAccountActivated())
	    base.closeAccount();
	actions.activateAccount(newAccount, ()->{
		statusArea.refresh();
		statusArea.reset(false);
	    });
	return true;
    }

	    void showTweetsArea(String title, TweetsPager pager)
    {
	NullCheck.notEmpty(title, "title");
	NullCheck.notNull(pager, "pager");
	final ListArea.Params tweetsParams = new ListArea.Params();
	tweetsParams.context = new DefaultControlEnvironment(luwrain);
	tweetsParams.model = new ListUtils.FixedModel(pager.getTweets());
	tweetsParams.appearance = new TweetsAppearance(luwrain, strings);
	tweetsParams.name = title;
	final ListArea area = new ListArea(tweetsParams) {
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    layout.closeTempLayout();
			    return true;
			case TAB:
			    luwrain.setActiveArea(statusArea);
			    return true;
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case ACTION:
			if (ActionEvent.isAction(event, "follow-author"))
			    return actions.onFollowAuthor(this);
			if (ActionEvent.isAction(event, "search"))
			    return actions.search(/*App.this*/null);
			if (ActionEvent.isAction(event, "user-timeline"))
			    return actions.onShowUserTimeline((pager)->{/*FIXME*/});
			return false;
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public Action[] getAreaActions()
		{
		    if (!base.isAccountActivated())
			return new Action[0];
		    return ActionLists.getTweetsActions();
		}
	    };
	layout.openTempLayout(new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, statusArea, postArea, area));
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
		@Override public boolean onInputEvent(KeyboardEvent event)
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
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
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
			return super.onSystemEvent(event);
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

    private boolean onShowLikes()
    {
	final List<Status> likes;
	try {
	    likes = (List)base.call(()->base.getTwitter().getFavorites(new Paging()));
	}
	catch(java.util.concurrent.ExecutionException e)
	{
	    luwrain.crash(e);
	    return true;
	}
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = new ListUtils.FixedModel(Tweet.create(likes)){
		@Override public void refresh()
		{

	final List<Status> l;
	try {
	    l = (List)base.call(()->base.getTwitter().getFavorites(new Paging()));
	}
	catch(java.util.concurrent.ExecutionException e)
	{
	    luwrain.crash(e);
	    return;
	}
	final List<Tweet> wrappers = new LinkedList();
	for(Status s: l)
	    wrappers.add(new Tweet(s));
	clear();
	addAll(wrappers);




		}
	    };
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "Лайки";//FIXME:
	final ListArea area = new ListArea(params) {
		@Override public boolean onInputEvent(KeyboardEvent event)
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
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
case ACTION:
    if (ActionEvent.isAction(event, "cancel-like"))
	return actions.onDeleteLike(this);
    return false;
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public Action[] getAreaActions()
		{
return ActionLists.getLikesActions(selected());
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
		luwrain.message(errorMsg, Luwrain.MessageType.ERROR);
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
	luwrain.message(strings.accountAuthCompleted (), Luwrain.MessageType.OK);
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
