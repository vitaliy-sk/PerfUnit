### How to use

#### Build

```
./gradlew build jar
```

#### Connect agent

```
-javaagent:/xxxx/perfunit/build/libs/perfunit-1.0-SNAPSHOT.jar
```

#### Debug agent

```
-agentlib:jdwp=transport=dt_socket,server=y,address=10110,suspend=y
```
