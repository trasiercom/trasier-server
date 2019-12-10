FROM oracle/graalvm-ce:19.2.1 as graalvm
COPY . /home/app/trasier-server
WORKDIR /home/app/trasier-server
RUN ./gradlew assemble
RUN gu install native-image
RUN native-image --no-server -cp build/libs/trasier-server-*-all.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/trasier-server/trasier-server .
ENTRYPOINT ["./trasier-server"]
