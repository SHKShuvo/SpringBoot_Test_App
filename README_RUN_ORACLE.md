# Run Guide - Spring Boot + Oracle

## 1) Requirements

- JDK 17
- Internet access for first Maven dependency download
- Oracle DB reachable from your PC/network/VPN

## 2) Maven build

Windows:

```bat
mvnw.cmd clean install
```

Linux/Mac:

```bash
chmod +x mvnw
./mvnw clean install
```

If you want to skip tests:

```bash
./mvnw clean install -DskipTests
```

## 3) Oracle DB configuration without code change

The project now supports environment variables. Change these on each PC according to that PC's Oracle DB/network.

### Windows CMD

```bat
set ORACLE_DB_URL=jdbc:oracle:thin:@//localhost:1521/orcl
set ORACLE_DB_USERNAME=your_username
set ORACLE_DB_PASSWORD=your_password
mvnw.cmd spring-boot:run
```

### Windows PowerShell

```powershell
$env:ORACLE_DB_URL="jdbc:oracle:thin:@//localhost:1521/orcl"
$env:ORACLE_DB_USERNAME="your_username"
$env:ORACLE_DB_PASSWORD="your_password"
.\mvnw.cmd spring-boot:run
```

### Linux/Mac

```bash
export ORACLE_DB_URL='jdbc:oracle:thin:@//localhost:1521/orcl'
export ORACLE_DB_USERNAME='your_username'
export ORACLE_DB_PASSWORD='your_password'
./mvnw spring-boot:run
```

## 4) Oracle URL examples

Service name format:

```text
jdbc:oracle:thin:@//HOST:PORT/SERVICE_NAME
```

SID format:

```text
jdbc:oracle:thin:@HOST:PORT:SID
```

Examples:

```text
jdbc:oracle:thin:@//localhost:1521/orcl
jdbc:oracle:thin:@localhost:1521:XE
```

## 5) Important files changed

- `pom.xml` - added explicit Lombok version in dependency and annotation processor path. This fixes Maven's blank/null version issue.
- `src/main/resources/application.properties` - changed DB settings to environment-variable based configuration.
- `src/main/java/com/example/test/Common/DBConnection.java` - changed manual Oracle connection to support env vars and safer error messages.
- `src/main/java/com/example/test/Common/LoggerManager.java` - removed hardcoded Windows-only log path.
- `src/test/java/com/example/test/TestApplicationTests.java` - made Maven build independent from live Oracle DB.

## 6) Swagger URL

After running the app:

```text
http://localhost:8091/swagger-ui/index.html
```
