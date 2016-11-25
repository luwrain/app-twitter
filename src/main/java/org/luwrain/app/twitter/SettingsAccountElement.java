
package org.luwrain.app.twitter;

import org.luwrain.core.*;
import org.luwrain.cpanel.*;
import org.luwrain.pim.news.*;

class SettingsAccountElement implements Element
{
    private final Element parent;
    private final Settings.Account account;
    private String title;

    SettingsAccountElement(Element parent, Settings.Account account,
String title)
    {
	NullCheck.notNull(parent, "parent");
	NullCheck.notNull(account, "account");
	NullCheck.notEmpty(title, "title");
	this.parent = parent;
	this.account = account;
	this.title = title;
    }

    @Override public Element getParentElement()
    {
	return parent;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof SettingsAccountElement))
	    return false;
	return title.equals(((SettingsAccountElement)o).title);
    }

    @Override public int hashCode()
    {
	return title.hashCode();
    }

    String getTitle()
    {
	return title;
    }

    Settings.Account getAccount()
    {
	return account;
    }

}
