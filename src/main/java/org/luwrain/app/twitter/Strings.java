/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import java.util.Date;

public interface Strings
{
    String appName();
    String tweetsAreaName();
    String searchSectionsItem();
    String postSectionsItem();
    String homeTweetsSectionsItem();
    String userTweetsSectionsItem();
    String noConnection();
    String problemConnecting();
    String searchPopupName();
    String searchPopupPrefix();
    String problemSearching();
    String nothingFound();
    String postPopupName();
    String postPopupPrefix();
    String postingSuccess();
    String problemPosting();
    String problemHomeTweets();
    String userTweetsPopupName();
    String userTweetsPopupPrefix();
    String problemUserTweets();
    String noUserTweets();
    String connectedAccount();
    String account();
    String numberOfFavorites(int num);
    String numberOfRetweets(int num);
    String passedTime(Date date);
    String retweet();
}
