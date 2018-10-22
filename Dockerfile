FROM openjdk:11.0.1-jre-sid

VOLUME /tmp

COPY beginners-course/target/scala-2.12/beginners-course.jar /

WORKDIR /