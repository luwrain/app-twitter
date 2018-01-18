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
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;

class Conversations
{
    private final Luwrain luwrain;
    private final Base base;
    private final Strings strings;

    Conversations(Luwrain luwrain, Base base, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(base, "base");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.base = base;
	this.strings = strings;
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
	final Object res = Popups.fixedList(luwrain, "Выберите учётную запись:", base.getAccounts());//FIXME:
	return (Account)res;
    }

    boolean confirmTweetDeleting(TweetWrapper tweet)
    {
	NullCheck.notNull(tweet, "tweet");
	return Popups.confirmDefaultNo(luwrain, "Удаление твита", "Вы действительно хотите удалить твит \"" + tweet.getText() + "\"?");
    }

    boolean confirmLikeDeleting(TweetWrapper tweet)
    {
	NullCheck.notNull(tweet, "tweet");
	return Popups.confirmDefaultNo(luwrain, "Отмена лайка", "Вы действительно хотите отменить лайк \"" + tweet.getText() + "\"?");
    }
}
