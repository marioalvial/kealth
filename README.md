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
![Maven Central](https://img.shields.io/maven-central/v/io.github.marioalvial/kealth.svg)

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
    <version>1.0.4</version>
</dependency>
```

#### Gradle:
```
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.marioalvial:kealth:1.0.4'
}    
```

## Getting Started

1. Create your component:

```
class HealthComponentA : HealthComponent() {

    override val name = "component A"

    override fun doHealthCheck(): HealthStatus {
        val result = doHealthCheckCallToComponentAService()
        return if(result) HealthStatus.HEALTHY else HealthStatus.UNHEALTHY
    }

    override fun handleFailure(throwable: Throwable) {
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
val componentMap = aggregator.aggregate() 
```

## Handle Failure

handleFailure method of your health component will be trigger only if doHealthCheck() call throws exception.

## How it works

When aggregator.aggregate() is called it will execute all components health() method in parallel and create a map with the component's name as key and health info as value.

If the doHealthCheck() throws exception the component will trigger the handleFailure method asynchronous with the exception that was thrown.

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