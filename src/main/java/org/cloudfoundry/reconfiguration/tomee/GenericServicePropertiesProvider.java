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

import org.apache.openejb.api.resource.PropertiesResourceProvider;
import org.springframework.cloud.cloudfoundry.CloudFoundryRawServiceData;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.util.EnvironmentAccessor;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by tomcollings on 1/21/18.
 */
public class GenericServicePropertiesProvider implements PropertiesResourceProvider {

    private final ObjectMapper objectMapper;
    private final EnvironmentAccessor environment;

    public GenericServicePropertiesProvider() {
        this (new EnvironmentAccessor(), new ObjectMapper());
    }

    protected GenericServicePropertiesProvider(EnvironmentAccessor environment, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    /**
     * The default configuration of the service provider with merged overrides.
     * Set by OpenEJB
     * NOTE: The field name must be "properties" because that's the name expected by OpenEJB
     */
    private Properties properties;

    /**
     * TomEE service id of the service to be configured.
     *
     * The serviceId is set by OpenEJB
     * NOTE: The field name must be "serviceId" because that's the name expected by OpenEJB
     */
    private String serviceId;

    @Override
    public Properties provides() {
        CloudFoundryRawServiceData rawServiceData = getRawData();
        Map<String, String> genericServiceCredentials = getGenericServiceCredentials(rawServiceData);
        convertToProperties(genericServiceCredentials);
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    private CloudFoundryRawServiceData getRawData() {
        String servicesString = environment.getEnvValue("VCAP_SERVICES");
        CloudFoundryRawServiceData rawServices = new CloudFoundryRawServiceData();

        if (servicesString != null && servicesString.length() > 0) {
            try {
                rawServices = objectMapper.readValue(servicesString, CloudFoundryRawServiceData.class);
            } catch (Exception e) {
                throw new ConfigurationException(e);
            }
        }

        return rawServices;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getGenericServiceCredentials(CloudFoundryRawServiceData rawServiceData) {

        String cfServiceId = removeContextRootFromServiceId();
        for (Map.Entry<String, List<Map<String, Object>>> entry : rawServiceData.entrySet()) {
            for (Map<String, Object> serviceEntry : entry.getValue()) {
                if (serviceEntry.containsKey("credentials")) {
                    Map<String, String> credentials = (Map<String, String>) serviceEntry.get("credentials");

                    if (credentials.containsKey("id")) {
                        String foundId = credentials.get("id");
                        if (foundId.equalsIgnoreCase(cfServiceId)) {
                            return credentials;
                        }
                    }
                }
            }
        }

        throw new ConfigurationException("Could not find required service with id " + serviceId);

    }

    private String removeContextRootFromServiceId() {
        String cfServiceId = serviceId;
        if (cfServiceId != null) {
            int index = cfServiceId.indexOf('/');
            if (index != -1) {
                cfServiceId = serviceId.substring(index + 1);
            }
        }
        return cfServiceId;
    }

    private void convertToProperties(Map<String, String> serviceCredentials) {

        for (Map.Entry<String, String> entry : serviceCredentials.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

    }

}
