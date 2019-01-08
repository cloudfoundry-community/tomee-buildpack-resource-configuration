/*
 * Copyright 2016-2019 the original author or authors.
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
import org.springframework.cloud.service.common.RelationalServiceInfo;

import java.util.Properties;


public final class MySqlProperiesProvider extends RelationalServicePropertiesProvider {

    private static final String[] JDBC_DRIVER_CLASSES =
            new String[] { "org.mariadb.jdbc.Driver", "com.mysql.jdbc.Driver" };

    private static final String JDBC_SCHEMA = "jdbc:mysql:";

    private static final String VALIDATION_QUERY = "SELECT 1";

    @Override
    protected void configure(RelationalServiceInfo serviceInfo, Properties properties) {
        properties.setProperty(PROPERTY_JDBC_DRIVER, getJdbcDriverClass(serviceInfo));
        properties.setProperty(PROPERTY_VALIDATION_QUERY, VALIDATION_QUERY);
    }

    @Override
    protected String getJdbcSchema() {
        return JDBC_SCHEMA;
    }

    private String getJdbcDriverClass(RelationalServiceInfo serviceInfo) {
        for (String driver : JDBC_DRIVER_CLASSES) {
            try {
                Thread.currentThread().getContextClassLoader().loadClass(driver);
                return driver;
            } catch (ClassNotFoundException ignore) {
            }
        }
        throw new ConfigurationException("Cannot find suitable driver for " + serviceInfo);
    }
}
