#!/usr/bin/env sh

set -e -u

ln -fs $PWD/maven $HOME/.m2

cd tomee-buildpack-resource-configuration
./mvnw -q -Dmaven.test.skip=true deploy
