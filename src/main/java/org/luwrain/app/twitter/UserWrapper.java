
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
