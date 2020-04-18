
package org.luwrain.app.twitter;

import org.luwrain.core.*;

import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.template.*;

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
