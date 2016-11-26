
package org.luwrain.app.twitter;

import org.luwrain.core.*;

class Account
{
    final String name;
    final Settings.Account sett;
    final String consumerKey;
    final String consumerSecret;
    String accessToken = "";
    String accessTokenSecret = "";

    boolean connected = false;

    Account(String name, Settings.Account sett,
	    String consumerKey, String consumerSecret,
	    String accessToken, String accessTokenSecret)
    {
	NullCheck.notNull(name, "name");
	NullCheck.notNull(sett, "sett");
	NullCheck.notNull(consumerKey, "consumerKey");
	NullCheck.notNull(consumerSecret, "consumerSecret");
	NullCheck.notNull(accessToken, "accessToken");
	NullCheck.notNull(accessTokenSecret, "accessTokenSecret");
	this.name = name;
	this.sett = sett;
	this.consumerKey = consumerKey;
	this.consumerSecret = consumerSecret;
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
