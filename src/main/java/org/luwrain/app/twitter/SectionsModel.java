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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.util.RegistryAutoCheck;

class SectionsModel implements ListArea.Model
{
    private Luwrain luwrain;
    private Strings strings;

    private Account[] accounts = new Account[0];

    public SectionsModel(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	refreshAccounts();
    }

    @Override public int getItemCount()
    {
	final int accountCount = accounts != null?accounts.length:0;
	return accountCount + 4;
    }

    @Override public Object getItem(int index)
    {
	switch(index)
	{
	case 0:
	    return strings.searchSectionsItem();
	case 1:
	    return strings.userTweetsSectionsItem();
	case 2:
	    return strings.homeTweetsSectionsItem(); 
	case 3:
	    return strings.postSectionsItem();
	default:
	    return accounts[index - 4];
	}
    }

    @Override public void refresh()
    {
	refreshAccounts();
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
    }

    public void setActiveAccount(Account account)
    {
	for(Account a: accounts)
	    a.connected = (a == account);
    }

    public void noActiveAccount()
    {
	for(Account a: accounts)
	    a.connected = false;
    }

    private void refreshAccounts()
    {
	final String accountsPath = "/org/luwrain/app/twitter/accounts";
	final Registry registry = luwrain.getRegistry();
	final RegistryAutoCheck check = new RegistryAutoCheck(registry);
	final LinkedList<Account> res = new LinkedList<Account>();
	final String[] accountDirs = registry.getDirectories(accountsPath);
	if (accountDirs == null || accountDirs.length < 1)
	{
	    accounts = new Account[0];
	    return;
	}
	for (String a: accountDirs)
	{
	    final String dir = accountsPath + "/" + a;
	    final Account account = new Account();
	    account.name = a;
	    account.consumerKey = check.stringAny(dir + "/consumer-key", "");
	    account.consumerSecret = check.stringAny(dir + "/consumer-secret", "");
	    account.accessToken = check.stringAny(dir + "/access-token", "");
	    account.accessTokenSecret= check.stringAny(dir + "/access-token-secret", "");
	    res.add(account);
	}
	accounts = res.toArray(new Account[res.size()]);
    }
}
