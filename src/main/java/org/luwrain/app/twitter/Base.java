/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import twitter4j.conf.ConfigurationLuwrain;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class Base
{
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final Luwrain luwrain;
    private Twitter twitter = null;
    final StatusModel statusModel;

    private TweetWrapper[] tweets = new TweetWrapper[0];
    private FutureTask task = null;

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.statusModel = new StatusModel();
    }

    Auth createAuth() throws TwitterException
    {
	return new Auth("luwrain-twitter-consumer-key", "luwrain-twitter-consumer-secret");
    }

    boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    boolean run(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	if (isBusy())
	    return false;
	task = new FutureTask(runnable, null);
	executor.execute(task);
	return true;
    }

    Object call(Callable callable) throws ExecutionException
    {
	NullCheck.notNull(callable, "callable");
	if (isBusy())
	    return false;
	task = new FutureTask(callable);
	executor.execute(task);
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

    boolean activateAccount(Account account)
    {
	NullCheck.notNull(account, "account");
	if (twitter != null)
	    return false;
	twitter = createTwitter("luwrain-twitter-consumer-key", "luwrain-twitter-consumer-secret",
				account.accessToken, account.accessTokenSecret);
	return twitter != null;
    }

    boolean isAccountActivated()
    {
	return twitter != null;
    }

    boolean isReadyForQuery()
    {
	return isAccountActivated() && !isBusy();
    }

    void closeAccount()
    {
	twitter = null;
    }

    TweetWrapper[] getUserTimeline(String user)
    {
	NullCheck.notNull(user, "user");
	if (twitter == null)
	    return null;
	return userTimeline(twitter, user);
    }

    TweetWrapper[] searchTweets(String query, int numPages)
    {
	NullCheck.notNull(query, "query");
	if (twitter == null)
	    return null;
	return search(twitter, query, numPages);
    }

    static private Twitter createTwitter(String consumerKey, String consumerSecret,
			  String accessToken, String accessTokenSecret)
    {
	final ConfigurationLuwrain conf = new ConfigurationLuwrain(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	final Twitter twitter = new TwitterFactory(conf).getInstance();
	if (twitter == null)
	    return null;
	if (!twitter.getAuthorization().isEnabled()) 
	{
	    Log.error("twitter", "no enabled authorization");
	    return null;
	}
	return twitter;
    }

    static private TweetWrapper[] search(Twitter twitter, String text, int numPages)
    {
	NullCheck.notNull(twitter, "twitter");
	NullCheck.notEmpty(text, "text");
	if (numPages < 1)
	    throw new IllegalArgumentException("numPages must be greater than zero");
	final LinkedList<TweetWrapper> wrappers = new LinkedList<TweetWrapper>();
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

    synchronized void updateHomeTimeline()
    {
	NullCheck.notNull(twitter, "twitter");
	try {
	    final List<Status> result = twitter.getHomeTimeline();
	    if (result == null)
	    {
		tweets = new TweetWrapper[0];
		return;
	    }
	    final List<TweetWrapper> wrappers = new LinkedList<TweetWrapper>();
	    for(Status s: result)
		wrappers.add(new TweetWrapper(s));
tweets = wrappers.toArray(new TweetWrapper[wrappers.size()]);
	}
	catch (TwitterException e)
	{
	    luwrain.crash(e);
	    tweets = new TweetWrapper[0];
	}
    }

    static private TweetWrapper[] userTimeline(Twitter twitter, String user)
    {
	NullCheck.notNull(twitter, "twitter");
	NullCheck.notEmpty(user, "user");
	try {
	    final List<Status> result = twitter.getUserTimeline(user);
	    if (result == null)
		return null;
	    final LinkedList<TweetWrapper> wrappers = new LinkedList<TweetWrapper>();
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

    Settings.Account getAccountSettings(String accountName)
    {
	NullCheck.notEmpty(accountName, "accountName");
	final Registry registry = luwrain.getRegistry();
	final String path = Registry.join(Settings.ACCOUNTS_PATH, accountName);
	registry.addDirectory(path);
	return Settings.createAccount(registry, path);
    }

    Account[] getAccounts()
    {
	final Registry registry = luwrain.getRegistry();
	final LinkedList<Account> res = new LinkedList<Account>();
	registry.addDirectory(Settings.ACCOUNTS_PATH);
	for (String a: registry.getDirectories(Settings.ACCOUNTS_PATH))
	{
	    final Settings.Account sett = getAccountSettings(a);
	    res.add(new Account(a, sett, sett.getAccessToken(""), sett.getAccessTokenSecret("")));
	}
	return res.toArray(new Account[res.size()]);
    }

    Twitter getTwitter()
    {
	return twitter;
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

    private class StatusModel implements ConsoleArea2.Model
{
    @Override public int getConsoleItemCount()
    {
	return tweets.length;
    }

    @Override public Object getConsoleItem(int index)
    {
	if (index < 0 || index >= tweets.length)
	    throw new IllegalArgumentException("Illegal index value (" + index + ")");
	return tweets[index];
    }
}
}
