#!/usr/bin/env bash
docker build --no-cache --progress plain -t graalvm-build-image:25.1.0 .
docker tag graalvm-build-image:25.1.0 graalvm-build-image:latest