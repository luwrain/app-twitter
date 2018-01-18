/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class AccessTokenForm extends FormArea
{
    enum State {GREETING, WAITING_PIN};

    private final Luwrain luwrain;
    private final TwitterApp app;
    private final Base base;
    private final Strings strings;

    private State state = null;
    private Auth auth = null;

    AccessTokenForm(Luwrain luwrain, TwitterApp app, 
		    Strings strings, Base base)
    {
	super(new DefaultControlEnvironment(luwrain), strings.accessTokenFormName());
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(app, "app");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	this.luwrain = luwrain;
	this.app = app;
	this.strings = strings;
	this.base = base;
	reset();
    }

    void reset()
    {
	auth = null;
	state = State.GREETING;
	fillGreeting();
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	{
	case ENTER:
	    return onEnter();
	}
	return super.onKeyboardEvent(event);
    }

    private boolean onEnter()
    {
	switch(state)
	{
	case GREETING:
	    try {
		auth = base.createAuth();
	    }
	    catch(Exception e)
	    {
		luwrain.message(e.getMessage(), Luwrain.MessageType.ERROR);
		return true;
	    }
	    fillWaitingPin(auth.getAuthorizationURL());
	    state = State.WAITING_PIN;
	    return true;
	case WAITING_PIN:
	    if (getEnteredText("pin").trim().isEmpty())
	    {
		luwrain.message(strings.accessTokenFormYouMustEnterPin(), Luwrain.MessageType.ERROR);
		return true;
	    }
	    try {
		auth.askForAccessToken(getEnteredText("pin"));
	    }
	    catch(Exception e)
	    {
		luwrain.message(e.getMessage(), Luwrain.MessageType.ERROR);
		return true;
	    }
	    app.endAccountAuth(true, "", auth.getAccessToken(), auth.getAccessTokenSecret());
	}
	return true;
    }

    private void fillGreeting()
    {
	final String message = strings.accessTokenFormGreeting();
	addStatic("intro", "");
	int k = 1;
	for(String s: message.split("\\\\n", -1))
	    addStatic("intro" + (k++), s);
    }

    void fillWaitingPin(String url)
    {
	clear();
	addStatic("intro", "");
	addStatic("intro1", strings.accessTokenFormOpenUrl());
	addStatic("static4", "URL: " + url);
	addEdit("pin", strings.accessTokenFormPin(), "");
    }
}
