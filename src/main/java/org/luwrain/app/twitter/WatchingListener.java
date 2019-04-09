
package org.luwrain.app.twitter;

import java.util.*;

import twitter4j.*;

import twitter4j.api.HelpResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;

import org.luwrain.core.*;

final class WatchingListener implements StatusListener
{
    private final Luwrain luwrain;

    WatchingListener(Luwrain luwrain)
    {
	this.luwrain = luwrain;
    }

    @Override public void onStatus(Status status)
    {
	luwrain.message(status.getText());
	Log.debug("proba", status.getText());
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

    public void onScrubGeo(long userId, long upToStatusId) {}


    public void onStallWarning(StallWarning warning) {}


    

    public void onException(Exception ex) {}

    

    }
