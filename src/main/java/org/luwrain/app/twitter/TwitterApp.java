/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
//import org.luwrain.util.RegistryAutoCheck;

class TwitterApp implements Application
{
    static private final int INITIAL_LAYOUT_INDEX = 0;
    static private final int ACCESS_TOKEN_FORM_LAYOUT_INDEX = 1;

    private Luwrain luwrain;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;

    private AreaLayoutSwitch layouts;
    private ListArea accountsArea;
    private StatusArea statusArea;
    private TweetsModel tweetsModel;
    
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
	layouts.add(new AreaLayout(AreaLayout.LEFT_RIGHT, accountsArea, statusArea));
	layouts.add(new AreaLayout(accessTokenForm));
	return true;
    }

    private void createAreas()
    {
	tweetsModel = new TweetsModel(luwrain);

	/*
	final ListClickHandler tweetsClickHandler = new ListClickHandler(){
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
		    return false;
		}
	    };
	*/

	final ListArea.Params accountsParams = new ListArea.Params();
	accountsParams.environment = new DefaultControlEnvironment(luwrain);
	accountsParams.model = new ListUtils.FixedModel(base.getAccounts());
	accountsParams.appearance = new SectionsAppearance(luwrain, strings);
	accountsParams.name = strings.accountsAreaName();

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
	//	tweetsParams.clickHandler = tweetsClickHandler;
	tweetsParams.name = strings.statusAreaName();

	statusArea = new StatusArea(new DefaultControlEnvironment(luwrain)) {

		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() &&! event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
gotoAccounts();
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
}
