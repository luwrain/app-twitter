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
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;

final class Conversations
{
    private final App app;
    private final Luwrain luwrain;
    private final Strings strings;

    Conversations(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.luwrain = app.getLuwrain();
	this.strings = app.getStrings();
    }

    String askUserNameToShowTimeline()
    {
final String res = Popups.text(luwrain, "Просмотр твитов пользователя", "Имя пользователя для просмотра твитов:", "");
if (res == null || res.isEmpty())
    return null;
return res;
    }

    String askSearchQuery()
    {
	final String res = Popups.text(luwrain, "Поиск твитов", "Выражение для поиска:", "");
if (res == null || res.isEmpty())
    return null;
return res;
    }


    Account chooseAnotherAccount()
    {
	final Object res = Popups.fixedList(luwrain, "Выберите учётную запись:", app.getAccounts());//FIXME:
	return (Account)res;
    }

    boolean confirmTweetDeleting(Tweet tweet)
    {
	NullCheck.notNull(tweet, "tweet");
	return Popups.confirmDefaultNo(luwrain, "Удаление твита", "Вы действительно хотите удалить твит \"" + tweet.getText() + "\"?");
    }

    boolean confirmLikeDeleting(Tweet tweet)
    {
	NullCheck.notNull(tweet, "tweet");
	return Popups.confirmDefaultNo(luwrain, "Отмена лайка", "Вы действительно хотите отменить лайк \"" + tweet.getText() + "\"?");
    }
}
