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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;

class TwitterApp implements Application
{
    static final int INITIAL_LAYOUT_INDEX = 0;
    static final int TWEETS_LAYOUT_INDEX = 1;
    static final int ACCOUNTS_LAYOUT_INDEX = 2;
    static final int ACCESS_TOKEN_FORM_LAYOUT_INDEX = 3;

    private Luwrain luwrain;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;

    private AreaLayoutSwitch layouts;
    private ListArea accountsArea;
    private ListArea tweetsArea;
    private StatusArea statusArea;
    private int defaultLayoutIndex = -1;

    //For account auth procedure
    private Account accountToAuth = null;
    private AccessTokenForm accessTokenForm;

    @Override public boolean onLaunch(Luwrain luwrain)
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
	this.actions = new Actions(luwrain, strings);
	this.base = new Base(luwrain);
	createAreas();
	layouts = new AreaLayoutSwitch(luwrain);
	layouts.add(new AreaLayout(statusArea));
	layouts.add(new AreaLayout(tweetsArea));
	layouts.add(new AreaLayout(AreaLayout.LEFT_RIGHT, accountsArea, statusArea));
	layouts.add(new AreaLayout(accessTokenForm));

	final Settings.Main sett = Settings.createMain(luwrain.getRegistry());
	final String defaultAccountName = sett.getDefaultAccount("");
	if (!defaultAccountName.isEmpty())
	{
	final Account defaultAccount = findAccount(accountsArea.getListModel(), defaultAccountName);
	if (defaultAccount == null || !actions.onAccountsClick(base, statusArea, defaultAccount))
	    layouts.show(ACCOUNTS_LAYOUT_INDEX);
	} else
	    layouts.show(ACCOUNTS_LAYOUT_INDEX);

	defaultLayoutIndex = layouts.getCurrentIndex();
	return true;
    }

    private void createAreas()
    {
	final ListArea.Params accountsParams = new ListArea.Params();
	accountsParams.environment = new DefaultControlEnvironment(luwrain);
	accountsParams.model = new ListUtils.FixedModel(base.getAccounts());
	accountsParams.appearance = new AccountsAppearance(luwrain, strings);
	accountsParams.name = "Учётные записи";

	accountsArea = new ListArea(accountsParams) {

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
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onEnvironmentEvent(event);
		    switch (event.getCode())
		    {

		    case ACTION:
			if (ActionEvent.isAction(event, "user-timeline"))
			    return actions.onShowUserTimeline(base, tweetsArea, layouts);
			if (ActionEvent.isAction(event, "search"))
			    return actions.onSearch(base, tweetsArea, layouts);
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
		    return actions.getAccountsActions();
		}
	    };

	statusArea = new StatusArea(new DefaultControlEnvironment(luwrain)) {

		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() &&! event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    if (layouts.getCurrentIndex() == ACCOUNTS_LAYOUT_INDEX)
			    {
gotoAccounts();
			    return true;
			    }
			    break;
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
			if (ActionEvent.isAction(event, "user-timeline"))
			    return actions.onShowUserTimeline(base, tweetsArea, layouts);
			if (ActionEvent.isAction(event, "search"))
			    return actions.onSearch(base, tweetsArea, layouts);
			if (ActionEvent.isAction(event, "show-accounts"))
			{
			    layouts.show(ACCOUNTS_LAYOUT_INDEX);
			    defaultLayoutIndex = layouts.getCurrentIndex();
			    luwrain.setActiveArea(accountsArea);
			    return true;
			}
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
		    return actions.getHomeTimelineActions(true);
		}
	    };

	final ListArea.Params tweetsParams = new ListArea.Params();
	tweetsParams.environment = new DefaultControlEnvironment(luwrain);
	tweetsParams.model = new ListUtils.FixedModel();
	tweetsParams.appearance = new TweetsAppearance(luwrain, strings);
	tweetsParams.name = "Список твитов";

	tweetsArea = new ListArea(tweetsParams) {

		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onEnvironmentEvent(event);
		    switch(event.getCode())
		    {
		    case ACTION:
			if (ActionEvent.isAction(event, "search"))
			    return actions.onSearch(base, tweetsArea, layouts);
			if (ActionEvent.isAction(event, "user-timeline"))
			    return actions.onShowUserTimeline(base, tweetsArea, layouts);
			if (ActionEvent.isAction(event, "show-timeline"))
			    return showDefaultLayout();
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
		    return actions.getTweetsActions();
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

	accountsArea.setListClickHandler((area, index, obj)->{
		if (obj == null || !(obj instanceof Account))
		    return false;
		final Account account = (Account)obj;
		if (account.isReadyToConnect())
		    return actions.onAccountsClick(base, statusArea, account);
		return startAccountAuth(account);
	    });

	statusArea.setListener((text)->actions.onUpdateStatus(base, text, statusArea));
    }

    private boolean startAccountAuth(Account account)
    {
	NullCheck.notNull(account, "account");
	if (!Popups.confirmDefaultYes(luwrain, strings.accountAuthPopupName(), strings.accountAuthPopupText(account.name)))
	    return true;
	accountToAuth = account;
	accessTokenForm.reset();
	layouts.show(ACCESS_TOKEN_FORM_LAYOUT_INDEX);
	luwrain.announceActiveArea();
	return true;
    }

    void endAccountAuth(boolean success, String errorMsg,
			String accessToken, String accessTokenSecret)
    {
	layouts.show(INITIAL_LAYOUT_INDEX);
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

    private boolean showDefaultLayout()
    {
	if (defaultLayoutIndex < 0)
	    return false;
	layouts.show(defaultLayoutIndex);
	luwrain.announceActiveArea();
	return true;
    }

private void gotoAccounts()
    {
	luwrain.setActiveArea(accountsArea);
    }

    private void gotoStatus()
    {
	luwrain.setActiveArea(statusArea);
    }

    private void closeApp()
    {
	if (base.isBusy())
	    return;
	luwrain.closeApp();
    }

    @Override public AreaLayout getAreasToShow()
    {
	return layouts.getCurrentLayout();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    static private Account findAccount(ListArea.Model model, String name)
    {
	NullCheck.notNull(model, "model");
	NullCheck.notNull(name, "name");
	if (!(model instanceof ListUtils.FixedModel))
	    return null;
	final ListUtils.FixedModel items = (ListUtils.FixedModel)model;
	for(Object o: items)
	{
	    if (!(o instanceof Account))
		continue;
	    final Account account = (Account)o;
	    if (account.name.equals(name))
		return account;
	}
	return null;
    }
}
