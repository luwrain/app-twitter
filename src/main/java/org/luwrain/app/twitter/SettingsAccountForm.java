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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.pim.*;
import org.luwrain.pim.news.*;

class SettingsAccountForm extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    //    private Strings strings;
    private final Settings.Account account;

    SettingsAccountForm(ControlPanel controlPanel, Settings.Account account, String title)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), title);
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(account, "account");
	this.controlPanel = controlPanel;
	this .luwrain = controlPanel.getCoreInterface();
	this.account = account;
	fillForm();
    }

    private void fillForm()
    {
	addEdit("access-token", "Access token:", account.getAccessToken(""));
	addEdit("access-token-secret", "Access token secret:", account.getAccessTokenSecret(""));
    }

    @Override public boolean saveSectionData()
    {
	//	    group.setName(getEnteredText("name"));
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    static SettingsAccountForm create(ControlPanel controlPanel, Settings.Account account, String title)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(account, "account");
	NullCheck.notNull(title, "title");
	return new SettingsAccountForm(controlPanel, account, title);
    }
}
