
package org.luwrain.app.twitter;

import org.luwrain.core.*;

public class Extension extends org.luwrain.core.extensions.EmptyExtension
{
    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[]{
	    new Command(){
		@Override public String getName()
		{
		    return "twitter";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    luwrain.launchApp("twitter");
		}
	    }};
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	return new Shortcut[]{
	    new Shortcut(){
		@Override public String getName()
		{
		    return "twitter";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    return new Application[]{new TwitterApp()};
		}
	    }};
    }

    @Override public org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return new org.luwrain.cpanel.Factory[]{new SettingsFactory(luwrain)};
    }



}
