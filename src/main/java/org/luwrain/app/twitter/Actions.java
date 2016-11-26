
package org.luwrain.app.twitter;

import org.luwrain.core.*;
import org.luwrain.popups.Popups;

import twitter4j.*;

class Actions
{
    private final Luwrain luwrain;
    private final Strings strings;

    Actions(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
    }

    boolean search(Base base, Area destArea)
    {
	NullCheck.notNull(base, "base");
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

    boolean onAccountsClick(Base base, StatusArea statusArea, Account account)
    {
	NullCheck.notNull(base, "base");
	NullCheck.notNull(statusArea, "statusArea");
	NullCheck.notNull(account, "account");
	if (base.isBusy())
	    return false;
	if (base.isAccountActivated())
	    base.closeAccount();
	if (!base.activateAccount(account))
	{
	    luwrain.message(strings.problemConnecting(), Luwrain.MESSAGE_ERROR);
	    return true;
	}
	return base.run(()->{
		final TweetWrapper[] wrappers = base.getHomeTimeline();
		if (wrappers == null)
		{
		    luwrain.runInMainThread(()->luwrain.message(strings.requestProblem(), Luwrain.MESSAGE_ERROR));
		    return;
		}
		luwrain.runInMainThread(()->{
			statusArea.setEnteringPrefix(account.name + ">");
			showTweets(statusArea, wrappers);
			luwrain.setActiveArea(statusArea);
		    });
	    });
    }

    static private void showTweets(Area area, TweetWrapper[] wrappers)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNullItems(wrappers, "wrappers");
	if (area instanceof StatusArea)
	    ((StatusArea)area).setTweets(wrappers);
    }

    private void userTweets()
    {
	/*
	if (work != null && !work.finished)
	    return;
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final String user = Popups.simple(luwrain, strings.userTweetsPopupName(), strings.userTweetsPopupPrefix(), "");
	if (user == null || user.trim().isEmpty())
	    return;
	if (allowedAccounts != null && allowedAccounts.length > 0)
	{
	    boolean permitted = false;
	    for(String s: allowedAccounts)
		if (s.toLowerCase().equals(user.toLowerCase()))
		    permitted = true;
	    if (!permitted)
	    {
		luwrain.message(strings.problemUserTweets(), Luwrain.MESSAGE_ERROR);
		return;
	    }
	}
	final Strings s = strings;
	work = new Work(luwrain, tweetsArea){
		private Strings strings = s;
		@Override public void work()
		{
		    TweetWrapper[] wrappers = base.userTweets(twitter, user);
		    if (wrappers == null)
		    {
			message(strings.problemUserTweets(), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    if (wrappers.length < 0)
		    {
			message(strings.noUserTweets(), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    showTweets(wrappers);
		}
	    };
	new Thread(work).start();
	*/
    }

    private void post()
    {
	/*
	if (work != null && !work.finished)
	    return;
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final String text = Popups.simple(luwrain, strings.postPopupName(), strings.postPopupPrefix(), "");
	if (text == null || text.trim().isEmpty())
	    return;
	final Strings s = strings;
	work = new Work(luwrain, tweetsArea){
		private Strings strings = s;
		@Override public void work()
		{
		    if (base.postTweet(twitter, text))
		    {
			message(strings.postingSuccess(), Luwrain.MESSAGE_DONE);
		    } else 
		    {
			message(strings.problemPosting(), Luwrain.MESSAGE_ERROR);
		    }
		}
	    };
	new Thread(work).start();
	*/
    }

    private void homeTweets()
    {
	/*
	if (work != null && !work.finished)
	    return;
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	final Strings s = strings;
	work = new Work(luwrain, tweetsArea){
		private Strings strings = s;
		@Override public void work()
		{
		    TweetWrapper[] wrappers = base.homeTweets(twitter);
		    if (wrappers == null)
		    {
			message(strings.problemHomeTweets(), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    showTweets(wrappers);
		}
	    };
	new Thread(work).start();
	*/
    }

}
