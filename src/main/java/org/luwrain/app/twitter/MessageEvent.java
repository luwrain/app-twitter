

package org.luwrain.app.twitter;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

class MessageEvent extends ThreadSyncEvent
{
    public String text = "";
    public int type;

    public MessageEvent(Area destArea, String text, int type)
    {
	super(destArea);
	this.text = text;
	this.type = type;
    }
}
