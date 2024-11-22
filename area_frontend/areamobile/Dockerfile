# Use an official OpenJDK image based on Ubuntu
FROM openjdk:17-jdk-bullseye

RUN mkdir /shared

# Install required packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Install cross-platform libraries
RUN apt-get update && apt-get install -y libc6-amd64-cross libgcc1-amd64-cross && ln -s /usr/x86_64-linux-gnu/lib64/ /lib64

# Install last version of node
RUN curl -fsSL https://deb.nodesource.com/setup_current.x | bash - \
    && apt-get install -y nodejs


# Set environment variables for Android SDK
ENV LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/lib64:/usr/x86_64-linux-gnu/lib"
ENV ANDROID_SDK_ROOT=/sdk
ENV ANDROID_HOME=/sdk
ENV PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/cmdline-tools/tools/bin
ENV JAVA_OPTS="-XX:-MaxFDLimit"
ENV GRADLE_OPTS="-XX:-MaxFDLimit"
ARG EXPO_TOKEN
ENV EXPO_TOKEN=${EXPO_TOKEN}

# Install the Android SDK command line tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && cd ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-6609375_latest.zip \
    && unzip commandlinetools-linux-6609375_latest.zip -d latest \
    && rm commandlinetools-linux-6609375_latest.zip

RUN cd ${ANDROID_SDK_ROOT}/cmdline-tools/ && mv latest/* . && rm -r latest

# Install SDK components and accept licenses
RUN yes | sdkmanager --licenses \
    && sdkmanager "build-tools;34.0.0" "platform-tools" "platforms;android-34"

# Set the working directory
WORKDIR /app

COPY . .

RUN npm install

RUN npx react-native build-android --tasks assembleRelease

RUN mv android/app/build/outputs/apk/release/app-release.apk android/app/build/outputs/apk/release/client.apk

RUN cp android/app/build/outputs/apk/release/client.apk /shared/

RUN ls /shared