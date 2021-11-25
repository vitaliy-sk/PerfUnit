### How to use

#### Build

```
./gradlew build jar
```

#### Connect agent

```
-javaagent:/xxxx/perfunit/build/libs/perfunit-1.0-SNAPSHOT.jar=${CONFIG_PATH}
```

#### Debug agent

```
-agentlib:jdwp=transport=dt_socket,server=y,address=10110,suspend=y
```

#### Run test

```
-javaagent:./build/libs/perfunit-1.0-SNAPSHOT.jar=./src/test/resources/config-sample.yml
```

#### Trace ID

https://github.com/opentracing/specification/blob/master/rfc/trace_identifiers.md
