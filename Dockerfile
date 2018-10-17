FROM openjdk:11-jdk-sid

ENV SBT_VERSION 1.2.6

VOLUME /tmp

COPY . /

RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt

RUN \
  sh -c "cd /" && \
  sh -c "sbt clean assembly"

WORKDIR /