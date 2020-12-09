FROM hseeberger/docker-sbt

RUN mkdir -p /overlaysim

WORKDIR /overlaysim
COPY . /overlaysim