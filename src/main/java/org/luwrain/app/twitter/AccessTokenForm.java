
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

    private State state = null;
    private Auth auth = null;

    AccessTokenForm(Luwrain luwrain, TwitterApp app, Base base)
    {
	super(new DefaultControlEnvironment(luwrain), "Подключение новой учётной записи");
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(app, "app");
	NullCheck.notNull(base, "base");
	this.luwrain = luwrain;
	this.app = app;
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
	    /*
	    try {
		auth = base.createAuth();
	    }
	    catch(Exception e)
	    {
		luwrain.message(e.getMessage(), Luwrain.MESSAGE_ERROR);
		return true;
	    }
	    fillWaitingPid(auth.getAuthorizationURL());
	    */
	    fillWaitingPin("http://marigostra.ru");
	    state = State.WAITING_PIN;
	    return true;
	case WAITING_PIN:
	    if (getEnteredText("pin").trim().isEmpty())
	    {
		luwrain.message("Необходимо ввести PIN-код", Luwrain.MESSAGE_ERROR);
		return true;
	    }
	    app.endAccountAuth(true, "", "value1", "value2");
	}
	return true;
    }

    private void fillGreeting()
    {
	final String message = "ВНИМАНИЕ! Сейчас будет запрошена ссылка для подключения выбранной\\nучётной записи. Необходимо открыть эту ссылку в браузере, указать свой\\nлогин и пароль для входа в Твиттер, после чего\\nзапомнить предоставленный PIN-код.  PIN-код следует ввести в поле,\\nкоторое будет находиться под ссылкой. Для начала процедуры нажмите\\nENTER; для отмены действия нажмите ESCAPE.";
	addStatic("intro", "");
	int k = 1;
	for(String s: message.split("\\\\n", -1))
	    addStatic("intro" + (k++), s);
    }

    void fillWaitingPin(String url)
    {
	clear();
	addStatic("static1", "");
	addStatic("static2", "Open URL in browser");
	addStatic("static3", "");
	addStatic("static4", "URL: " + url);
	addEdit("pin", "PIN:", "");
    }
}
