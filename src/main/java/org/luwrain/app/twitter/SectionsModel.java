/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.util.RegistryAutoCheck;

class SectionsModel implements ListModel
{
    private Luwrain luwrain;
    private Account[] accounts = new Account[0];

    public SectionsModel(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	refreshAccounts();
    }

    @Override public int getItemCount()
    {
	return accounts.length;
    }

    @Override public Object getItem(int index)
    {
	return accounts[index];
    }

    @Override public void refresh()
    {
	refreshAccounts();
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
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
