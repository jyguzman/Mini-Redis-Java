#!/bin/sh
set -e
tmpFile=$(mktemp -d)
javac -sourcepath src/main/java src/main/java/Redis/RedisClient.java -d "$tmpFile"
jar cf java_redis_client.jar -C "$tmpFile"/ .
exec java -cp java_redis_client.jar Redis.RedisClient "$@"
