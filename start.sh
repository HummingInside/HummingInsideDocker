#!/usr/bin/env bash
service nginx start;
cd /humming/backend_dist/;
/usr/bin/java -jar -Dspring.profiles.active=dev /humming/backend_dist/humminginside-0.0.2-SNAPSHOT.jar
