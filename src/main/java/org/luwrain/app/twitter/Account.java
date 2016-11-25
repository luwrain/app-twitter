
package org.luwrain.app.twitter;

import org.luwrain.core.*;

class Account
{
    final String name;
    final String consumerKey;
    final String consumerSecret;
    final String accessToken;
    final String accessTokenSecret;

    boolean connected = false;

    Account(String name,
	    String consumerKey, String consumerSecret,
	    String accessToken, String accessTokenSecret)
    {
	NullCheck.notNull(name, "name");
	NullCheck.notNull(consumerKey, "consumerKey");
	NullCheck.notNull(consumerSecret, "consumerSecret");
	NullCheck.notNull(accessToken, "accessToken");
	NullCheck.notNull(accessTokenSecret, "accessTokenSecret");
	this.name = name;
	this.consumerKey = consumerKey;
	this.consumerSecret = consumerSecret;
	this.accessToken = accessToken;
	this.accessTokenSecret = accessTokenSecret;
    }

    @Override public String toString()
    {
	return "@" + name;
    }
}
