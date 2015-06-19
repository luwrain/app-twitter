/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.twitter;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class SectionsAppearance implements ListItemAppearance
{
    private Luwrain luwrain;
    private Strings strings;

    public SectionsAppearance(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
    }

    @Override public void introduceItem(Object item, int flags)
    {
	if (item == null)
	    return;
	if (item instanceof Account)
	{
	    luwrain.playSound(Sounds.NEW_LIST_ITEM);
	    final Account account = (Account)item;
	    if (account.connected)
		luwrain.say(strings.connectedAccount() + " " + account.name); else
		luwrain.say(strings.account() + " " + account.name);
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
	if (item == null)
	    return 0;
	return item.toString().length();
    }
}