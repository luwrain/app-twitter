/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class Account
{
    final String name;
    final Settings.Account sett;
    String accessToken = "";
    String accessTokenSecret = "";

    boolean connected = false;

    Account(String name, Settings.Account sett,
	    String accessToken, String accessTokenSecret)
    {
	NullCheck.notNull(name, "name");
	NullCheck.notNull(sett, "sett");
	NullCheck.notNull(accessToken, "accessToken");
	NullCheck.notNull(accessTokenSecret, "accessTokenSecret");
	this.name = name;
	this.sett = sett;
	this.accessToken = accessToken;
	this.accessTokenSecret = accessTokenSecret;
    }

    boolean isReadyToConnect()
    {
	return accessToken != null && !accessToken.isEmpty() && 
	accessTokenSecret != null && !accessTokenSecret.isEmpty();
    }

    @Override public String toString()
    {
	return "@" + name;
    }
}
