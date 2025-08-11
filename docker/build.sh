#!/usr/bin/env bash
docker build --no-cache --progress plain -t graalvm-build-image:24.0.2 .
docker tag graalvm-build-image:24.0.2 graalvm-build-image:latest