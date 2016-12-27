/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.core.*;
import org.luwrain.controls.*;

import twitter4j.*;

class TweetsModel implements ListArea.Model
{
    private Luwrain luwrain;
    private TweetWrapper[] wrappers;

    TweetsModel(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
    }

    void setTweets(TweetWrapper[] wrappers)
    {
	this.wrappers = wrappers;
    }

    @Override public int getItemCount()
    {
	return wrappers != null?wrappers.length:0;
    }

    @Override public Object getItem(int index)
    {
	if (wrappers == null || index < 0 || index >= wrappers.length)
	    return null;
	return wrappers[index];
    }

    @Override public void refresh()
    {
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
    }
}
