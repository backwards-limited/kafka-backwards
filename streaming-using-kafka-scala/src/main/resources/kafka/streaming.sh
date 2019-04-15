#!/usr/bin/env bash

TOPIC=$1
echo "Mockaroo data -> Topic ${TOPIC}"
echo "Q or q and Enter to Quit"

while :; do
  curl "https://api.mockaroo.com/api/ff3df5f0?count=1&key=adc016a0" | \
    awk '{print $$0;system("sleep 1");}' | kafka-console-producer --broker-list localhost:9092 --topic ${TOPIC}

  read -n 1 -t 1 INPUT
  if [[ ${INPUT} = "q" ]] || [[ ${INPUT} = "Q" ]]; then
    exit 1
  fi
done