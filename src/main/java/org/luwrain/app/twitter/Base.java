
package org.luwrain.app.twitter;

import twitter4j.TwitterException;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.Status;
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
}
