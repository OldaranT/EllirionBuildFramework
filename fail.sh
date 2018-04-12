#!/usr/bin/env bash
echo 'Sending Discord Webhook';
export BACKTICK='`';
export TIMESTAMP=$(date --utc +%FT%TZ);
export COMMIT_FORMATTED="[$BACKTICK${TRAVIS_COMMIT:0:7}$BACKTICK](https://github.com/OldaranT/EllirionBuildFramework/commit/$TRAVIS_COMMIT)";
declare -A authors=( ["Joris Klein Tijssink"]="66160074304786432" ["Richard de Vries"]="290484264376729610" ["OldaranT"]="224691335481786368" ["Chris Coerdes"]="194476736996573185" ["nikro412956"]="202843805857218560" )
curl -v -H User-Agent:bot -H Content-Type:application/json -d '{"avatar_url":"https://i.imgur.com/kOfUGNS.png","username":"Travis CI","content":"<@'${authors[$AUTHOR_NAME]}'>","embeds":[{"author":{"name":"Build #'"$TRAVIS_BUILD_NUMBER"' Failed - '"$AUTHOR_NAME"'","url":"https://travis-ci.com/'"$REPO_OWNER"'/'"$REPO_NAME"'/builds/'"$TRAVIS_BUILD_ID"'"},"url":"https://github.com/'"$REPO_OWNER"'/'"$REPO_NAME"'/commit/'"$TRAVIS_COMMIT"'","title":"['"$TRAVIS_REPO_SLUG"':'"$TRAVIS_BRANCH"'] ","color":16711680,"fields":[{"name":"_ _", "value": "'"$COMMIT_FORMATTED"' - '"@everyone $TRAVIS_COMMIT_MESSAGE"'"}],"timestamp":"'"$TIMESTAMP"'","footer":{"text":"Travis CI"}}]}' $DISCORD_WEBHOOK_URL;
