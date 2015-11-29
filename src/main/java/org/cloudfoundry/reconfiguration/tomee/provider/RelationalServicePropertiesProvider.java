/*
* Copyright 2015 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.cloudfoundry.reconfiguration.tomee.provider;


import org.cloudfoundry.reconfiguration.tomee.ConfigurationException;
import org.cloudfoundry.reconfiguration.tomee.spi.PropertiesProvider;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.RelationalServiceInfo;

import java.util.Properties;


public abstract class RelationalServicePropertiesProvider implements PropertiesProvider {
    /**
     * JDBC driver class
     */
    protected static final String PROPERTY_JDBC_DRIVER = "JdbcDriver";

    /**
     * JDBC connection URL
     */
    protected static final String PROPERTY_JDBC_URL = "JdbcUrl";

    /**
     * User name
     */
    protected static final String PROPERTY_USER_NAME = "UserName";

    /**
     * Password
     */
    protected static final String PROPERTY_PASSWORD = "Password";

    /**
     * The password cipher
     */
    protected static final String PROPERTY_PASSWORD_CIPHER = "PasswordCipher";

    /**
     * The SQL query that will be used to validate connections from this pool
     * before returning them to the caller. If specified, this query MUST be an
     * SQL SELECT statement that returns at least one row.
     */
    protected static final String PROPERTY_VALIDATION_QUERY = "ValidationQuery";

    /**
     * If true connections will be validated before being returned from the
     * pool. If the validation fails, the connection is destroyed, and a new
     * connection will be retrieved from the pool (and validated).
     * <p/>
     * NOTE - for a true value to have any effect, the ValidationQuery parameter
     * must be set.
     */
    protected static final String PROPERTY_TEST_ON_BORROW = "TestOnBorrow";

    @Override
    public final Properties provide(ServiceInfo serviceInfo, Properties defaultConfiguration) {
        if (!canProvide(serviceInfo)) {
            throw new ConfigurationException("Cannot provide properties for: " + serviceInfo);
        }

        final RelationalServiceInfo serviceConfig = (RelationalServiceInfo) serviceInfo;
        defaultConfiguration.setProperty(PROPERTY_JDBC_URL, serviceConfig.getJdbcUrl());
        defaultConfiguration.setProperty(PROPERTY_USER_NAME, serviceConfig.getUserName());
        defaultConfiguration.setProperty(PROPERTY_PASSWORD, serviceConfig.getPassword());
        defaultConfiguration.setProperty(PROPERTY_TEST_ON_BORROW, "true");
        defaultConfiguration.put(PROPERTY_PASSWORD_CIPHER, "PlainText");

        configure(serviceConfig, defaultConfiguration);
        return defaultConfiguration;
    }

    @Override
    public boolean canProvide(ServiceInfo serviceInfo) {
        boolean provides = serviceInfo instanceof RelationalServiceInfo;
        if (provides) {
            final RelationalServiceInfo relationalServiceInfo = (RelationalServiceInfo) serviceInfo;
            final String jdbcSchema = getJdbcSchema();
            provides = relationalServiceInfo.getJdbcUrl().startsWith(jdbcSchema);
        }
        return provides;
    }

    protected String getJdbcSchema() {
        /**
         * Providing default behaviour in case canProvide() is overridden
         */
        return null;
    }

    protected abstract void configure(RelationalServiceInfo serviceInfo, Properties properties);
}
