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

import twitter4j.*;
import twitter4j.conf.*;

import org.luwrain.core.*;

class Tokens
{
Auth createAuth() throws TwitterException
    {
	return new Auth("luwrain-twitter-consumer-key", "luwrain-twitter-consumer-secret");
    }

        static private twitter4j.conf.Configuration getConfiguration(String accessToken, String accessTokenSecret)
    {
	twitter = createTwitter("luwrain-twitter-consumer-key", "luwrain-twitter-consumer-secret",
				accessToken, accessTokenSecret);
    }

    static twitter4j.conf.Configuration getConfiguration(Account account)
    {
	NullCheck.notNull(account, "account");
	return getConfiguration(account.getAccessToken(), account.getAccessTokenSecret());
    }
}
