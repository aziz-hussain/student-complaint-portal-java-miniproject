FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN javac -d out $(find src -name "*.java")

EXPOSE 8080

CMD ["java", "-cp", "out", "core.HttpServerMain"]
