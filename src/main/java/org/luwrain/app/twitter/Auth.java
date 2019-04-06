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
	conf = new ConfigurationLuwrain(consumerKey, consumerSecret, null, null);
	    twitter = new TwitterFactory(conf).getInstance();
requestToken = twitter.getOAuthRequestToken();
    }

String getAuthorizationURL()
    {
	return requestToken.getAuthorizationURL();
    }

    void askForAccessToken(String pin) throws TwitterException
    {
	NullCheck.notNull(pin, "pin");
	if (pin.length() > 0) 
			accessToken = twitter.getOAuthAccessToken(requestToken, pin); else
	    accessToken = twitter.getOAuthAccessToken(requestToken);
    }

    String getAccessToken()
    {
	return accessToken.getToken();
    }

    String getAccessTokenSecret()
    {
	return accessToken.getTokenSecret();
    }

    public static void main(String[] args) 
    {
	if (args.length < 2)
	{
	    System.err.println("You must provide consumer key and consumer secret");
	    return ;
	}
	Twitter twitter = null;
	try {
	    ConfigurationLuwrain conf = new ConfigurationLuwrain(args[0], args[1], null, null);
	    twitter = new TwitterFactory(conf).getInstance();
	    final RequestToken requestToken = twitter.getOAuthRequestToken();
	    System.out.println("Got request token.");
	    System.out.println("Request token: " + requestToken.getToken());
	    System.out.println("Request token secret: " + requestToken.getTokenSecret());
	    AccessToken accessToken = null;
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    while (accessToken == null)
	    {
		System.out.println("Open the following URL and grant access to your account:");
		System.out.println(requestToken.getAuthorizationURL());
		System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
		String pin = br.readLine();
		try {
		    if (pin.length() > 0) 
		    {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		    } else 
		    {
			accessToken = twitter.getOAuthAccessToken(requestToken);
		    }
		} 
		catch (TwitterException te) 
		{
		    if (te.getStatusCode() == 401) 
		    {
			System.out.println("Unable to get the access token.");
		    } else 
		    {
			te.printStackTrace();
		    }
		}
	    } //while();
	    System.out.println("Got access token.");
	    System.out.println("Access token: " + accessToken.getToken());
	    System.out.println("Access token secret: " + accessToken.getTokenSecret());
	} 
	catch (IllegalStateException e) 
	{
	    if (!twitter.getAuthorization().isEnabled()) 
		System.out.println("OAuth consumer key/secret is not set."); else
		e.printStackTrace();
	}
	catch (TwitterException e)
	{
	    e.printStackTrace(); 
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
    }
}
