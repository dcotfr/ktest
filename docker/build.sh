#!/usr/bin/env bash
docker build --no-cache --progress plain -t graalvm-build-image:25.0.1 .
docker tag graalvm-build-image:25.0.1 graalvm-build-image:latest