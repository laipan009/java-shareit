FROM amazoncorretto:11-alpine-jdk
WORKDIR /app
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8061" \
    TZ="Europe/Moscow"
COPY target/*.jar /app/shareit-gateway.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/shareit-gateway.jar"]