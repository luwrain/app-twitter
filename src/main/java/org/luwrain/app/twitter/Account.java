
package org.luwrain.app.twitter;

class Account
{
    public String name = "";
    public String consumerKey = "";
    public String consumerSecret = "";
    public String accessToken = "";
    public String accessTokenSecret= "";
    public boolean connected = false;

    @Override public String toString()
    {
	return "@" + name;
    }
}
