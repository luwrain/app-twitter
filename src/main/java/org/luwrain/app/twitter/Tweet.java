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

import org.luwrain.core.*;

final class Tweet
{
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

    String getBasicText()
    {
	return getText();
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

    @Override public String toString()
    {
	return getText();
    }

    static Tweet[] create(List<Status> tweets)
    {
	NullCheck.notNull(tweets, "tweets");
	final List<Tweet> wrappers = new LinkedList();
	for(Status s: tweets)
	    wrappers.add(new Tweet(s));
	return wrappers.toArray(new Tweet[wrappers.size()]);
    }
}
