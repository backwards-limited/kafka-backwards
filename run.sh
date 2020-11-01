#!/bin/bash

project=$1

docker-compose up -d

sbt "; project $project; run"

docker-compose down