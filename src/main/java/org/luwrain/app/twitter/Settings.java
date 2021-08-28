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

import org.luwrain.core.*;

interface Settings
{
    static final String PATH = "/org/luwrain/app/twitter";
    static final String ACCOUNTS_PATH = "/org/luwrain/app/twitter/accounts";

    interface Account
    {
	String getAccessToken(String defValue);
	void setAccessToken(String value);
	String getAccessTokenSecret(String defValue);
	void setAccessTokenSecret(String value);
	boolean getNotForStreaming(boolean defValue);
	void setNotForStreaming(boolean value);
    }

    String getDefaultAccount(String defValue);
    void setDefaultAccount(String value);
    String getStreamListeningKeywords(String defValue);
    void setStreamListeningKeywords(String value);

    static Settings create(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	return RegistryProxy.create(registry, PATH, Settings.class);
    }

    static Account createAccountByName(Registry registry, String accountName)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notEmpty(accountName, "accountName");
	final String path = Registry.join(Settings.ACCOUNTS_PATH, accountName);
	registry.addDirectory(path);
	return createAccountByPath(registry, path);
    }

    static Account createAccountByPath(Registry registry, String path)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notEmpty(path, "path");
	return RegistryProxy.create(registry, path, Account.class);
    }

    static String[] decodeKeywords(String value)
    {
	NullCheck.notNull(value, "value");
	if (value.trim().isEmpty())
	    return new String[0];
	final List<String> res = new ArrayList<>();
	for(String s: value.split(" ", -1))
	    if (!s.trim().isEmpty())
		res.add(s.trim());
	return res.toArray(new String[res.size()]);
    }
}
