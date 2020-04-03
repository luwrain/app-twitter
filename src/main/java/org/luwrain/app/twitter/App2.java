
package org.luwrain.app.twitter;

import java.util.*;

import twitter4j.*;
import twitter4j.conf.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.template.*;

final class App2 extends AppBase<Strings> implements MonoApp
{
    private Twitter twitter = null;
    private final Watching watching;
    private MainLayout mainLayout = null;

    App2(Watching watching)
    {
	super(Strings.NAME, Strings.class);
	NullCheck.notNull(watching, "watching");
	this.watching = watching;
    }

    @Override public boolean onAppInit()
    {
	final Account initialAccount = findInitialAccount();
	if (initialAccount == null)
	    return false;
	this.twitter = createTwitter(initialAccount);
	if (this.twitter == null)
	    return false;
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	this.mainLayout.updateHomeTimelineBkg();
	return true;
    }

    Account[] getAccounts()
    {
	final Registry registry = getLuwrain().getRegistry();
	final LinkedList<Account> res = new LinkedList<Account>();
	registry.addDirectory(Settings.ACCOUNTS_PATH);
	for (String a: registry.getDirectories(Settings.ACCOUNTS_PATH))
	{
	    final Settings.Account sett = Settings.createAccountByName(registry, a);
	    res.add(new Account(a, sett));
	}
	return res.toArray(new Account[res.size()]);
    }

    Account findAccount(Account[] accounts, String name)
    {
	NullCheck.notNullItems(accounts, "accounts");
	NullCheck.notEmpty(name, "name");
	for(Account a: accounts)
	{
	    if (a.name.equals(name))
		return a;
	}
	return null;
    }

    private Account findInitialAccount()
    {
	final Settings sett = Settings.create(getLuwrain().getRegistry());
	final String defaultAccountName = sett.getDefaultAccount("");
	if (!defaultAccountName.trim().isEmpty())
	{
	    final Account defaultAccount = findAccount(getAccounts(), defaultAccountName);
	    if (defaultAccount != null && defaultAccount.isReadyToConnect())
		return defaultAccount;
	}
	final Account[] accounts = getAccounts();
	for(Account a: accounts)
	    if (a.isReadyToConnect())
		return a;
	return null;
    }

    private Twitter createTwitter(Account account)
    {
	NullCheck.notNull(account, "account");
	final Configuration conf = Tokens.getConfiguration(account);
	final Twitter twitter = new TwitterFactory(conf).getInstance();
	if (twitter == null)
	    return null;
	if (!twitter.getAuthorization().isEnabled())
	    return null;
	return twitter;
    }

    Twitter getTwitter()
    {
	return this.twitter;
    }

    @Override public AreaLayout getDefaultAreaLayout()
    {
	return this.mainLayout.getLayout();
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
