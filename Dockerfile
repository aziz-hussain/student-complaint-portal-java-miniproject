FROM openjdk:17-slim

WORKDIR /app

COPY . /app

RUN find . -name "*.java" > sources.txt

RUN javac -cp "lib/*" @sources.txt

EXPOSE 8080

CMD ["java", "-cp", ".:lib/*", "core.HttpServerMain"]
