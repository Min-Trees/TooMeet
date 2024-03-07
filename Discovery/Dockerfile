# Base image được sử dụng để build image
FROM openjdk:oraclelinux8

# Thông tin tác giả
LABEL authors="Min-Trees"

# Set working directory trong container
WORKDIR /app

# Copy file JAR được build từ ứng dụng Spring Boot vào working directory trong container
ARG JAR_FILE=target/discovery-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Expose port của ứng dụng
EXPOSE 8762

# Chỉ định command để chạy ứng dụng khi container khởi chạy
CMD ["java", "-jar", "app.jar"]