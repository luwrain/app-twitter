
package org.luwrain.app.twitter;

import java.io.*;

import twitter4j.*;
import twitter4j.auth.*;
import twitter4j.conf.*;

public class Auth
{
    public static void main(String[] args) 
    {
	if (args.length < 2)
	{
	    System.err.println("You must provide consumer key and consumer secret");
	    return ;
	}
	Twitter twitter = null;
	try {
	    ConfigurationLuwrain conf = new ConfigurationLuwrain(args[0], args[1], null, null);
	    twitter = new TwitterFactory(conf).getInstance();
	    RequestToken requestToken = twitter.getOAuthRequestToken();
	    System.out.println("Got request token.");
	    System.out.println("Request token: " + requestToken.getToken());
	    System.out.println("Request token secret: " + requestToken.getTokenSecret());
	    AccessToken accessToken = null;
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    while (accessToken == null)
	    {
		System.out.println("Open the following URL and grant access to your account:");
		System.out.println(requestToken.getAuthorizationURL());
		System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
		String pin = br.readLine();
		try {
		    if (pin.length() > 0) 
		    {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		    } else 
		    {
			accessToken = twitter.getOAuthAccessToken(requestToken);
		    }
		} 
		catch (TwitterException te) 
		{
		    if (te.getStatusCode() == 401) 
		    {
			System.out.println("Unable to get the access token.");
		    } else 
		    {
			te.printStackTrace();
		    }
		}
	    } //while();
	    System.out.println("Got access token.");
	    System.out.println("Access token: " + accessToken.getToken());
	    System.out.println("Access token secret: " + accessToken.getTokenSecret());
	} 
	catch (IllegalStateException e) 
	{
	    if (!twitter.getAuthorization().isEnabled()) 
		System.out.println("OAuth consumer key/secret is not set."); else
		e.printStackTrace();
	}
	catch (TwitterException e)
	{
	    e.printStackTrace(); 
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
    }
}
