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

    String actionAddAccount()
    {
	return "Добавить учётную запись";
    }

    String actionDeleteAccount()
    {
	return "Удалить учётную запись";
    }

    String addAccountPopupName()
    {
	return "Добавление учётной записи";
    }

    String addAccountPopupPrefix()
    {
	return "Имя новой учётной записи:";
    }

    String invalidAccountName()
    {
	return "Имя учётной записи содержит неверные символы";
    }

    String accountAlreadyExists(String name)
    {
	return "Учётная запись \"" + name + "\" уже существует";
    }

    String accountAddedSuccessfully(String accountName)
    {
	return "Учётная запись \"" + accountName + "\" добавлена";
    }

    String deleteAccountPopupName()
    {
	return "Удаление учётной записи";
    }

    String deleteAccountPopupText(String accountName)
    {
	return "Вы действительно хотите удалить учётную запись Твиттера \"" + accountName + "\"?";
    }

    String accountDeletedSuccessfully(String accountName)
    {
	return "Учётная запись Твиттера \"" + accountName + "\" удалена";
    }

    String published(String text)
    {
	return "Опубликовано \"" + text + "\"";
    }




}
