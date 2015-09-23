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
import org.luwrain.controls.*;

class TweetsAppearance implements ListItemAppearance
{
    private Luwrain luwrain;
    private Strings strings;

    TweetsAppearance(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
    }

    @Override public void introduceItem(Object item, int flags)
    {
	if (item == null)
	    return;
	if (item instanceof TweetWrapper)
	{
	    final TweetWrapper wrapper = (TweetWrapper)item;
	    luwrain.playSound(Sounds.NEW_LIST_ITEM);
	    if ((flags & BRIEF) != 0)
	    {
		luwrain.say(wrapper.toString());
		return;
	    }
	    final StringBuilder b = new StringBuilder();
	    if (wrapper.isRetweet())
		b.append(strings.retweet() + " ");
	    b.append(wrapper.getUserName() + " ");
	    b.append(strings.passedTime(wrapper.getDate()) + " ");
	    b.append(wrapper.getText());
	    b.append(" " + strings.numberOfFavorites(wrapper.getFavoriteCount()));
	    b.append(" " + strings.numberOfRetweets(wrapper.getRetweetCount()));
	    luwrain.say(b.toString());
	    return;
	}
	luwrain.playSound(Sounds.NEW_LIST_ITEM);
	luwrain.say(item.toString());
    }

    @Override public String getScreenAppearance(Object item, int flags)
    {
	if (item == null)
	    return "  ";
	return item.toString();
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	return getScreenAppearance(item, 0).length();
    }
}
