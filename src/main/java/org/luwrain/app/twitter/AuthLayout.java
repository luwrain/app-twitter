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
import java.io.*;
import twitter4j.*;

import twitter4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

final class AuthLayout extends LayoutBase
{
    private final App app;
    private final Auth auth;
    private final FormArea formArea;

    AuthLayout(App app) throws TwitterException
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.auth = Tokens.createAuth();
	this.formArea = new FormArea(new DefaultControlContext(app.getLuwrain()), app.getStrings().appName()){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ENTER:
			    return onEnter();
			}
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() == SystemEvent.Type.REGULAR)
			switch(event.getCode())
			{
			case OK:
			    return onEnter();
			}
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
	    };
	fillInitialContent();
	    }

    private void fillInitialContent()
    {
	final App.TaskId taskId = app.newTaskId();
	app.runTask(taskId, ()->{
		final String url = auth.getAuthorizationURL();
		app.finishedTask(taskId, ()->{
			formArea.addStatic("line1", "");
			formArea.addStatic("line2", "  Добро пожаловать в Твиттер!");
			formArea.addStatic("line3", "");
			formArea.addStatic("line4", "Сейчас необходимо подключить учётную запись, которая будет");
			formArea.addStatic("line5", "использоваться для работы в Твиттере. Для этого требуется открыть");
			formArea.addStatic("line6", "ссылку, показанную ниже, в браузере, произвести вход в учётную запись");
			formArea.addStatic("line7", "и подтвердить, что разрешается работа приложения LUWRAIN с");
			formArea.addStatic("line8", "пользовательскими данными. После подтверждения будет предоставлен");
			formArea.addStatic("line9", "PIN-код, который, пожалуйста, введите ниже в соответствующее поле и");
			formArea.addStatic("line10", "нажмите Enter.  Для удобства приведённая ниже ссылка также сохранена в");
			formArea.addStatic("line11", "вашем домашнем каталоге.");
			formArea.addStatic("line12", "");
			formArea.addStatic("url", "Ссылка для авторизации:" + url);
			formArea.addEdit("pin", "PIN-код:", "");
			try {
			    final File file = new File(app.getLuwrain().getFileProperty("luwrain.dir.userhome"), "Подключение учётной записи Твиттера.txt");
			    final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			    try {
				w.write(url);
				w.newLine();
				w.flush();
			    }
			    finally {
				w.close();
			    }
			}
			catch(IOException e)
			{
			    app.getLuwrain().crash(e);
			}
		    });
	    });
    }

    private boolean onEnter()
    {
	if (app.isBusy())
	    return false;
	final String pin = formArea.getEnteredText("pin");
	if (!pin.matches("[0-9]+"))
	{
	    app.getLuwrain().message("PIN-код не может быть пустым и должен состоять из цифр", Luwrain.MessageType.ERROR);
	    return true;
	}
	final App.TaskId taskId = app.newTaskId();
	app.runTask(taskId, ()->{
		try {
		    auth.askForAccessToken(pin);
		}
		catch(TwitterException e)
		{
		    app.getLuwrain().crash(e);
		    return;
		}
		app.finishedTask(taskId, ()->app.authCompleted(auth.getAccessToken(), auth.getAccessTokenSecret()));
	    });
	
	return true;
    }

    AreaLayout getLayout()
    {
	return new AreaLayout(formArea);
    }
    }
