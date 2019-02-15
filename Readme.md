## Spring Boot, PostgreSQL, JPA, Hibernate REST API Demo

## Tutorial

Forked from :
[callicoder repo](https://github.com/callicoder/spring-boot-postgresql-jpa-hibernate-rest-api-demo.git)

See blog at :
[Spring Boot, PostgreSQL, JPA, Hibernate RESTful CRUD API Example](https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/)

## Steps to Setup

**1. Clone the repository**

```bash
git clone https://github.com/bucklb/postgresJpa.git
```

**2. Configure PostgreSQL**

First, create a database named `pg-test`. Then, open `src/main/resources/application-POSTGRES.properties` file and change the spring datasource username and password as per your PostgreSQL installation.

During dev/test an H2 in memory database may suffice

**3. Run the app**

Decide if you want to use the in memory H2 database option, or use Postgres.  Set spring active profile to match (H2 or POSTGRES)

Type the following command from the root directory of the project to run it -

```bash
SPRING_PROFILES_ACTIVE=H2 mvn spring-boot:run
```

Alternatively, you can package the application in the form of a JAR file and then run it like so -

```bash
mvn clean package
java -jar -Dspring.profiles.active=H2 target/postgres-demo-0.0.1-SNAPSHOT.jar
```


If using H2 can use the console to inspect/interact with the data.  Modifications made to the relevant properties file (the H2 one) to allow this.  
Console on localhost:8888/h2-console
If challenged for logon then use URL=jdbc:h2:mem:pg-test, user=sa, no password

Add to properties:
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

More details on 
https://medium.com/@harittweets/how-to-connect-to-h2-database-during-development-testing-using-spring-boot-44bbb287570





