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
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;

class Actions
{
    static final int MAX_TWEET_LEN = 140;

    private final Luwrain luwrain;
    private final Base base;
    private final Strings strings;
    final Conversations conv;

    Actions(Luwrain luwrain, Base base, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(base, "base");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.base = base;
	this.strings = strings;
	this.conv = new Conversations(luwrain, base, strings);
    }

    boolean search(Area destArea)
    {
	NullCheck.notNull(destArea, "destArea");
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
		final Tweet[] wrappers = base.searchTweets(query, 10);
		if (wrappers == null)
		{
		    luwrain.runUiSafely(()->luwrain.message(strings.requestProblem(), Luwrain.MessageType.ERROR));
		    return;
		}
		if (wrappers.length <= 0)
		{
		    luwrain.runUiSafely(()->luwrain.message(strings.nothingFound(), Luwrain.MessageType.ERROR));
		    return;
		}
		luwrain.runUiSafely(()->showTweets(destArea, wrappers));
	    });
    }

    boolean activateAccount(StatusArea statusArea, Account account)
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
			statusArea.setInputPrefix(account.name + ">");
			statusArea.refresh();
			luwrain.setActiveArea(statusArea);
		    });
	    });
    }

    boolean onShowUserTimeline(App app)
    {
	NullCheck.notNull(app, "app");
	if (base.isBusy())
	    return false;
	final String userName = conv.askUserNameToShowTimeline();
	if (userName == null || userName.trim().isEmpty())
	    return true;
	return base.run(()->{
		final Tweet[] wrappers = base.getUserTimeline(userName);
		if (wrappers == null)
		{
		    luwrain.message(strings.requestProblem(), Luwrain.MessageType.ERROR);
		    return;
		}
		luwrain.runUiSafely(()->{
			app.showTweetsArea("Твиты пользователя \"" + userName + "\"", wrappers);
		    });
	    });
    }

    boolean onSearch(App app)
    {
	NullCheck.notNull(app, "app");
	if (base.isBusy())
	    return false;
	final String query = conv.askSearchQuery();
	if (query == null)
	    return true;
	return base.run(()->{
		final Tweet[] wrappers = base.searchTweets(query, 1);
		if (wrappers == null)
		{
		    luwrain.message(strings.requestProblem(), Luwrain.MessageType.ERROR);
		    return;
		}
		luwrain.runUiSafely(()->{
			app.showTweetsArea("Результаты поиска по фразе \"" + query + "\"", wrappers);
		    });
	    });
    }

    ConsoleArea2.InputHandler.Result onUpdateStatus(String text, ConsoleArea2 area)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(area, "area");
	if (text.isEmpty() || !base.isReadyForQuery())
	    return ConsoleArea2.InputHandler.Result.REJECTED;
	if (text.length() > MAX_TWEET_LEN)
	{
	    luwrain.message("Слишком длинный твит", Luwrain.MessageType.ERROR);
	    return ConsoleArea2.InputHandler.Result.OK;
	}
	base.run(()->{
		try {
		    base.getTwitter().updateStatus(text);
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
	return ConsoleArea2.InputHandler.Result.CLEAR_INPUT;
    }

    boolean onDestroyStatus(Tweet tweet, ConsoleArea2 area)
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

    boolean onCreateFavourite(Tweet tweet, ConsoleArea2 area)
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

    boolean onRetweetStatus(Tweet tweet, ConsoleArea2 area)
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
