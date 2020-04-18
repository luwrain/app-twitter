
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
	this.conv = null;
    }


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
	if (base.isBusy())
	    return false;
	return base.run(()->{
		try {
		    base.getTwitter().createFavorite(tweet.tweet.getId());
		    updateHomeTimeline();
		    done();
		    luwrain.runUiSafely(onSuccess);
		}
		catch (TwitterException e)
		{
		    onExceptionBkg(e);
		}
	    });
    }

    boolean onRetweetStatus(Tweet tweet, Runnable onSuccess)
    {
	NullCheck.notNull(tweet, "tweet");
	NullCheck.notNull(onSuccess, "onSuccess");
	if (base.isBusy())
	    return false;
	return base.run(()->{
		try {
		    base.getTwitter().retweetStatus(tweet.tweet.getId());
		    updateHomeTimeline();
		    done();
		    luwrain.runUiSafely(onSuccess);
		}
		catch (TwitterException e)
		{
		    onExceptionBkg(e);
		}
	    });
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
	if (base.isBusy())
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
	done();
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
