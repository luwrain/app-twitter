#!/bin/sh

java -cp ../lib/twitter4j-core-4.0.1.jar:../jar/luwrain-app-twitter.jar org.luwrain.app.twitter.Auth "$@"
