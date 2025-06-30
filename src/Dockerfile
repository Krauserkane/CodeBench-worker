FROM ubuntu:latest AS build-env

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates git libcap-dev libsystemd-dev pkg-config python3 gcc g++ openjdk-17-jdk-headless \
     build-essential make && \
    rm -rf /var/lib/apt/lists/*

RUN git clone https://github.com/ioi/isolate.git /tmp/isolate

RUN cd /tmp/isolate && make -j$(nproc) install && rm -rf /tmp/*

FROM eclipse-temurin:17
#

RUN apt-get update && apt-get install -y python3 nano gcc g++ && rm -rf /var/lib/apt/lists/*

# Copying isolate from builder stage
COPY --from=build-env /usr/local/bin/isolate /usr/local/bin/isolate
COPY --from=build-env /usr/local/sbin/isolate-cg-keeper /usr/local/sbin/isolate-cg-keeper
COPY --from=build-env /usr/local/etc/isolate /usr/local/etc/isolate

RUN apt-get update && apt-get install -y docker.io


WORKDIR /app

COPY worker-0.0.1-SNAPSHOT.jar target.jar

ENTRYPOINT ["java","-jar","target.jar"]


