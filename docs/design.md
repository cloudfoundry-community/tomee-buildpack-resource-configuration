# Design

## Problem
Today configuration of cloud foundry backing services in TomEE is cumbersome. If one wants to use Postgresql for example, one needs to specify `Resource` definition below in `WEB-INF/resources.xml` file.
The configuration for the backing service can be obtained from the `VCAP_SERVICES` environment variable.

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <Resource id="jdbc/DB" type="DataSource">
      JdbcDriver org.postgresql.Driver
      JdbcUrl = jdbc:postgres://user:pwd@db-host:db-port/db-name
      UserName user
      Password pwd
      JtaManaged true
  </Resource>
</resources>
```

## Solution
TomEE Buildpack can be modified to provide facilities for automatically configuring as much of the `WEB-INF/resources.xml` file for TomEE as possible in order to remove as much of the busywork from app migration as possible.
The buildpack will perform all of this automatic configuration by interpreting the data it finds in the `VCAP_SERVICES` environment variable that is configured automatically by Cloud Foundry via either brokered services or user-provided services.

## Implementation datails
The automatic configuration can be achieved via [PropertiesResourceProvider][] - a standard API provided by TomEE.

### TomEE Buildpack compile phase
Preconfigured `Resource` definitions will be added to the `WEB-INF/resources.xml`.

```
<Resource id='jdbc/...' type='DataSource' properties-provider='org.cloudfoundry.reconfiguration.tomee.DelegatingPropertiesProvider' />
```

This configuration consists of:
* id - `jdbc/` prefix combined with the cloud service name
* type - `DataSource`
* properties provider - `org.cloudfoundry.reconfiguration.tomee.DelegatingPropertiesProvider` that will supply the configuration properties for the corresponding cloud service.

### TomEE startup

![Resources Auto Configuration](resource_configuration.png)

1. During application depoyment `TomEE Deployer` will invoke `DelegatingPropertiesProvider` to provide the configuration properties for a given resource.
2. `DelegatingPropertiesProvider` will use `CloudFoundry Connector` to obtain the service instance properties from the application environment.
3. `DelegatingPropertiesProvider` will try to find a suitable `PropertiesProvider` from `Java Service Registry` that can handle this service instance properties.
4. If there is such `PropertiesProvider`, `DelegatingPropertiesProvider` will delegate the call to it.
5. Using the configuration properties `TomEE Deployer` will create the resource and bind it to the JNDI.
6. Once the resource is available in the JNDI it will be available to the application.

## Alternative solutions
There are other solutions that can be applied in order to achieve automatic configuration.
They are described in the [TomEE Buildpack issue][] outlining the pros and cons.

[PropertiesResourceProvider]: https://github.com/apache/tomee/blob/master/container/openejb-api/src/main/java/org/apache/openejb/api/resource/PropertiesResourceProvider.java
[TomEE Buildpack issue]: https://github.com/cloudfoundry-community/tomee-buildpack/issues/5