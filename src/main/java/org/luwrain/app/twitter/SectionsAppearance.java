
package org.luwrain.app.twitter;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class SectionsAppearance implements ListArea.Appearance
{
    private Luwrain luwrain;
    private Strings strings;

    SectionsAppearance(Luwrain luwrain, Strings strings)
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
		luwrain.say(strings.connectedAccount() + " " + account.name); else
		luwrain.say(strings.account() + " " + account.name);
	    return;
	}
	luwrain.playSound(Sounds.LIST_ITEM);
	luwrain.say(item.toString());
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
