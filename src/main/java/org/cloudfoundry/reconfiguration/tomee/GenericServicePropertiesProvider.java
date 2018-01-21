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

    private ObjectMapper objectMapper;
    private EnvironmentAccessor environment;

    public GenericServicePropertiesProvider() {
        objectMapper = new ObjectMapper();
        environment = new EnvironmentAccessor();
    }

    public GenericServicePropertiesProvider(EnvironmentAccessor environment) {
        this.environment = environment;
        objectMapper = new ObjectMapper();
    }

    /**
     * The default configuration of the service provider with merged overrides. Set by OpenEJB <p> NOTE: The field name must be "properties" because that's the name expected by OpenEJB </p>
     */
    private Properties properties;

    /**
     * TomEE service id of the service to be configured. The serviceId is set by OpenEJB <p> NOTE: The field name must be "serviceId" because that's the name expected by OpenEJB </p>
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

        for (Map.Entry<String, List<Map<String, Object>>> entry : rawServiceData.entrySet()) {
            System.out.println(entry.getValue());
            for (Map<String, Object> serviceEntry : entry.getValue()) {
                if (serviceEntry.containsKey("credentials")) {
                    Map<String, String> credentials = (Map<String, String>) serviceEntry.get("credentials");

                    if (credentials.containsKey("id")) {
                        String foundId = credentials.get("id");
                        if (foundId.equalsIgnoreCase(serviceId)) {
                            return credentials;
                        }
                    }
                }
            }
        }

        throw new ConfigurationException("Could not find required service with id " + serviceId);

    }

    private void convertToProperties (Map<String, String> serviceCredentials) {

        for (Map.Entry <String, String> entry : serviceCredentials.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

    }

}
