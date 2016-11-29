
package org.luwrain.app.twitter;

import java.util.*;
import java.util.concurrent.*;

import org.luwrain.core.*;

import twitter4j.*;
import twitter4j.conf.ConfigurationLuwrain;

class Base
{
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final Luwrain luwrain;
    private String consumerKey = "";
    private String consumerSecret = "";
    private Twitter twitter = null;
    private FutureTask task = null;

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    boolean init()
    {
	return readKeys();
    }

    Auth createAuth() throws TwitterException
    {
	return new Auth(consumerKey, consumerSecret);
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

    boolean activateAccount(Account account)
    {
	NullCheck.notNull(account, "account");
	if (twitter != null)
	    return false;
	twitter = createTwitter(consumerKey, consumerSecret,
				     account.accessToken, account.accessTokenSecret);
	return twitter != null;
    }

    boolean isAccountActivated()
    {
	return twitter != null;
    }

    void closeAccount()
    {
	twitter = null;
    }

    TweetWrapper[] getHomeTimeline()
    {
	if (twitter == null)
	    return null;
	return homeTweets(twitter);
    }

    TweetWrapper[] getUserTimeline(String user)
    {
	NullCheck.notNull(user, "user");
	if (twitter == null)
	    return null;
	return userTimeline(twitter, user);
    }

    boolean updateStatus(String text)
    {
	NullCheck.notEmpty(text, "text");
	if (twitter == null)
	    return false;
	return updateStatusImpl(twitter, text);
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
	Log.debug("twitter", "preparing new Twitter instance");
	final ConfigurationLuwrain conf = new ConfigurationLuwrain(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	final Twitter twitter = new TwitterFactory(conf).getInstance();
	if (twitter == null)
	    return null;
	if (!twitter.getAuthorization().isEnabled()) 
	{
	    Log.error("twitter", "no enabled authorization");
	    return null;
	}
	Log.debug("twitter", "new twitter instance prepared");
	return twitter;
    }

    boolean updateStatus(Twitter twitter, String text)
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

    static private TweetWrapper[] search(Twitter twitter, String text,
			  int numPages)
    {
	NullCheck.notNull(twitter, "twitter");
	NullCheck.notNull(text, "text");
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

    static private TweetWrapper[] homeTweets(Twitter twitter)
    {
	NullCheck.notNull(twitter, "twitter");
	Log.debug("twitter", "trying to get list of tweets for the current user");
	try {
	    final List<Status> result = twitter.getHomeTimeline();
	    if (result == null)
	    {
		Log.debug("twitter", "null returned in responce");
		return null;
	    }
	    Log.debug("twitter", "" + result.size() + " items returned in responce");
	    final LinkedList<TweetWrapper> wrappers = new LinkedList<TweetWrapper>();
	    for(Status s: result)
		wrappers.add(new TweetWrapper(s));
	    return wrappers.toArray(new TweetWrapper[wrappers.size()]);
	}
	catch (TwitterException e)
	{
	    Log.error("twitter", "unable to get current user timeline:" + e.getClass().getName() + ":" + e.getMessage());
	    e.printStackTrace();
	    return null;
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

    private boolean readKeys()
    {
	final Object o;
		try {
	    o = Class.forName("org.luwrain.keys.TwitterKeys").newInstance();
	    	}
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException e)
		{
		    Log.error("twitter", "unable to read keys:" + e.getClass().getName() + ":" + e.getMessage());
		    return false;
		}
	    if (!(o instanceof org.luwrain.base.CoreProperties))
	    {
		Log.error("twitter", "unable to read keys:" + o.getClass().getName() + " is not an instance of org.luwrain.base.CoreProperties");
		return false;
	    }
	    final org.luwrain.base.CoreProperties props = (org.luwrain.base.CoreProperties)o;
	    consumerKey = props.getProperty("consumer-key"); 
	    consumerSecret = props.getProperty("consumer-secret");
	    if (consumerKey == null || consumerKey.isEmpty())
	    {
		Log.error("twitter", "no consumer-key value in keys class");
		return false;
	    }
	    if (consumerSecret == null || consumerSecret.isEmpty())
	    {
		Log.error("twitter", "no consumer-secret value in keys class");
		return false;
	    }
	    return true;
    }
}
