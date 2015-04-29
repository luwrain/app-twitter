

package org.luwrain.app.twitter;

import org.luwrain.core.*;

class Work implements Runnable
{
    protected Luwrain luwrain;
    protected Area destArea;
    public boolean finished = false;

    public Work(Luwrain luwrain, Area destArea)
    {
	this.luwrain = luwrain;
	this.destArea = destArea;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (destArea == null)
	    throw new NullPointerException("destArea may not be null");
    }

    @Override public void run()
    {
	finished = false;
	work();
	finished = true;
    }

    public void work()
    {
    }

    protected void showTweets(TweetWrapper[] tweets)
    {
	luwrain.enqueueEvent(new ShowTweetsEvent(destArea, tweets));
    }

    protected void message(String text, int type)
    {
	luwrain.enqueueEvent(new MessageEvent(destArea, text, type));
    }

}
