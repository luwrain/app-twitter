
package org.luwrain.app.twitter;

class Configuration extends ConfigurationBase
{
    public Configuration(String consumerKey,
			 String consumerSecret,
			 String accessToken,
String accessTokenSecret)
    {
	setOAuthConsumerKey(consumerKey);
	setOAuthConsumerSecret(consumerSecret);
	setOAuthAccessToken(accessToken);
	setOAuthAccessTokenSecret(accessTokenSecret);
    }
}
