FROM eclipse-temurin:17

WORKDIR /app

# Copy everything (including src and lib)
COPY . .

# Compile all Java files with classpath including all jars in lib/
RUN javac -cp "lib/*" -d out $(find src -name "*.java")

EXPOSE 8080

# Run the server with classpath including compiled classes + jars
CMD ["java", "-cp", "out:lib/*", "core.HttpServerMain"]
