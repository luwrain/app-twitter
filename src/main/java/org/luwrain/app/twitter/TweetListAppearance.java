/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class TweetListAppearance implements ListArea.Appearance<Object>
{
    private final Luwrain luwrain;
    private final Strings strings;

    TweetListAppearance(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
    }

    @Override public void announceItem(Object item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	if (!(item instanceof Tweet))
	{
	    luwrain.setEventResponse(DefaultEventResponse.listItem(item.toString(), Suggestions.LIST_ITEM));
	    return;
	}
	final Tweet tweet = (Tweet)item;
	final StringBuilder b = new StringBuilder();
		    b.append(luwrain.getSpeakableText(tweet.getReducedText(), Luwrain.SpeakableTextType.NATURAL))
	    .append(", ")
	    .append(tweet.getTimeMark(luwrain.i18n()))
	    .append(", ")
	    .append(tweet.getUserName());
		    luwrain.setEventResponse(DefaultEventResponse.listItem(new String(b), Suggestions.LIST_ITEM));
    }

    @Override public String getScreenAppearance(Object item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	return item.toString();
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
    }
}
