/*
 * Copyright 2016-2018 the original author or authors.
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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.util.EnvironmentAccessor;
import org.springframework.util.ReflectionUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Scanner;


/**
 * Created by tomcollings on 1/21/18.
 */


public class GenericServicePropertiesProviderTest {

    @Mock
    private EnvironmentAccessor env;


    @Test
    public void testCorrectPropertiesFound() throws Exception {

        MockitoAnnotations.initMocks(this);

        String rawJson = readTestDataFile("validGenericService.json");
        GenericServicePropertiesProvider propertiesProvider = new GenericServicePropertiesProvider();
        Field envField = ReflectionUtils.findField(GenericServicePropertiesProvider.class, "environment");
        ReflectionUtils.makeAccessible(envField);
        ReflectionUtils.setField(envField, propertiesProvider, env);

        Mockito.when(env.getEnvValue("VCAP_SERVICES")).thenReturn(rawJson);


        configureDelegatingPropertiesProvider(propertiesProvider, "someId", new Properties());
        Properties properties = propertiesProvider.provides();
        Assert.assertEquals(properties.get("name1"), "val1");
        Assert.assertEquals(properties.get("name2"), "val2");
    }

    @Test(expected = ConfigurationException.class)
    public void testProvidesWithNoServiceId() throws Exception {
        GenericServicePropertiesProvider propertiesProvider = new GenericServicePropertiesProvider();
        configureDelegatingPropertiesProvider(propertiesProvider, null, null);
        propertiesProvider.provides();
    }

    private void configureDelegatingPropertiesProvider(GenericServicePropertiesProvider propertiesProvider, String serviceId, Properties properties) {
        propertiesProvider.setServiceId(serviceId);
        propertiesProvider.setProperties(properties);
    }

    private String readTestDataFile(String fileName) {
        Scanner scanner = null;
        try {
            Reader fileReader = new InputStreamReader(getClass().getResourceAsStream(fileName));
            scanner = new Scanner(fileReader);
            return scanner.useDelimiter("\\Z").next();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
