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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

class StatusArea extends ConsoleArea2
{
    StatusArea(ControlEnvironment context, Model model, ClickHandler clickHandler, InputHandler inputHandler)
    {
	super(createParams(context, model, clickHandler, inputHandler));
    }

    void setTweets(TweetWrapper[] tweets)
    {
	NullCheck.notNullItems(tweets, "tweets");
    }

    static private Params createParams(ControlEnvironment context, Model model, ClickHandler clickHandler, InputHandler inputHandler)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(model, "model");
	NullCheck.notNull(clickHandler, "clickHandler");
	NullCheck.notNull(inputHandler, "inputHandler");
	final Params params = new Params();
	params.context = context;
	params.areaName = "Твиттер";
	params.model = model;
	params.appearance = new StatusAppearance(context);
	params.clickHandler = clickHandler;
	params.inputHandler = inputHandler;
params.inputPos = InputPos.TOP;
	return params;
    }

static private class StatusAppearance implements ConsoleArea2.Appearance
{
    private final ControlEnvironment context;
    StatusAppearance(ControlEnvironment context)
    {
	NullCheck.notNull(context, "context");
	this.context = context;
    }
    @Override public String getTextAppearance(Object item)
    {
	NullCheck.notNull(item, "item");
	return item.toString();
    }
    @Override public void announceItem(Object item)
    {
	NullCheck.notNull(item, "item");
	if (!(item instanceof TweetWrapper))
	{
	context.playSound(Sounds.LIST_ITEM);
	context.say(item.toString());
	return;
	}
	final TweetWrapper wrapper = (TweetWrapper)item;
	if (wrapper.tweet.isFavorited())
	context.playSound(Sounds.PARAGRAPH); else
	context.playSound(Sounds.LIST_ITEM);
	context.say(wrapper.getText() + " " + wrapper.getUserName() + " " + context.getI18n().getPastTimeBrief(wrapper.getDate()) + " " + wrapper.getFavoriteCount());
    }
}
}
