/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.twitter;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.popups.Popups;
import org.luwrain.cpanel.*;

class SettingsFactory implements org.luwrain.cpanel.Factory
{
    private final Luwrain luwrain;
    private final Strings strings;

    private SimpleElement twitterElement = new SimpleElement(StandardElements.APPLICATIONS, this.getClass().getName());

    SettingsFactory(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
		final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    throw new RuntimeException("No strings object " + Strings.NAME);
	  this.strings = (Strings)o;
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
	    final Settings.Account account = Settings.createAccountByPath(registry, path);
	    res.add(new SettingsAccountElement(parent, account, p));
	}
	return res.toArray(new Element[res.size()]);
    }

    @Override public Section createSection(Element el)
    {
	NullCheck.notNull(el, "el");
	if (el.equals(twitterElement))
	    return new SimpleSection(twitterElement, "Твиттер", null,
				     new Action[]{
					 new Action("add-twitter-account", strings.actionAddAccount(), new InputEvent(InputEvent.Special.INSERT)),
				     }, (controlPanel, event)->onActionEvent(controlPanel, event, ""));
	if (el instanceof SettingsAccountElement)
	{
	    final SettingsAccountElement accountEl = (SettingsAccountElement)el;
	    return new SimpleSection(el, accountEl.getTitle(), (controlPanel)->SettingsAccountForm.create(controlPanel, accountEl.getAccount(), accountEl.getTitle()),
				     new Action[]{
					 new Action("add-twitter-account", strings.actionAddAccount(), new InputEvent(InputEvent.Special.INSERT)),
					 new Action("delete-twitter-account", strings.actionDeleteAccount(), new InputEvent(InputEvent.Special.DELETE)),
				     }, (controlPanel, event)->onActionEvent(controlPanel, event, accountEl.getTitle()));
	}
	return null;
    }

    private boolean onActionEvent(ControlPanel controlPanel, ActionEvent event, String accountName)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(event, "event");
	if (ActionEvent.isAction(event, "add-twitter-account"))
	{
	    final String name = Popups.text(luwrain, strings.addAccountPopupName(), strings.addAccountPopupPrefix(), "");
	    if (name == null || name.trim().isEmpty())
		return true;
	    if (name.indexOf("/") >= 0)
	    {
		luwrain.message(strings.invalidAccountName(), Luwrain.MessageType.ERROR);
	    }
	    final Registry registry = controlPanel.getCoreInterface().getRegistry();
	    final String path = Registry.join(Settings.ACCOUNTS_PATH, name);
	    if (registry.hasDirectory(path))
	    {
		luwrain.message(strings.accountAlreadyExists(name), Luwrain.MessageType.ERROR);
		return true;
	    }
	    registry.addDirectory(path);
	    controlPanel.refreshSectionsTree();
	    luwrain.message(strings.accountAddedSuccessfully(name), Luwrain.MessageType.OK);
	    return true;
	}
	if (ActionEvent.isAction(event, "delete-twitter-account"))
	{
	    NullCheck.notNull(accountName, "accountName");
	    if (!Popups.confirmDefaultNo(luwrain, strings.deleteAccountPopupName(), strings.deleteAccountPopupText(accountName)))
		return true;
	    final Registry registry = controlPanel.getCoreInterface().getRegistry();
	    final String path = Registry.join(Settings.ACCOUNTS_PATH, accountName);
	    if (registry.deleteDirectory(path))
	    {
		luwrain.message(strings.accountDeletedSuccessfully(accountName), Luwrain.MessageType.OK);
		controlPanel.refreshSectionsTree();
	    }
	    return true;
	}
	return false;
    }
}
