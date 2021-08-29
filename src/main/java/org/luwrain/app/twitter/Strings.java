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

import java.util.Date;

public interface Strings
{
    static final String NAME = "luwrain.twitter";

    String accessTokenFormGreeting();
    String accessTokenFormName();
    String accessTokenFormPin();
    String account();
    String accountAddedSuccessfully(String accountName);
    String accountAlreadyExists(String name);
    String accountDeletedSuccessfully(String accountName);
    String actionAddAccount();
    String actionDeleteAccount();
    String actionDeleteTweet();
    String actionFollow();
    String actionLike();
    String actionSearch();
    String actionSearchUsers();
    String addAccountPopupName();
    String addAccountPopupPrefix();
    String appName();
    String connectedAccount();
    String deleteAccountPopupName();
    String deleteAccountPopupText(String accountName);
    String invalidAccountName();
    String postAreaName();
    String search();
    String searchAreaName();
    String searchUsersAreaName();
    String searchUsersInputPrefix();
    String statusAreaName();

    String actionDeleteFollowing();
    String actionStatus();
}
