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

class AccountsAppearance implements ListArea.Appearance<Object>
{
    private Luwrain luwrain;
    private Strings strings;

    AccountsAppearance(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
    }

    @Override public void announceItem(Object item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	if (item instanceof Account)
	{
	    luwrain.playSound(Sounds.LIST_ITEM);
	    final Account account = (Account)item;
	    if (account.connected)
		luwrain.speak(strings.connectedAccount() + " " + account.name); else
		luwrain.speak(strings.account() + " " + account.name);
	    return;
	}
	luwrain.playSound(Sounds.LIST_ITEM);
	luwrain.speak(item.toString());
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
	if (item == null)
	    return 0;
	return item.toString().length();
    }
}
