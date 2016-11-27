
package org.luwrain.app.twitter;

import org.luwrain.core.*;

interface Settings
{
    static final String ACCOUNTS_PATH = "/org/luwrain/app/twitter/accounts";

    interface Account
    {
	String getAccessToken(String defValue);
	String getAccessTokenSecret(String defValue);
	void setAccessToken(String value);
	void setAccessTokenSecret(String value);
    }

    static Account createAccount(Registry registry, String path)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notEmpty(path, "path");
	return RegistryProxy.create(registry, path, Account.class);
    }
}
