
package org.luwrain.app.twitter;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.popups.Popups;
import org.luwrain.cpanel.*;

class SettingsFactory implements org.luwrain.cpanel.Factory
{
    private final Luwrain luwrain;
    //    private Strings strings;

    private SimpleElement twitterElement = new SimpleElement(StandardElements.APPLICATIONS, this.getClass().getName());

    SettingsFactory(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Element[] getElements()
    {
	return new Element[]{twitterElement};
    }

    @Override public Element[] getOnDemandElements(Element parent)
    {
	NullCheck.notNull(parent, "parent");
	if (!parent.equals(twitterElement))
return new Element[0];
	final LinkedList<Element> res = new LinkedList<Element>();
	final Registry registry = luwrain.getRegistry();
	registry.addDirectory(Settings.ACCOUNTS_PATH);
	for(String p: registry.getDirectories(Settings.ACCOUNTS_PATH))
	{
	    final String path = Registry.join(Settings.ACCOUNTS_PATH, p);
	    final Settings.Account account = Settings.createAccount(registry, path);
	    res.add(new SettingsAccountElement(parent, account, p));
	}
	return res.toArray(new Element[res.size()]);
    }

    @Override public Section createSection(Element el)
    {
	NullCheck.notNull(el, "el");
	if (el.equals(twitterElement))
	    return new SimpleSection(twitterElement, "Твиттер", null);/*,
				     new Action[]{
					 //					 new Action("add-mail-account-yandex", strings.addAccountYandex()),
					 //					 new Action("add-mail-account", strings.addMailAccount(), new KeyboardEvent(KeyboardEvent.Special.INSERT)),
					 //					 new Action("add-mail-account-google", strings.addAccountGoogle()),
				     }, (controlPanel, event)->onAccountsActionEvent(controlPanel, event, -1));
										       */

	if (el instanceof SettingsAccountElement)
	{
	    final SettingsAccountElement accountEl = (SettingsAccountElement)el;
	    return new SimpleSection(el, accountEl.getTitle(), (controlPanel)->SettingsAccountForm.create(controlPanel, accountEl.getAccount(), accountEl.getTitle()));
}
	/*
				     new Action[]{
					 new Action("add-mail-account", strings.addMailAccount(), new KeyboardEvent(KeyboardEvent.Special.INSERT)),
					 new Action("add-mail-account-google", strings.addAccountGoogle()),
					 new Action("add-mail-account-yandex", strings.addAccountYandex()),
					 new Action("delete-mail-account", strings.deleteAccount(), new KeyboardEvent(KeyboardEvent.Special.DELETE)),
				     }, (controlPanel, event)->onAccountsActionEvent(controlPanel, event, ((AccountElement)el).id()));
	*/
	return null;
    }

    /*
    private boolean onAccountsActionEvent(ControlPanel controlPanel, ActionEvent event, long id)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(event, "event");
	if (!initStoring())
	{
	    luwrain.message(strings.noStoring(), Luwrain.MESSAGE_ERROR);
	    return true;
	}
    */

}
