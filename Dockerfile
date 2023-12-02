# With this file we create a Docker image that contains the application
FROM gradle:7-jdk17 AS build
# We create a directory for the application and copy the build.gradle file
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# Update and install required OS packages to continue
# Note: Playwright is a system for running browsers, and here we use it to
# install Chromium.
#RUN apt-get update \
   # && apt-get install -y curl gnupg unzip wget \
   # && curl -sL https://deb.nodesource.com/setup_19.x | bash - \
   # && apt-get install -y nodejs \
   # && npm init -y \
   # && npx playwright install --with-deps chromium


# Decrease Gradle memory usage to avoid OOM situations in tight environments
# (many free Cloud tiers only give you 512M of RAM). The following amount
# should be more than enough to build and export our site.
#RUN mkdir ~/.gradle && \
   # echo "org.gradle.jvmargs=-Xmx256m" >> ~/.gradle/gradle.properties

# We create a new image with the application
FROM openjdk:17-jdk-slim-buster
EXPOSE 8080:8080
#EXPOSE 8083:8082
# Directory to store the application
RUN mkdir /app
# Copy the certificate to the container (if it is necessary)
#RUN mkdir /cert
#COPY --from=build /home/gradle/src/cert/* /cert/

# Copy the jar file to the container
COPY --from=build /home/gradle/src/build/libs/*.jar /app/mayorca-server-api.jar
ENTRYPOINT ["java","-jar","/app/mayorca-server-api.jar"]