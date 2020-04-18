
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
