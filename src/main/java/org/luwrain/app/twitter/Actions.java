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
import java.util.function.*;

import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;

final class Actions
{
    static final int MAX_TWEET_LEN = 140;

    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    final Conversations conv;

    Actions(Base base)
    {
	NullCheck.notNull(base, "base");
	this.luwrain = base.luwrain;
	this.strings = base.strings;
	this.base = base;
	this.conv = new Conversations(luwrain, base, strings);
    }

    boolean search(Consumer onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	if (base.isBusy())
	    return false;
	if (!base.isAccountActivated())
	{
	    luwrain.message(strings.youShouldConnect(), Luwrain.MessageType.ERROR);
	    return true;
	}
	final String query = Popups.simple(luwrain, strings.searchPopupName(), strings.searchPopupPrefix(), "");
	if (query == null || query.trim().isEmpty())
	    return true;
	return base.run(()->{
		try {
		    final TweetsPager pager = new TweetsPager((fromPos,count)->searchTweets(query, 1));
		    done();
		    luwrain.runUiSafely(()->onSuccess.accept(pager));
		}
		catch(TwitterException e)
		{
		    onExceptionBkg(e);
		}
	    });
    }

    boolean activateAccount(ListArea statusArea, Account account)
    {
	NullCheck.notNull(statusArea, "statusArea");
	NullCheck.notNull(account, "account");
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
		base.updateHomeTimeline();
		luwrain.runUiSafely(()->{
			statusArea.refresh();
			luwrain.setActiveArea(statusArea);
		    });
	    });
    }

    boolean onShowUserTimeline(Consumer onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	if (base.isBusy())
	    return false;
	final String userName = conv.askUserNameToShowTimeline();
	if (userName == null || userName.trim().isEmpty())
	    return true;
	return base.run(()->{
		try {
		    final TweetsPager pager = new TweetsPager((fromPos,count)->getUserTimeline(userName));
		    done();
		    luwrain.runUiSafely(()->onSuccess.accept(pager));
		}
		catch(TwitterException e)
		{
		    onExceptionBkg(e);
		}
	    });
    }

    boolean onUpdateStatus(String[] lines, Runnable onSuccess)
    {
	NullCheck.notNullItems(lines, "lines");
	NullCheck.notNull(onSuccess, "onSuccess");
	if (!base.isReadyForQuery())
	    return false;
	final String text = makeTweetText(lines);
	if (text.isEmpty())
	    return false;
	return base.run(()->{
		try {
		    base.getTwitter().updateStatus(text);
		    updateHomeTimeline();
		    done();
		    luwrain.runUiSafely(onSuccess);
		}
		catch (TwitterException e)
		{
		    onExceptionBkg(e);
		    return;
		}
	    });
    }

    boolean onDestroyStatus(Tweet tweet, ListArea area)
    {
	NullCheck.notNull(tweet, "tweet");
	NullCheck.notNull(area, "area");
	if (!base.isReadyForQuery())
	    return false;
	if (!conv.confirmTweetDeleting(tweet))
	    return true;
	base.run(()->{
		try {
		    base.getTwitter().destroyStatus(tweet.tweet.getId());
		}
		catch (TwitterException e)
		{
		    luwrain.crash(e);
		    return;
		}
		base.updateHomeTimeline();
		luwrain.runUiSafely(()->{
			area.refresh();
			luwrain.playSound(Sounds.DONE);
		    }); 
	    });
	return true;
    }

    boolean onCreateFavourite(Tweet tweet, ListArea area)
    {
	NullCheck.notNull(tweet, "tweet");
	NullCheck.notNull(area, "area");
	if (!base.isReadyForQuery())
	    return false;
	base.run(()->{
		try {
		    base.getTwitter().createFavorite(tweet.tweet.getId());
		}
		catch (TwitterException e)
		{
		    luwrain.crash(e);
		    return;
		}
		base.updateHomeTimeline();
		luwrain.runUiSafely(()->{
			area.refresh();
			luwrain.playSound(Sounds.DONE);
		    }); 
	    });
	return true;
    }

    boolean onRetweetStatus(Tweet tweet, ListArea area)
    {
	NullCheck.notNull(tweet, "tweet");
	NullCheck.notNull(area, "area");
	if (!base.isReadyForQuery())
	    return false;
	base.run(()->{
		try {
		    base.getTwitter().retweetStatus(tweet.tweet.getId());
		}
		catch (TwitterException e)
		{
		    luwrain.crash(e);
		    return;
		}
		base.updateHomeTimeline();
		luwrain.runUiSafely(()->{
			area.refresh();
			luwrain.playSound(Sounds.DONE);
		    }); 
	    });
	return true;
    }

    boolean onFollowAuthor(ListArea tweetsArea)
    {
	NullCheck.notNull(tweetsArea, "tweetsArea");
	if (base.isBusy() || !base.isAccountActivated())
	    return false;
	final Object obj = tweetsArea.selected();
	if (obj == null || !(obj instanceof Tweet))
	    return false;
	final Tweet wrapper = (Tweet)obj;
	base.run(()->{
		try {
		    base.getTwitter().createFriendship(wrapper.getAuthorId(), true);
		}
		catch(TwitterException e)
		{
		    luwrain.crash(e);
		    return;
		}
		luwrain.runUiSafely(()->{
			luwrain.playSound(Sounds.DONE);
		    }); 
	    });
	return true;
    }

    boolean onDeleteFriendship(ListArea listArea)
    {
	NullCheck.notNull(listArea, "listArea");
	if (base.isBusy())
	    return false;
	if (listArea.selected() == null || !(listArea.selected() instanceof UserWrapper))
	    return false;
	final UserWrapper userWrapper = (UserWrapper)listArea.selected();
	if (!Popups.confirmDefaultNo(luwrain, "Исключение из списка друзей", "Вы действительно хотите исключить из списка друзей пользователя \"" + userWrapper.toString() + "\"?"))
	    return true;
	base.run(()->{
		try {
		    base.getTwitter().destroyFriendship(userWrapper.user.getId());
		    luwrain.runUiSafely(()->{
			    luwrain.playSound(Sounds.DONE);
			    listArea.refresh();
			});
		}
		catch(TwitterException e)
		{
		    luwrain.crash(e);
		}
	    });
	return true;
    }

    boolean onDeleteLike(ListArea listArea)
    {
	NullCheck.notNull(listArea, "listArea");
	if (!base.isReadyForQuery())
	    return false;
	if (listArea.selected() == null || !(listArea.selected() instanceof Tweet))
	    return false;
	final Tweet tweetWrapper = (Tweet)listArea.selected();
	if (!conv.confirmLikeDeleting(tweetWrapper))
	    return true;
	base.run(()->{
		try {
		    base.getTwitter().destroyFavorite(tweetWrapper.tweet.getId());
		}
		catch(TwitterException e)
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
    }

    void updateHomeTimeline() throws TwitterException
    {
	final List<Status> result = base.getTwitter().getHomeTimeline();
	if (result == null)
	{
	    base.homeTimeline = new Tweet[0];
	    return;
	}
	final List<Tweet> tweets = new LinkedList();
	for(Status s: result)
	    tweets.add(new Tweet(s));
	base.homeTimeline = tweets.toArray(new Tweet[tweets.size()]);
    }

    Tweet[] getUserTimeline(String user) throws TwitterException
    {
	NullCheck.notEmpty(user, "user");
	final List<Status> result = base.getTwitter().getUserTimeline(user);
	if (result == null)
	    return new Tweet[0];
	final List<Tweet> tweets = new LinkedList();
	for(Status s: result)
	    tweets.add(new Tweet(s));
	return tweets.toArray(new Tweet[tweets.size()]);
    }

    private Tweet[] searchTweets(String text, int pageCount) throws TwitterException
    {
	NullCheck.notEmpty(text, "text");
	final List<Tweet> tweets = new LinkedList();
	Query query = new Query(text);
	QueryResult result;
	int pageNum = 1;
	do {
	    result = base.getTwitter().search(query);
	    List<Status> statuses = result.getTweets();
	    for (Status tweet : statuses) 
		tweets.add(new Tweet(tweet));
	    if (pageNum >= pageCount)
		return tweets.toArray(new Tweet[tweets.size()]);
	    ++pageNum;
	} while ((query = result.nextQuery()) != null);
	return tweets.toArray(new Tweet[tweets.size()]);
    }

    private String makeTweetText(String[] lines)
    {
	return "FIXME:";
    }

    private void done()
    {
	luwrain.runUiSafely(()->base.done());
    }

    private void onExceptionBkg(Exception e)
    {
	NullCheck.notNull(e, "e");
	luwrain.runUiSafely(()->onException(e));
    }

    private void onException(Exception e)
    {
	NullCheck.notNull(e, "e");
	luwrain.crash(e);
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
