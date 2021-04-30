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

import org.luwrain.core.*;
import org.luwrain.cpanel.*;
import org.luwrain.pim.news.*;

class SettingsAccountElement implements Element
{
    private final Element parent;
    private final Settings.Account account;
    private String title;

    SettingsAccountElement(Element parent, Settings.Account account,
String title)
    {
	NullCheck.notNull(parent, "parent");
	NullCheck.notNull(account, "account");
	NullCheck.notEmpty(title, "title");
	this.parent = parent;
	this.account = account;
	this.title = title;
    }

    @Override public Element getParentElement()
    {
	return parent;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof SettingsAccountElement))
	    return false;
	return title.equals(((SettingsAccountElement)o).title);
    }

    @Override public int hashCode()
    {
	return title.hashCode();
    }

    String getTitle()
    {
	return title;
    }

    Settings.Account getAccount()
    {
	return account;
    }

}
