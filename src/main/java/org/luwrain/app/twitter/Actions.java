
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

    boolean search(Base base, Twitter twitter, Area destArea)
    {
	NullCheck.notNull(base, "base");
	NullCheck.notNull(destArea, "destArea");
	if (base.isBusy())
	    return false;
	if (twitter == null)
	{
	    luwrain.message(strings.noConnection(), Luwrain.MESSAGE_ERROR);
	    return true;
	}
	final String query = Popups.simple(luwrain, strings.searchPopupName(), strings.searchPopupPrefix(), "");
	if (query == null || query.trim().isEmpty())
	    return true;
	return base.run(()->{
		final TweetWrapper[] wrappers = base.search(twitter, query, 10);
		if (wrappers == null)
		{
		    luwrain.runInMainThread(()->luwrain.message(strings.problemSearching(), Luwrain.MESSAGE_ERROR));
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
		    luwrain.runInMainThread(()->luwrain.message(strings.problemHomeTweets(), Luwrain.MESSAGE_ERROR));
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
}
