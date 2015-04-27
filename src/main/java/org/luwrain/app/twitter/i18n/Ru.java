/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.twitter.i18n;

public class Ru implements org.luwrain.app.twitter.Strings
{
    @Override public String appName()
    {
	return "Твиттер";
    }

    @Override public String searchSectionsItem()
    {
	return "Поиск";
    }

    @Override public String postSectionsItem()
    {
	return "Новый твит";
    }

    @Override public String homeTweetsSectionsItem()
    {
	return "Опубликованные твиты";
    }

    @Override public String userTweetsSectionsItem()
    {
	return "Твиты других пользователей";
    }


    @Override public String noConnection()
    {
	return "Необходимо выбрать учётную запись для работы";
    }

    @Override public String problemConnecting()
    {
	return "Невозможно подключиться к выбранной учётной записи";
    }

    @Override public String searchPopupName()
    {
	return "Поиск";
    }

    @Override public String searchPopupPrefix()
    {
	return "Введите фразу для поиска:";
    }

    @Override public String problemSearching()
    {
	return "Произошла ошибка при поиске";
    }

    @Override public String nothingFound()
    {
	return "Поиск по указанной фразе не дал результатов";
    }

    @Override public String postPopupName()
    {
	return "Новый твит";
    }

    @Override public String postPopupPrefix()
    {
	return "Введите текст нового твита:";
    }

    @Override public String postingSuccess()
    {
	return "Новый твит успешно опубликован";
    }

    @Override public String problemPosting()
    {
	return "Произошла ошибка при публикации нового твита";
    }

    @Override public String problemHomeTweets()
    {
	return "Произошла ошибка при получении опубликованных твитов";
    }

    @Override public String userTweetsPopupName()
    {
	return "Просмотр твитов";
    }

    @Override public String userTweetsPopupPrefix()
    {
	return "Введите имя пользователя для просмотра твитов:";
    }

    @Override public String problemUserTweets()
    {
	return "Произошла ошибка при получении твитов пользователя";
    }

    @Override public String noUserTweets()
    {
	return "Пользователь не имеет опубликованных твитов";
    }
}
