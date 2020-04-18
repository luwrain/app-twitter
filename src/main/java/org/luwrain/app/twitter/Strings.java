
package org.luwrain.app.twitter;

import java.util.Date;

public interface Strings
{
    static final String NAME = "luwrain.twitter";

    String accessTokenFormGreeting();
    String accessTokenFormName();
    String accessTokenFormOpenUrl();
    String accessTokenFormPin();
    String accessTokenFormYouMustEnterPin();
    String account();
    String accountAddedSuccessfully(String accountName);
    String accountAlreadyExists(String name);
    String accountAuthCompleted();
    String accountAuthPopupName();
    String accountAuthPopupText(String accountName);
    String accountDeletedSuccessfully(String accountName);
    String actionAddAccount();
    String actionDeleteAccount();
    String addAccountPopupName();
    String addAccountPopupPrefix();
    String appName();
    String connectedAccount();
    String deleteAccountPopupName();
    String deleteAccountPopupText(String accountName);
    String invalidAccountName();
    String problemConnecting();
    String statusAreaName();
    String userTimelineAreaName(String userName);
    String youShouldConnect();

    String postAreaName();
    String actionDeleteTweet();
    String actionSearch();
    String search();
    String searchAreaName();
}
