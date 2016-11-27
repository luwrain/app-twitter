
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
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()), title);
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

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    static SettingsAccountForm create(ControlPanel controlPanel, Settings.Account account, String title)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(account, "account");
	NullCheck.notNull(title, "title");
	return new SettingsAccountForm(controlPanel, account, title);
    }
}
