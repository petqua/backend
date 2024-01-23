FROM amazoncorretto:17-alpine-jdk

ARG JAR_FILE=build/libs/petqua-0.0.1.jar

ADD ${JAR_FILE} petqua.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-jar", "/petqua.jar"]
