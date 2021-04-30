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
import org.luwrain.app.base.*;

final class StartingLayout extends LayoutBase
{
    private final App app;
    private final FormArea formArea;
    private Auth auth = null;

    StartingLayout(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.formArea = new FormArea(new DefaultControlContext(app.getLuwrain()), app.getStrings().accessTokenFormName()){
	    };
		formArea.addStatic("intro", "");
	int k = 1;
	for(String s: app.getStrings().accessTokenFormGreeting().split("\\\\n", -1))
	    formArea.addStatic("intro" + (k++), s);
	formArea.addEdit("pin", app.getStrings().accessTokenFormPin(), "");
    }
}
