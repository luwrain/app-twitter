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
import twitter4j.conf.*;

import org.luwrain.core.*;

class Watching
{
    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;

    final TwitterStream twitter;

    Watching(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Account account = chooseAccount(luwrain);
	if (account == null)
	{
	    Log.warning(LOG_COMPONENT, "no Twitter stream listening, no suitable account");
	    this.twitter = null;
	    return;
	}
		Log.debug(LOG_COMPONENT, "starting twitter listener for the account \'" + account.name + "\'");
		final Configuration conf = Base.getConfiguration(account);
this.twitter = new TwitterStreamFactory(conf).getInstance();
twitter.addListener(new WatchingListener(luwrain));
twitter.filter(new FilterQuery(new String[]{"Томск", "Томске", "Томском", "Томска", "luwrain"}));
    }

    private Account chooseAccount(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Account[] accounts = Base.getAccounts(luwrain);
	for(Account a: accounts)
	    if (a.isReadyToConnect())
		return a;
	return null;
    }
}
