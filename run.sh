#!/bin/bash

project=$1

docker-compose up -d

sbt "; project $project; run ${*:2}"

docker-compose down