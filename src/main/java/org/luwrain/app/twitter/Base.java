/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import twitter4j.*;
import twitter4j.conf.ConfigurationLuwrain;

class Base
{
    public Twitter createTwitter(String consumerKey,
				 String consumerSecret,
				 String accessToken,
				 String accessTokenSecret)
    {
	ConfigurationLuwrain conf = new ConfigurationLuwrain(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	Twitter twitter = new TwitterFactory(conf).getInstance();
	if (twitter == null)
	    return null;
	if (!twitter.getAuthorization().isEnabled()) 
	    return null;
	return twitter;
    }

    public boolean updateStatus(Twitter twitter, String text)
    {
	try {
	    Status status = twitter.updateStatus(text);
	}
	catch (TwitterException e)
	{
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public TweetWrapper[] search(Twitter twitter,
				 String text,
				 int numPages)
    {
	if (twitter == null)
	    throw new NullPointerException("twitter may not be null");
	if (text == null)
	    throw new NullPointerException("text may not be null");
	if (text.trim().isEmpty())
	    throw new IllegalArgumentException("text may not be empty");
	if (numPages < 1)
	    throw new IllegalArgumentException("numPages must be greater than zero");
	LinkedList<TweetWrapper> wrappers = new LinkedList<TweetWrapper>();
	try {
	    Query query = new Query(text);
            QueryResult result;
	    int pageNum = 1;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
		System.out.println("" + tweets.size());
                for (Status tweet : tweets) 
		    wrappers.add(new TweetWrapper(tweet));
		if (pageNum >= numPages)
		    return wrappers.toArray(new TweetWrapper[wrappers.size()]);
		++pageNum;
            } while ((query = result.nextQuery()) != null);
	    } 
	catch (TwitterException e) 
	{
            e.printStackTrace();
	    return null;
        }
	return wrappers.toArray(new TweetWrapper[wrappers.size()]);
    }

    public boolean postTweet(Twitter twitter, String tweet)
    {
	if (twitter == null)
	    throw new NullPointerException("twitter may not be null");
	if (tweet == null)
	    throw new NullPointerException("tweet may not be null");
	if (tweet.trim().isEmpty())
	    throw new IllegalArgumentException("tweet may not be empty");
	try {
	    twitter.updateStatus(tweet);
	    return true;
	}
	catch(TwitterException e)
	{
	    e.printStackTrace();
	    return false;
	}
    }

    public TweetWrapper[] homeTweets(Twitter twitter)
    {
	if (twitter == null)
	    throw new NullPointerException("twitter may not be null");
	try {
	    List<Status> result = twitter.getHomeTimeline();
	    if (result == null)
		return null;
	    LinkedList<TweetWrapper> wrappers = new LinkedList<TweetWrapper>();
	    for(Status s: result)
		wrappers.add(new TweetWrapper(s));
	    return wrappers.toArray(new TweetWrapper[wrappers.size()]);
	}
	catch (TwitterException e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

    public TweetWrapper[] userTweets(Twitter twitter, String user)
    {
	if (twitter == null)
	    throw new NullPointerException("twitter may not be null");
	if (user == null)
	    throw new NullPointerException("user may not be null");
	if (user.trim().isEmpty())
	    throw new IllegalArgumentException("user may not be empty");
	try {
	    List<Status> result = twitter.getUserTimeline(user);
	    if (result == null)
		return null;
	    LinkedList<TweetWrapper> wrappers = new LinkedList<TweetWrapper>();
	    for(Status s: result)
		wrappers.add(new TweetWrapper(s));
	    return wrappers.toArray(new TweetWrapper[wrappers.size()]);
	}
	catch (TwitterException e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

}
