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

import java.util.*;
import twitter4j.*;

import org.luwrain.core.*;

final class UserWrapper
{
final User user;

    UserWrapper(User user)
    {
	NullCheck.notNull(user, "user");
	this.user = user;
    }

    @Override public String toString()
    {
	return user.getName();
    }

    public String getName()
    {
	return user.getScreenName();
    }


    static UserWrapper[] create(List<User> users)
    {
	NullCheck.notNull(users, "users");
	final List<UserWrapper> wrappers = new LinkedList<UserWrapper>();
	for(User u: users)
	    wrappers.add(new UserWrapper(u));
	return wrappers.toArray(new UserWrapper[wrappers.size()]);
    }

        static UserWrapper[] create(User[] users)
    {
	NullCheck.notNullItems(users, "users");
	final List<UserWrapper> wrappers = new LinkedList<UserWrapper>();
	for(User u: users)
	    wrappers.add(new UserWrapper(u));
	return wrappers.toArray(new UserWrapper[wrappers.size()]);
    }
}
