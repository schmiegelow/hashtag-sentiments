FROM docker.dc.xing.com/xing/ubuntu:xenial
WORKDIR /build

RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
	apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && \
	apt-get update && apt-get install -y scala sbt

RUN curl -sL https://deb.nodesource.com/setup_9.x | bash -
RUN apt-get install -y nodejs

COPY . /build

RUN sbt fullOptJS::webpack


FROM nginx
ARG GIT_SHA1=-1
LABEL "com.xing.git.sha1"=${GIT_SHA1}
COPY --from=0 /build /usr/share/nginx/html
