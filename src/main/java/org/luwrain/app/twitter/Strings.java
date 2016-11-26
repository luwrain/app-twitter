/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import java.util.Date;

public class Strings
{
    static final String NAME = "luwrain.twitter";

    String appName()
    {
	return "Твиттер";
    }

    String youShouldConnect()
    {
	return "Необходимо выбрать учётную запись";
    }

    //    String noConnection();

    String problemConnecting()
    {
	return "Невозможно подключиться при помощи выбранной учётной записи";
    }

    String searchPopupName()
    {
	return "Поиск";
    }

    String searchPopupPrefix()
    {
	return "Фраза для поиска:";
    }

    String requestProblem()
    {
	return "При обработке запроса произошла неожиданная ошибка:";
    }

    String nothingFound()
    {
	return  "Ничего не найдено";
    }

    String postingSuccess()
    {
	return "Твит опубликован";
    }

    String userTweetsPopupName()
    {
	return "Твиты пользователя";
    }

    String userTweetsPopupPrefix()
    {
	return "Имя пользователя для показа твитов:";
    }

    String connectedAccount()
    {
	return "Подключённая учётная запись";
    }

    String account()
    {
	return "Учётная запись";
    }

    String numberOfFavorites(int num)
    {
	return "FIXME";
    }

    String numberOfRetweets(int num)
    {
	return "FIXME";
    }

    String passedTime(Date date)
    {
	return "FIXME";
    }

    String retweet()
    {
	return "Ретвит";
    }

    String accountsAreaName()
    {
	return "Учётные записи Твиттера";
    }

    String statusAreaName()
    {
	return "Ваш Твиттер";
    }

    String accountAuthPopupName()
    {
	return "Подключение новой учётной записи";
    }

    String accountAuthPopupText(String accountName)
    {
	return "Учётная запись \"" + accountName + "\" не подключена; подключить её сейчас?";
    }

    String accountAuthCompleted()
    {
	return "Учётная запись подключена";
    }

    String accessTokenFormName()
    {
	return "Подключение новой учётной записи";
    }

    String accessTokenFormGreeting()
    {
	return "ВНИМАНИЕ! Сейчас будет запрошена ссылка для подключения выбранной\\nучётной записи. Необходимо открыть эту ссылку в браузере, указать свой\\nлогин и пароль для входа в Твиттер, после чего\\nзапомнить предоставленный PIN-код.  PIN-код следует ввести в поле,\\nкоторое будет находиться под ссылкой. Для начала процедуры нажмите\\nENTER; для отмены действия нажмите ESCAPE.";
    }

    String accessTokenFormOpenUrl()
    {
	return "Откройте URL в браузере и сохраните полученный PIN-код:";
    }

    String accessTokenFormPin()
    {
	return "PIN-код:";
    }

    String accessTokenFormYouMustEnterPin()
    {
	return "Необходимо ввести PIN-код";
    }


}
