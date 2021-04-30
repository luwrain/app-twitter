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

import java.util.*;
import java.util.function.*;

import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;

final class Actions
{
    static final int MAX_TWEET_LEN = 140;

    private final Luwrain luwrain = null;
    private final Strings strings = null;
    final Conversations conv = null;



    /*
    boolean activateAccount(Account account, Runnable onSuccess)
    {
	NullCheck.notNull(account, "account");
	NullCheck.notNull(onSuccess, "onSuccess");
	if (base.isBusy())
	    return false;
	if (base.isAccountActivated())
	    base.closeAccount();
	if (!base.activateAccount(account))
	{
	    luwrain.message(strings.problemConnecting(), Luwrain.MessageType.ERROR);
	    return true;
	}
	return base.run(()->{
		try {
		    updateHomeTimeline();
		    done();
		    luwrain.runUiSafely(onSuccess);
		}
		catch(TwitterException e)
		{
		    onExceptionBkg(e);
		}
	    });
    }
    */





    boolean onCreateFavourite(Tweet tweet, Runnable onSuccess)
    {
	NullCheck.notNull(tweet, "tweet");
	NullCheck.notNull(onSuccess, "onSuccess");
	/*
	return base.run(()->{
		try {
		    //base.getTwitter().createFavorite(tweet.tweet.getId());
		    updateHomeTimeline();
		    done();
		    luwrain.runUiSafely(onSuccess);
		}
		catch (TwitterException e)
		{
		    onExceptionBkg(e);
		}
	    });
	*/
	return false;
    }

    boolean onRetweetStatus(Tweet tweet, Runnable onSuccess)
    {
	NullCheck.notNull(tweet, "tweet");
	NullCheck.notNull(onSuccess, "onSuccess");
	/*
	return base.run(()->{
		try {
		    //base.getTwitter().retweetStatus(tweet.tweet.getId());
		    updateHomeTimeline();
		    done();
		    luwrain.runUiSafely(onSuccess);
		}
		catch (TwitterException e)
		{
		    onExceptionBkg(e);
		}
	    });
	*/
	return true;
    }


    boolean onDeleteFriendship(ListArea listArea)
    {
	NullCheck.notNull(listArea, "listArea");
	/*
	if (base.isBusy())
	    return false;
	if (listArea.selected() == null || !(listArea.selected() instanceof UserWrapper))
	    return false;
	final UserWrapper userWrapper = (UserWrapper)listArea.selected();
	if (!Popups.confirmDefaultNo(luwrain, "Исключение из списка друзей", "Вы действительно хотите исключить из списка друзей пользователя \"" + userWrapper.toString() + "\"?"))
	    return true;
	base.run(()->{
		try {
		    //base.getTwitter().destroyFriendship(userWrapper.user.getId());
		    luwrain.runUiSafely(()->{
			    luwrain.playSound(Sounds.DONE);
			    listArea.refresh();
			});
		}
		catch(Exception e)
		{
		    luwrain.crash(e);
		}
	    });
	return true;
	*/
	return true;
    }

    boolean onDeleteLike(ListArea listArea)
    {
	NullCheck.notNull(listArea, "listArea");
	/*
	if (base.isBusy())
	    return false;
	if (listArea.selected() == null || !(listArea.selected() instanceof Tweet))
	    return false;
	final Tweet tweetWrapper = (Tweet)listArea.selected();
	if (!conv.confirmLikeDeleting(tweetWrapper))
	    return true;
	base.run(()->{
		try {
		    //base.getTwitter().destroyFavorite(tweetWrapper.tweet.getId());
		}
		catch(Exception e)
		{
		    luwrain.crash(e);
		    return;
		}
		luwrain.runUiSafely(()->{
			luwrain.playSound(Sounds.DONE);
			listArea.refresh();
		    });
	    });
	return true;
	*/
	return false;
    }

    void updateHomeTimeline() throws TwitterException
    {
	/*
	final List<Status> result = new LinkedList();//base.getTwitter().getHomeTimeline();
	if (result == null)
	{
	    base.homeTimeline = new Tweet[0];
	    return;
	}
	final List<Tweet> tweets = new LinkedList();
	for(Status s: result)
	    tweets.add(new Tweet(s));
	base.homeTimeline = tweets.toArray(new Tweet[tweets.size()]);
	*/
    }





    static private void showTweets(Area area, Tweet[] wrappers)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNullItems(wrappers, "wrappers");
	if (area instanceof ListArea)
	{
	    final ListArea.Model model = ((ListArea)area).getListModel();
	    if (model instanceof ListUtils.FixedModel)
		((ListUtils.FixedModel)model).setItems(wrappers);
	}
    }
}
