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
import org.luwrain.core.events.*;

class ActionLists
{
    static Action[] getHomeTimelineActions(boolean withShowAccounts)
    {
	return new Action[]{
	    new Action("retweet", "Ретвитнуть"),//FIXME://FIXME:
	    new Action("like", "Поставить лайк"),//FIXME://FIXME:
	    new Action("delete-tweet", "Удалить твит", new KeyboardEvent(KeyboardEvent.Special.DELETE)),//FIXME://FIXME:
	    new Action("show-friends", "Показать друзей"),
	    new Action("show-likes", "Показать лайки"),
	    new Action("user-timeline", "Показать твиты другого пользователя", new KeyboardEvent(KeyboardEvent.Special.F5)),
	    new Action("search", "Поиск твитов", new KeyboardEvent(KeyboardEvent.Special.F6)),
	    new Action("change-account", "Сменить учётную запись", new KeyboardEvent(KeyboardEvent.Special.F10)),
	};
    }

    static Action[] getTweetsActions()
    {
	return new Action[]{
	    new Action("follow-author", "Отслеживать твиты автора", new KeyboardEvent(KeyboardEvent.Special.F9)),
	    new Action("user-timeline", "Показать твиты другого пользователя", new KeyboardEvent(KeyboardEvent.Special.F5)),
	    new Action("search", "Поиск твитов", new KeyboardEvent(KeyboardEvent.Special.F6)),
	};
    }

    static Action[] getFriendsActions(Object selected)
    {
	if (selected == null)
	    return new Action[0];
	return new Action[]{
	    new Action("delete-friendship", "Исключить из друзей", new KeyboardEvent(KeyboardEvent.Special.DELETE)),
	};
    }

    static Action[] getLikesActions(Object selected)
    {
	if (selected == null)
	    return new Action[0];
	return new Action[]{
	    new Action("cancel-like", "Отменить лайк", new KeyboardEvent(KeyboardEvent.Special.DELETE)),
	};
    }
}