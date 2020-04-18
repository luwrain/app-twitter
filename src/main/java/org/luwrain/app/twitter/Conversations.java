/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class Conversations
{
    private final Luwrain luwrain;
    private final Strings strings;

    Conversations(App app)
    {
	NullCheck.notNull(app, "app");
	this.luwrain = app.getLuwrain();
	this.strings = app.getStrings();
    }

    String askUserNameToShowTimeline()
    {
final String res = Popups.simple(luwrain, "Просмотр твитов пользователя", "Имя пользователя для просмотра твитов:", "");
if (res == null || res.isEmpty())
    return null;
return res;
    }

    String askSearchQuery()
    {
	final String res = Popups.simple(luwrain, "Поиск твитов", "Выражение для поиска:", "");
if (res == null || res.isEmpty())
    return null;
return res;
    }


    Account chooseAnotherAccount()
    {
	final Object res = Popups.fixedList(luwrain, "Выберите учётную запись:", Base.getAccounts(luwrain));//FIXME:
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
