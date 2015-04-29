

package org.luwrain.app.twitter;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

class ShowTweetsEvent extends ThreadSyncEvent
{
    public TweetWrapper[] tweets;

    public ShowTweetsEvent(Area destArea, TweetWrapper[] tweets)
    {
	super(destArea);
	this.tweets = tweets;
    }
}
