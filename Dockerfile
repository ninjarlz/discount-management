FROM gradle:8.11.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

ARG profile
FROM eclipse-temurin:17
ENV TZ="Europe/Warsaw"
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/discount-management-0.0.1-SNAPSHOT.jar /usr/bin/discount-management/app.jar
WORKDIR /usr/bin/discount-management
CMD ["java", "-Dspring.profiles.active=dev", "-jar", "./app.jar"]