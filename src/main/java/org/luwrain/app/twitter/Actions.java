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
    final Conversations conversations;

    Actions(Luwrain luwrain, Base base, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(base, "base");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.base = base;
	this.strings = strings;
	this.conversations = new Conversations(luwrain, base, strings);
    }

    boolean search(Area destArea)
    {
	NullCheck.notNull(destArea, "destArea");
	if (base.isBusy())
	    return false;
	if (!base.isAccountActivated())
	{
	    luwrain.message(strings.youShouldConnect(), Luwrain.MESSAGE_ERROR);
	    return true;
	}
	final String query = Popups.simple(luwrain, strings.searchPopupName(), strings.searchPopupPrefix(), "");
	if (query == null || query.trim().isEmpty())
	    return true;
	return base.run(()->{
		final TweetWrapper[] wrappers = base.searchTweets(query, 10);
		if (wrappers == null)
		{
		    luwrain.runInMainThread(()->luwrain.message(strings.requestProblem(), Luwrain.MESSAGE_ERROR));
		    return;
		}
		if (wrappers.length <= 0)
		{
		    luwrain.runInMainThread(()->luwrain.message(strings.nothingFound(), Luwrain.MESSAGE_ERROR));
		    return;
		}
		luwrain.runInMainThread(()->showTweets(destArea, wrappers));
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
		final TweetWrapper[] wrappers = base.getHomeTimeline();
		if (wrappers == null)
		{
		    luwrain.message(strings.requestProblem(), Luwrain.MessageType.ERROR);
		    return;
		}
		luwrain.runInMainThread(()->{
			statusArea.setEnteringPrefix(account.name + ">");
			showTweets(statusArea, wrappers);
			luwrain.setActiveArea(statusArea);
		    });
	    });
    }

    boolean onShowUserTimeline(TwitterApp app)
    {
	NullCheck.notNull(app, "app");
	if (base.isBusy())
	    return false;
	final String userName = conversations.askUserNameToShowTimeline();
	if (userName == null || userName.trim().isEmpty())
	    return true;
	return base.run(()->{
		final TweetWrapper[] wrappers = base.getUserTimeline(userName);
		if (wrappers == null)
		{
		    luwrain.message(strings.requestProblem(), Luwrain.MessageType.ERROR);
		    return;
		}
		luwrain.runInMainThread(()->{
			app.showTweetsArea("Твиты пользователя \"" + userName + "\"", wrappers);
		    });
	    });
    }

    boolean onSearch(TwitterApp app)
    {
	NullCheck.notNull(app, "app");
	if (base.isBusy())
	    return false;
	final String query = conversations.askSearchQuery();
	if (query == null)
	    return true;
	return base.run(()->{
		final TweetWrapper[] wrappers = base.searchTweets(query, 1);
		if (wrappers == null)
		{
		    luwrain.message(strings.requestProblem(), Luwrain.MessageType.ERROR);
		    return;
		}
		luwrain.runInMainThread(()->{
			app.showTweetsArea("Результаты поиска по фразе \"" + query + "\"", wrappers);
		    });
	    });
    }

    boolean onUpdateStatus(String text, StatusArea area)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(area, "area");
	if (base.isBusy() || text.isEmpty())
	    return false;
	if (!base.isAccountActivated())
	    return false;
	if (text.length() > MAX_TWEET_LEN && !conversations.confirmTooLongTweet())
	    return true;
	base.run(()->{
		if (!base.updateStatus(text))
		{
		    luwrain.message(strings.requestProblem(), Luwrain.MessageType.ERROR);
		return;
	    }
		final TweetWrapper[] wrappers = base.getHomeTimeline();
		if (wrappers == null)
		{
		    luwrain.message(strings.requestProblem(), Luwrain.MessageType.ERROR);
		    return;
		}
		    luwrain.runInMainThread(()->{
			    showTweets(area, wrappers);
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
	if (obj == null || !(obj instanceof TweetWrapper))
	    return false;
	base.run(()->{
		if (!base.followAuthor((TweetWrapper)obj))
		{
		    luwrain.runInMainThread(()->luwrain.message(strings.requestProblem(), Luwrain.MESSAGE_ERROR));
		    return;
		}
		luwrain.runInMainThread(()->{
			luwrain.playSound(Sounds.MESSAGE);
		    }); 
	    });
	return true;
    }

    static private void showTweets(Area area, TweetWrapper[] wrappers)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNullItems(wrappers, "wrappers");
	if (area instanceof StatusArea)
	    ((StatusArea)area).setTweets(wrappers);
	if (area instanceof ListArea)
	{
	    final ListArea.Model model = ((ListArea)area).getListModel();
	    if (model instanceof ListUtils.FixedModel)
		((ListUtils.FixedModel)model).setItems(wrappers);
	}
    }
}
