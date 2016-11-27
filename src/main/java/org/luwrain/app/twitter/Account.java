
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
