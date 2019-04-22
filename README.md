```
      :::    :::       ::::::::::           :::        :::    :::::::::::       :::    ::: 
     :+:   :+:        :+:                :+: :+:      :+:        :+:           :+:    :+:  
    +:+  +:+         +:+               +:+   +:+     +:+        +:+           +:+    +:+   
   +#++:++          +#++:++#         +#++:++#++:    +#+        +#+           +#++:++#++    
  +#+  +#+         +#+              +#+     +#+    +#+        +#+           +#+    +#+     
 #+#   #+#        #+#              #+#     #+#    #+#        #+#           #+#    #+#      
###    ###       ##########       ###     ###    ########## ###           ###    ###  
```
[![CircleCI](https://circleci.com/gh/marioalvial/kealth.svg?style=svg)](https://circleci.com/gh/marioalvial/kealth)
[![codecov](https://codecov.io/gh/marioalvial/kealth/branch/master/graph/badge.svg)](https://codecov.io/gh/marioalvial/kealth)
![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/io.github.marioalvial/kealth/1.0.1.svg)

Health check for external dependencies in Kotlin

## Installation

#### Maven:
```
<repositories>
    <repository>
        <id>central</id>
        <name>Central Repository</name>
        <url>http://repo.maven.apache.org/maven2</url>
    </repository>
</repositories>

<dependency>
    <groupId>io.github.marioalvial</groupId>
    <artifactId>kealth</artifactId>
    <version>1.0.2</version>
</dependency>
```

#### Gradle:
```
repositories {
    mavenCentral()
}

dependencies {
    compile 'io.github.marioalvial:kealth:1.0.2'
}    
```

## Getting Started

1. Create your component:

```
class HealthComponentA : HealthComponent() {

    override val name = "component A"

    override suspend fun doHealthCheck(): HealthStatus {
        val result = doHealthCheckCallToComponentAService()
        return if(result) HealthStatus.HEALTHY else HealthStatus.UNHEALTHY
    }

    override suspend fun handleFailure(throwable: Throwable?) {
        sendAlert()
    }
}
```

2. Instantiate HealthAggregator:
```
val aggregator = HealthAggregator(listOf(HealthComponentA()))
```

3. Execute health method:
```
val componentMap = runBlocking { aggregator.aggregate() }
```

**Make sure to execute aggregate() method on a coroutine or suspend function.**

## Handle Failure

handleFailure method of your health component will be trigger only if:

- doHealthCheck() call returns HealthStatus.UNHEALTHY (in this case the throwable parameter will be null)

- doHealthCheck() call throws exception (in this case the throwable parameter will be the exception thrown)


## How it works

When aggregator.aggregate() is called it will execute all components health() method concurrently and it will create a map with the name of the component as key and health status as value.

If doHealthCheck() method throws exception or returns HealthStatus.UNHEALTHY the handleFailure() method will be executed asynchronously.

## Continuous Integration and Test Coverage

Test Coverage configured on CodeCov. Checkout the [test coverage here](https://codecov.io/gh/marioalvial/kealth).

Continuous Integration is configured on CircleCI. Checkout the [continuous integration here](https://circleci.com/gh/marioalvial/kealth)

##  Testing

```shell
./gradlew test
```

## Built With

- [Kotlin](https://kotlinlang.org/) - Programming language
- [IntelliJ](https://www.jetbrains.com/idea/) - IDE
- [Gradle](https://gradle.org/) - Dependency Management