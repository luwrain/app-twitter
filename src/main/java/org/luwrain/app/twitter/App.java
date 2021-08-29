/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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
import twitter4j.conf.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

final class App extends AppBase<Strings> implements MonoApp
{
    static final String LOG_COMPONENT = "twitter";
    static private final String NEW_ACCOUNT_NAME = "Initial";

    static final InputEvent
		HOTKEY_MAIN = new InputEvent(InputEvent.Special.F5),
	HOTKEY_SEARCH = new InputEvent(InputEvent.Special.F6),
	HOTKEY_SEARCH_USERS = new InputEvent(InputEvent.Special.F7),
	HOTKEY_FOLLOWING = new InputEvent(InputEvent.Special.F8);

    private Conversations conv = null;
    private Twitter twitter = null;
    private final Watching watching;

    private AuthLayout authLayout = null;
    private MainLayout mainLayout = null;
    private FollowingLayout followingLayout = null;
    private SearchLayout searchLayout = null;
    private SearchUsersLayout searchUsersLayout = null;

    App(Watching watching)
    {
	super(Strings.NAME, Strings.class, "luwrain.twitter");
	NullCheck.notNull(watching, "watching");
	this.watching = watching;
    }

    @Override public AreaLayout onAppInit() throws TwitterException
    {
	this.conv = new Conversations(this);
	final Account initialAccount = findInitialAccount();
	if (initialAccount == null)
	    	this.authLayout = new AuthLayout(this); else
	this.twitter = createTwitter(initialAccount);
	this.mainLayout = new MainLayout(this);
	this.followingLayout = new FollowingLayout(this);
	this.searchLayout = new SearchLayout(this);
		this.searchUsersLayout = new SearchUsersLayout(this);
	setAppName(getStrings().appName());
	if (twitter != null)
	    this.mainLayout.updateHomeTimelineBkg();
		if (this.authLayout != null)
	    return this.authLayout.getLayout();
	return this.mainLayout.getAreaLayout();
    }

    void authCompleted(String accessToken, String accessTokenSecret)
    {
	NullCheck.notNull(accessToken, "accessToekn");
	NullCheck.notNull(accessTokenSecret, "accessToeknSecret");
	final Settings.Account sett = Settings.createAccountByName(getLuwrain().getRegistry(), NEW_ACCOUNT_NAME);
	sett.setAccessToken(accessToken);
	sett.setAccessTokenSecret(accessTokenSecret);
	final Account a = new Account(NEW_ACCOUNT_NAME, sett);
	this.twitter = createTwitter(a);
	this.authLayout = null;
	setAreaLayout(mainLayout);
		    this.mainLayout.updateHomeTimelineBkg();
    }

    Account[] getAccounts()
    {
	return getAccounts(getLuwrain());
    }

    static Account[] getAccounts(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Registry registry = luwrain.getRegistry();
	final List<Account> res = new LinkedList<Account>();
	registry.addDirectory(Settings.ACCOUNTS_PATH);
	for (String a: registry.getDirectories(Settings.ACCOUNTS_PATH))
	{
	    final Settings.Account sett = Settings.createAccountByName(registry, a);
	    res.add(new Account(a, sett));
	}
	return res.toArray(new Account[res.size()]);
    }

    Account findAccount(Account[] accounts, String name)
    {
	NullCheck.notNullItems(accounts, "accounts");
	NullCheck.notEmpty(name, "name");
	for(Account a: accounts)
	{
	    if (a.name.equals(name))
		return a;
	}
	return null;
    }

    private Account findInitialAccount()
    {
	final Settings sett = Settings.create(getLuwrain().getRegistry());
	final String defaultAccountName = sett.getDefaultAccount("");
	if (!defaultAccountName.trim().isEmpty())
	{
	    final Account defaultAccount = findAccount(getAccounts(), defaultAccountName);
	    if (defaultAccount != null && defaultAccount.isReadyToConnect())
		return defaultAccount;
	}
	final Account[] accounts = getAccounts();
	for(Account a: accounts)
	    if (a.isReadyToConnect())
		return a;
	return null;
    }

    private Twitter createTwitter(Account account)
    {
	NullCheck.notNull(account, "account");
	final Configuration conf = Tokens.getConfiguration(account);
	final Twitter twitter = new TwitterFactory(conf).getInstance();
	if (twitter == null)
	    return null;
	if (!twitter.getAuthorization().isEnabled())
	    return null;
	return twitter;
    }

    Twitter getTwitter()
    {
	return this.twitter;
    }

        Conversations conv()
    {
	return this.conv;
    }


    Layouts layouts()
    {
	return new Layouts(){
	    @Override public boolean main()
	    {
		setAreaLayout(mainLayout);
		mainLayout.setActiveArea(mainLayout.statusArea);
		return true;
	    }
	    @Override public boolean following()
	    {
		setAreaLayout(followingLayout);
				followingLayout.updateFollowing();
				return true;
	    }
	    	    @Override public boolean search()
	    {
		setAreaLayout(searchLayout);
				searchLayout.onActivation();
				return true;
	    }
	    	    	    @Override public boolean searchUsers()
	    {
				getLayout().setBasicLayout(searchUsersLayout.getLayout());
				searchUsersLayout.onActivation();
				return true;
	    }
	    	    @Override public void custom(AreaLayout layout)
	    {
		NullCheck.notNull(layout, "layout");
				getLayout().setBasicLayout(layout);
	    }
	};
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }

    interface Layouts
    {
	boolean main();
	boolean following();
	boolean search();
	boolean searchUsers();
	void custom(AreaLayout layout);
    }
}
