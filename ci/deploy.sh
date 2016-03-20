#!/usr/bin/env sh

set -e

cd tomee-buildpack-resource-configuration
./mvnw -q -Dmaven.test.skip=true deploy
