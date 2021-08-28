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

final class Watching
{
    static private final String LOG_COMPONENT = App.LOG_COMPONENT;
    static private final String HOOK_NAME = "luwrain.announcement";

    private final Luwrain luwrain;
    private final TwitterStream twitter;

    private final Set<String> statuses = new TreeSet<>();

    Watching(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	final Account account = chooseAccount();
	if (account == null)
	{
	    Log.warning(LOG_COMPONENT, "no Twitter stream listening, no suitable account");
	    this.twitter = null;
	    return;
	}
	Log.debug(LOG_COMPONENT, "starting twitter listener for the account \'" + account.name + "\'");
	final Configuration conf = Tokens.getConfiguration(account);
	this.twitter = new TwitterStreamFactory(conf).getInstance();
	loadKeywords();
    }

    void update()
    {
	loadKeywords();
    }

    private void loadKeywords()
    {
	twitter.cleanUp();
		final Settings sett = Settings.create(luwrain.getRegistry());
	final String[] keywords = Settings.decodeKeywords(sett.getStreamListeningKeywords(""));
	if (keywords.length == 0)
	    return;
	    twitter.addListener(new StatusAdapter() {
		    @Override public synchronized void onStatus(Status status)
		    {
			final Tweet tweet = new Tweet(status);
			if (statuses.contains(tweet.getReducedText()))
			    return;
			statuses.add(tweet.getReducedText());
			luwrain.announcement(status.getText(), "social_networks", "twitter");
		    }
		});
	    twitter.filter(new FilterQuery(keywords));
    }

    private Account chooseAccount()
    {
	final Account[] accounts = App.getAccounts(luwrain);
	for(Account a: accounts)
	    if (a.isReadyToConnect())
		return a;
	return null;
    }
}
