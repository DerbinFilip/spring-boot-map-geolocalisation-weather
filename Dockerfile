FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu as build
WORKDIR /workspace/app

COPY *app/target/*.jar /workspace/app/app.jar

RUN java -Djarmode=layertools -jar app.jar extract

FROM gcr.io/distroless/java17-debian11:nonroot
VOLUME /tmp
WORKDIR /workspace/app

ENV DEPENDENCY=/workspace/app
COPY --from=build ${DEPENDENCY}/dependencies/ ./
COPY --from=build ${DEPENDENCY}/spring-boot-loader/ ./
COPY --from=build ${DEPENDENCY}/snapshot-dependencies/ ./

RUN ["java", "--version"]
COPY --from=build ${DEPENDENCY}/application/ ./

USER 1000
ENTRYPOINT ["java", \
    "-cp", ".", "org.springframework.boot.loader.launch.JarLauncher"]