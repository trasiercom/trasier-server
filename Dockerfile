FROM ghcr.io/graalvm/graalvm-ce:21.3.0 as graalvm
COPY . /home/app/trasier-server
WORKDIR /home/app/trasier-server
RUN ./gradlew assemble
ADD build/libs/trasier-server-*-all.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
EXPOSE 8000