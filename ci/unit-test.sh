#!/usr/bin/env bash

set -e

pushd tomee-buildpack-resource-configuration
  ./mvnw -q package
popd
