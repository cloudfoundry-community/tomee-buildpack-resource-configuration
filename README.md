# TomEE Resources Auto Configuration
This repository contains the resources auto configuration functionality for TomEE Buildpack.

## What is TomEE Resources Auto Configuration?
TomEE Resources Auto Configuration provides adaptation in applications deployment descriptors to support cloud service utilization.
Functionality targets exposure of relational data service connection properties in the JNDI environment of applications.
If a `WEB-INF/resources.xml` file does not exist, it will be created. If it exists it will be modified.
Preconfigured `Resource` definition will be added to this file for every relational data service.

```
<Resource id='jdbc/...' type='DataSource' properties-provider='org.cloudfoundry.reconfiguration.tomee.DelegatingPropertiesProvider' />
```

This configuration consists of:
* id - `jdbc/` prefix combined with the cloud service name
* type - `DataSource`
* properties provider - `org.cloudfoundry.reconfiguration.tomee.DelegatingPropertiesProvider` that will supply the configuration properties for the corresponding cloud service.

## License
This buildpack is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
