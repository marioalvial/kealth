# 2.0.0
> Published 28 July 2019

#### Improvements:

* Add custom coroutine scope to run the `aggregate()`

#### Features:

* Added `handleUnhealthyStatus()` to executes logic when `doHealthCheck()` returns UNHEALTHY but does not throw exception
* Added `parameters` variable to allow the component to add parameters to be used in any handle method

#### Bugfix:

* Change context logic to be set one time with a variable instead of method

# 1.0.9.5
> Published 9 July 2019

#### Bugfix:

* Close connection after jdbc component health check execution

# 1.0.9.3
> Published 3 June 2019

#### Features:

* Added `CriticalLevel.MEDIUM` constant
* Added `aggregateWithFilter()` method in `HealthAggregator`

# 1.0.9.2
> Published 30 May 2019

#### Features:

* Added `criticalLevel` property to HealthComponent

# 1.0.9.1
> Published 19 May 2019

#### Features:

* Added `HTTP Module` HealthComponent for HTTP requests

# 1.0.9.0
> Published 18 May 2019

#### Features:

* Added `JDBC Module` HealthComponent for JDBC health check
