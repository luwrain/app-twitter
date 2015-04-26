/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.twitter;

import org.luwrain.core.Application;
import org.luwrain.core.Shortcut;
import org.luwrain.core.Command;
import org.luwrain.core.CommandEnvironment;
import org.luwrain.core.I18nExtension;
import org.luwrain.core.Luwrain;
import org.luwrain.core.Registry;

public class Extension extends org.luwrain.core.EmptyExtension
{
    @Override public Command[] getCommands(CommandEnvironment env)
    {
	Command res = new Command(){
		@Override public String getName()
		{
		    return "twitter";
		}
		@Override public void onCommand(CommandEnvironment env)
		{
		    env.launchApp("twitter");
		}
	    };
	return new Command[]{res};
    }

    @Override public Shortcut[] getShortcuts()
    {
	Shortcut res = new Shortcut() {
		@Override public String getName()
		{
		    return "twitter";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    return new Application[]{new TwitterApp()};
		}
	    };
	return new Shortcut[]{res};
    }

    @Override public void i18nExtension(I18nExtension i18nExt)
    {
	i18nExt.addCommandTitle("en", "twitter", "Twitter");
	i18nExt.addCommandTitle("ru", "twitter", "Твиттер");
i18nExt.addStrings("ru", TwitterApp.STRINGS_NAME, new org.luwrain.app.twitter.i18n.Ru());
    }
}
