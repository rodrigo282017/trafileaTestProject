# Getting Started

## To run the project

### Prerequisites

- Docker must be installed on your system. You can download it from [Docker's official website](https://www.docker.com/get-started).

## Usage

1. Clone this repository to your local machine.

2. Open a terminal and navigate to the project directory.

3. The project is configured to use jdk 17. If it is not configured, make sure to do it.

4. The postgres image is using port 5432. Make sure this connection is available before to use it, or change it in the compose.yaml file

5. Run the project

## Additional Details

- The Docker image configuration for PostgreSQL can be found in the `compose.yml` file in the project root directory.
- Make sure to customize the PostgreSQL configuration in the `compose.yml` file to suit your specific needs.
- [Docker Compose File](./compose.yaml)

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.3/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.3/gradle-plugin/reference/html/#build-image)
* [Docker Compose Support](https://docs.spring.io/spring-boot/docs/3.1.3/reference/htmlsingle/index.html#features.docker-compose)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.1.3/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.1.3/reference/htmlsingle/index.html#appendix.configuration-metadata.annotation-processor)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

* postgres: [`postgres:latest`](https://hub.docker.com/_/postgres)

Please review the tags of the used images and set them to the same as you're running in production.

