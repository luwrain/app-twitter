/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

class StatusArea extends NavigationArea implements  EmbeddedEditLines
{
    protected Listener listener = null;
    protected final EmbeddedSingleLineEdit edit;
    protected String enteringPrefix = "";
    protected String enteringText = "";

    protected TweetWrapper[] tweets = new TweetWrapper[0];

StatusArea(ControlEnvironment environment)
    {
	super(environment);
	edit = new EmbeddedSingleLineEdit(environment, this, this, 0, 0);
    }

    void setTweets(TweetWrapper[] tweets)
    {
	NullCheck.notNullItems(tweets, "tweets");
	this.tweets = tweets;
	updateEditPos();
	environment.onAreaNewContent(this);
    }

    void setListener(Listener listener)
    {
	this.listener = listener;
    }

    void setEnteringPrefix(String prefix)
    {
	NullCheck.notNull(prefix, "prefix");
	this.enteringPrefix = prefix;
	updateEditPos();
	environment.onAreaNewContent(this);
    }

    @Override public int getLineCount()
    {
	return tweets.length + 2;
    }

    @Override public String getLine(int index)
    {
	if (index == 0 )
	    return enteringPrefix + enteringText;
	if (index >= 1 && index - 1 < tweets.length)
	    return tweets[index - 1].toString();
	return "";
    }

    @Override public String getAreaName()
    {
	return "Хронология";
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.isPosCovered(getHotPointX(), getHotPointY()))
	    if (event.isSpecial() && !event.isModified())
		switch(event.getSpecial())
		{
		case ENTER:
		    return onEnterInEdit();
		}
	if (edit.isPosCovered(getHotPointX(), getHotPointY()) && edit.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.isPosCovered(getHotPointX(), getHotPointY()) && edit.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.isPosCovered(getHotPointX(), getHotPointY()) && edit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

@Override public void setEmbeddedEditLine(int x, int y, String line)
    {
	NullCheck.notNull(line, "line");
	enteringText = line;
    }

@Override public String getEmbeddedEditLine(int x,int y)
    {
	return enteringText;
    }

    protected boolean onEnterInEdit()
    {
	if (enteringText.isEmpty())
	    return false;
	    listener.onNewEnteredMessage(enteringText);
	enteringText = "";
	environment.onAreaNewContent(this);
	//	setHotPoint(enteringPrefix.length(), contact.getMessages().lastMessages().size());
	return true;
    }

	protected void updateEditPos()
    {
	/*
		if (contact==null) return;
		if (contact.getMessages()==null) return;
	*/
	edit.setNewPos(enteringPrefix.length(), 0);
    }

	interface Listener 
	{
	    boolean onNewEnteredMessage(String text);
	}
}
