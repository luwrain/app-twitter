/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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
