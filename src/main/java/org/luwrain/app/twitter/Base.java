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
import java.util.concurrent.*;

import twitter4j.*;
import twitter4j.conf.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

final class Base extends Tokens
{
    static final String LOG_COMPONENT = "twitter";

    final Luwrain luwrain;
    final Strings strings;
    final Watching watching;
    private Twitter twitter = null;
    final StatusModel statusModel;

    Tweet[] homeTimeline = new Tweet[0];
    private Tweet[] tweets = new Tweet[0];
    private FutureTask task = null;

    private Area[] visibleAreas = new Area[0];

    Base(Luwrain luwrain, Strings strings, Watching watching)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
	this.watching = watching;
	this.statusModel = new StatusModel();
    }

    boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    void done()
    {
	this.task = null;
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
    }

    void setVisibleAreas(Area[] areas)
    {
	NullCheck.notNullItems(areas, "areas");
	this.visibleAreas = areas.clone();
    }

    boolean run(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	if (isBusy())
	    return false;
	task = new FutureTask(runnable, null);
	luwrain.executeBkg(task);
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
	return true;
    }

    Object call(Callable callable) throws ExecutionException
    {
	NullCheck.notNull(callable, "callable");
	if (isBusy())
	    return false;
	task = new FutureTask(callable);
	luwrain.executeBkg(task);
	try {
	    return task.get();
	}
	catch(ExecutionException e)
	{
	    throw e;
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return null;
	}
    }

    Twitter getTwitter()
    {
	return twitter;
    }

    boolean activateAccount(Account account)
    {
	NullCheck.notNull(account, "account");
	if (twitter != null)
	    return false;
	twitter = createTwitter(account);
	return twitter != null;
    }

    void closeAccount()
    {
	twitter = null;
    }

    boolean isAccountActivated()
    {
	return twitter != null;
    }

    static boolean updateStatusImpl(Twitter twitter, String tweet)
    {
	NullCheck.notNull(twitter, "twitter");
	NullCheck.notEmpty(tweet, "tweet");
	try {
	    twitter.updateStatus(tweet);
	    return true;
	}
	catch(TwitterException e)
	{
	    Log.error("twitter", "unable to update status:" + e.getClass().getName() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    static private Settings.Account getAccountSettings(Luwrain luwrain, String accountName)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(accountName, "accountName");
	final Registry registry = luwrain.getRegistry();
	final String path = Registry.join(Settings.ACCOUNTS_PATH, accountName);
	registry.addDirectory(path);
	return Settings.createAccount(registry, path);
    }

    static Account[] getAccounts(Luwrain luwrain )
    {
	final Registry registry = luwrain.getRegistry();
	final LinkedList<Account> res = new LinkedList<Account>();
	registry.addDirectory(Settings.ACCOUNTS_PATH);
	for (String a: registry.getDirectories(Settings.ACCOUNTS_PATH))
	{
	    final Settings.Account sett = getAccountSettings(luwrain, a);
	    res.add(new Account(a, sett));
	}
	return res.toArray(new Account[res.size()]);
    }

    static Account findAccount(Account[] accounts, String name)
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

    static Twitter createTwitter(Account account)
    {
	NullCheck.notNull(account, "account");
	final Configuration conf = getConfiguration(account);
	final Twitter twitter = new TwitterFactory(conf).getInstance();
	if (twitter == null)
	    return null;
	if (!twitter.getAuthorization().isEnabled()) 
	{
	    Log.error(LOG_COMPONENT, "no enabled authorization");
	    return null;
	}
	return twitter;
    }

    private class StatusModel implements ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return homeTimeline.length;
	}
	@Override public Object getItem(int index)
	{
	    return homeTimeline[index];
	}
	@Override public void refresh()
	{
	}
    }
}
