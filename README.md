![Valiktor](docs/logo.png)

[![CircleCI](https://circleci.com/gh/marioalvial/kealth.svg?style=svg)](https://circleci.com/gh/marioalvial/kealth)
[![Known Vulnerabilities](https://snyk.io/test/github/marioalvial/kealth/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/marioalvial/kealth?targetFile=build.gradle)
[![codecov](https://codecov.io/gh/marioalvial/kealth/branch/master/graph/badge.svg)](https://codecov.io/gh/marioalvial/kealth)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.marioalvial/kealth.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.marioalvial%22%20AND%20a:%22kealth%22)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f51e7103bcc34855b506e947990b2395)](https://www.codacy.com/app/marioalvial/kealth?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=marioalvial/kealth&amp;utm_campaign=Badge_Grade)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![HitCount](http://hits.dwyl.io/marioalvial/kealth.svg)](http://hits.dwyl.io/marioalvial/kealth)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> Kealth is a health check library for external dependencies in Kotlin

## Installation

![Maven](docs/maven.png) 

```xml
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
    <version>1.0.5</version>
</dependency>
```

![Gradle](docs/gradle.png)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.marioalvial:kealth:1.0.5'
}    
```

## Getting Started

1. Create your component:

```kotlin
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

2. Instantiate `HealthAggregator`:

```kotlin
val aggregator = HealthAggregator(listOf(HealthComponentA()))
```

3. Execute `aggregate()`:

```kotlin
val componentMap: Map<String, HealthInfo> = aggregator.aggregate() 
```

**Example of `componentMap` serialized for json:**

```json
{
	"component-A": {
		"status": "HEALTHY",
		"duration": 1500
	},
	"component-B": {
		"status": "UNHEALTHY",
		"duration": 400
	}
}
```
## Handle Failure

`handleFailure()` will be trigger only if `doHealthCheck()` call throws exception.

## How it works

When `aggregator.aggregate()` is called it will execute `health()` of each component in parallel and create a map with the component's name as key and health info as value.

If the `doHealthCheck()` throws exception the component will trigger the `handleFailure()` method asynchronous with the exception that was thrown.

## Continuous Integration and Test Coverage

Test Coverage configured on CodeCov. Checkout the [test coverage here](https://codecov.io/gh/marioalvial/kealth).

Continuous Integration is configured on CircleCI. Checkout the [continuous integration here](https://circleci.com/gh/marioalvial/kealth)

## Testing

```shell
./gradlew test
```

## Built With

- [Kotlin](https://kotlinlang.org/) - Programming language
- [IntelliJ](https://www.jetbrains.com/idea/) - IDE
- [Gradle](https://gradle.org/) - Dependency Management