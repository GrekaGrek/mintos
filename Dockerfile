FROM eclipse-temurin:17-jdk
COPY build/libs/java-0.0.1-SNAPSHOT.jar java.jar
ENTRYPOINT [ "java", "-jar", "java.jar" ]