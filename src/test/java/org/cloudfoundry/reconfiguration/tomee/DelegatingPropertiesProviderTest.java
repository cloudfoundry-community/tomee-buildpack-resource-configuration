/*
 * Copyright 2016-2017 the original author or authors.
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

package org.cloudfoundry.reconfiguration.tomee;

import org.cloudfoundry.reconfiguration.tomee.dummy.DummyServiceInfo;
import org.cloudfoundry.reconfiguration.tomee.dummy.JdbcServiceInfo;
import org.cloudfoundry.reconfiguration.tomee.dummy.ServiceInfoWithoutPropertiesProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class DelegatingPropertiesProviderTest {

    private static final String DEFAULT_CONTEXT_ROOT = "ROOT";

    private static final String NO_SUCH_SERVICE_INFO = "no-such-service-info";

    private DelegatingPropertiesProvider propertiesProvider;

    @Before
    public void setUp() {
        propertiesProvider = new DelegatingPropertiesProvider();
    }

    @Test
    public void testProvide() throws Exception {
        configureDelegatingPropertiesProvider(DEFAULT_CONTEXT_ROOT + "/" + DummyServiceInfo.class.getName(), new Properties());
        Properties properties = propertiesProvider.provides();
        Assert.assertEquals(DummyServiceInfo.CONFIG_VALUE, properties.getProperty(DummyServiceInfo.CONFIG_KEY));
    }

    @Test
    public void testProvideJdbcProperties() throws Exception {
        configureDelegatingPropertiesProvider(DEFAULT_CONTEXT_ROOT + "/" + DelegatingPropertiesProvider.PREFIX_JDBC + JdbcServiceInfo.ID, new Properties());
        Properties properties = propertiesProvider.provides();
        Assert.assertEquals(JdbcServiceInfo.ID, properties.getProperty(JdbcServiceInfo.CONFIG_KEY));
    }

    @Test(expected = ConfigurationException.class)
    public void testProvidesWithNoServiceId() throws Exception {
        configureDelegatingPropertiesProvider(null, null);
        propertiesProvider.provides();
    }

    @Test(expected = ConfigurationException.class)
    public void testProvidesWithNoServiceInfoWithTheProvidedId() throws Exception {
        configureDelegatingPropertiesProvider(NO_SUCH_SERVICE_INFO, new Properties());
        propertiesProvider.provides();
    }

    @Test(expected = ConfigurationException.class)
    public void testProvidesWithNoSuitablePropertiesProvider() throws Exception {
        configureDelegatingPropertiesProvider(ServiceInfoWithoutPropertiesProvider.class.getName(), new Properties());
        propertiesProvider.provides();
    }

    private void configureDelegatingPropertiesProvider(String serviceId, Properties properties) {
        propertiesProvider.setServiceId(serviceId);
        propertiesProvider.setProperties(properties);
    }

}
