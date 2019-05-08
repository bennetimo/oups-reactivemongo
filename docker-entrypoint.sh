#!/usr/bin/env bash

echo "Restoring oups-test mongo collection"

mongorestore --host mongodb ./dump

/usr/local/bin/amm oups.sc