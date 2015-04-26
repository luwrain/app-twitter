
package twitter4j.conf;

public class ConfigurationLuwrain extends ConfigurationBase
{
    public ConfigurationLuwrain(String consumerKey,
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
