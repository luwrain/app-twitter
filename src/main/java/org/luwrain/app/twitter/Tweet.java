
package org.luwrain.app.twitter;

import java.util.*;
import java.util.regex.*;

import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;

final class Tweet
{
    private final Pattern REDUCED_TEXT_PATTERN = Pattern.compile("^RT @[^ ]+: (.*)$");

final Status tweet;

    Tweet(Status tweet)
    {
	NullCheck.notNull(tweet, "tweet");
	this.tweet = tweet;
    }

    String getText()
    {
	return tweet.getText().replaceAll("\n", " ");
    }

    String getReducedText()
    {
	final String text = getText();
	final Matcher m = REDUCED_TEXT_PATTERN.matcher(text);
	if (!m.find())
	    return text;
	return m.group(1);
    }

    String getUserName()
    {
	return tweet.getUser().getName();
    }

    long getAuthorId()
    {
	return tweet.getUser().getId();
    }

    Date getDate()
    {
	return tweet.getCreatedAt();
    }

    int getFavoriteCount()
    {
	return tweet.getFavoriteCount();
    }

    int getRetweetCount()
    {
	return tweet.getRetweetCount();
    }

    boolean isRetweet()
    {
	return tweet.isRetweet();
    }

    public String getTimeMark(I18n i18n)
    {
	NullCheck.notNull(i18n, "i18n");
	return i18n.getPastTimeBrief(getDate());
    }

    @Override public String toString()
    {
	return getText();
    }

    static Tweet[] create(List<Status> tweets)
    {
	if (tweets == null)
	    return new Tweet[0];
	final List<Tweet> wrappers = new LinkedList();
	for(Status s: tweets)
	    wrappers.add(new Tweet(s));
	return wrappers.toArray(new Tweet[wrappers.size()]);
    }
}
