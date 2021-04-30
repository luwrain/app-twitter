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

//For consumer key and secret everybody should go to https://dev.twitter.com/apps

import java.io.*;

import org.luwrain.core.*;

import twitter4j.*;
import twitter4j.auth.*;
import twitter4j.conf.*;

public class Auth
{
private 	final Twitter twitter;
    private final ConfigurationLuwrain conf;
    private final RequestToken requestToken;
private 	    AccessToken accessToken = null;

    Auth(String consumerKey, String consumerSecret) throws TwitterException
    {
	NullCheck.notEmpty(consumerKey, "consumerKey");
	NullCheck.notNull(consumerSecret, "consumerSecret");
	this.conf = new ConfigurationLuwrain(consumerKey, consumerSecret, null, null);
	    this.twitter = new TwitterFactory(conf).getInstance();
this.requestToken = twitter.getOAuthRequestToken();
    }

String getAuthorizationURL()
    {
	return requestToken.getAuthorizationURL();
    }

    void askForAccessToken(String pin) throws TwitterException
    {
	NullCheck.notEmpty(pin, "pin");
			this.accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			    }

    String getAccessToken()
    {
	return accessToken.getToken();
    }

    String getAccessTokenSecret()
    {
	return accessToken.getTokenSecret();
    }
}
