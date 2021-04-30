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

final class Account
{
    final String name;
    final Settings.Account sett;

    boolean connected = false;

    Account(String name, Settings.Account sett)
    {
	NullCheck.notNull(name, "name");
	NullCheck.notNull(sett, "sett");
	this.name = name;
	this.sett = sett;
    }

    boolean isReadyToConnect()
    {
	return !getAccessToken().trim().isEmpty() && !getAccessTokenSecret().trim().isEmpty();
    }

    @Override public String toString()
    {
	return "@" + name;
    }

    String getAccessToken()
    {
	return sett.getAccessToken("");
    }

    String getAccessTokenSecret()
    {
	return sett.getAccessTokenSecret("");
    }
}
