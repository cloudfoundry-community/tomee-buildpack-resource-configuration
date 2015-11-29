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

package org.cloudfoundry.reconfiguration.tomee;

import org.cloudfoundry.reconfiguration.tomee.dummy.DummyServiceInfo;
import org.cloudfoundry.reconfiguration.tomee.dummy.ServiceInfoWithoutPropertiesProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;


public class DelegatingPropertiesProviderTest {
    private static final String NO_SUCH_SERVICE_INFO = "no-such-service-info";
    private DelegatingPropertiesProvider propertiesProvider;

    @Before
    public void setUp() {
        propertiesProvider = new DelegatingPropertiesProvider();
    }

    @Test(expected = ConfigurationException.class)
    public void testProvidesWithNoServiceId() throws Exception {
        propertiesProvider.setServiceId(null);
        propertiesProvider.provides();
    }

    @Test(expected = ConfigurationException.class)
    public void testProvidesWithNoServiceInfoWithTheProvidedId() throws Exception {
        propertiesProvider.setServiceId(NO_SUCH_SERVICE_INFO);
        propertiesProvider.setProperties(new Properties());
        propertiesProvider.provides();
    }

    @Test(expected = ConfigurationException.class)
    public void testProvidesWithNoSuitablePropertiesProvider() throws Exception {
        propertiesProvider.setServiceId(ServiceInfoWithoutPropertiesProvider.class.getName());
        propertiesProvider.setProperties(new Properties());
        propertiesProvider.provides();
    }

    @Test
    public void testProvide() throws Exception {
        propertiesProvider.setServiceId(DummyServiceInfo.class.getName());
        propertiesProvider.setProperties(new Properties());
        Properties properties = propertiesProvider.provides();
        Assert.assertEquals(DummyServiceInfo.CONFIG_VALUE, properties.getProperty(DummyServiceInfo.CONFIG_KEY));
    }
}