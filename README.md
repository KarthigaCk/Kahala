# Kalaha
This is  Kalaha game. Hope you enjoy playing!

## Requirements

For building and running the application you need:

- [JDK 11](https://adoptium.net/temurin/releases/?version=11) and [JAVA_HOME](https://docs.oracle.com/cd/E19182-01/821-0917/inst_jdk_javahome_t/index.html#:~:text=To%20set%20JAVA_HOME%2C%20do%20the,6.0_02.) set in environmental variables
- [Maven 3](https://maven.apache.org) optional as MVN wrapper is added in source code.
- Open Command Prompt pointing to root director of the source code location.

```shell
.\mvnw test
```
## Tech-Stack
- Java 11
- Maven
- Spring-Boot
- Spring-Data-JPA
- In-Memory Database H2
- Git Hub
- Angular11
- HTML5
- CSS
## High Level Flow diagram

![KahalaSequenceDiagram](https://github.com/KarthigaCk/Kalaha/assets/139973861/6d32a1a0-c849-4bda-9847-b92a69e9bb43)

## Steps to Run the application

### Running the application

#### Option 1: Running the application from IDE
- One way is to execute the main method in the com.abc.assesment.kalaha.KalahaApplication class from your IDE.
#### Option 2: Maven way of running
- Open command prompt(cmd) or terminal
- Navigate to the project folder
- Run command `mvn clean install` to build the application
- Once the build is successful, then run command `mvn spring-boot:run` to run the application

## How to Test the application
## From UI
- After the application is started, Open the URL in your browser : http://localhost:8080/#/
- Player can create a new game and start playing.
### GameController
- Endpoint 1: `POST /kahalaGame`
    - Allows user to create new game
- Endpoint 2: `GET /kahalaGame/{gameid}`
    - Allows user to get game details
- Endpoint 3: `POST /kahalaGame/playGame`
    - Allows user to play the game with post request body
 ### Swagger UI
  - After the application is started, Open the URL in your browser : [http://localhost:8080](http://localhost:8080/swagger-ui/index.html)
 - The below swagger UI will be displayed with all the application specification
   ![image](https://github.com/KarthigaCk/Kalaha/assets/139973861/a5403bf2-6098-4afb-a402-325e1d750ee6)


### Postman Collection
- /Assignment/postman/Kalaha.postman_collection.json
