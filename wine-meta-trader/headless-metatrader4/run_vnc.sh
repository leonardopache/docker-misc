#!/usr/bin/env bash
# Break script on any non-zero status of any command
set -e

x11vnc -bg -nopw -rfbport 5900 -forever -xkb -o /tmp/x11vnc.log
sleep 10

# Wait end of all wine processes
/docker/waitonprocess.sh wineserver
