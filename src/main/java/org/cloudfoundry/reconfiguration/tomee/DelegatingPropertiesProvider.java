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

import org.apache.openejb.api.resource.PropertiesResourceProvider;
import org.cloudfoundry.reconfiguration.tomee.spi.PropertiesProvider;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;

import java.util.Collection;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link PropertiesResourceProvider} implementation that delegates the resource configuration to a suitable
 * {@link PropertiesProvider} implementation. The <code>PropertiesProvider</code> implementations are discovered
 * through the {@link java.util.ServiceLoader} mechanism.
 *
 * @see ServiceLoader
 * @see PropertiesProvider
 * @see PropertiesResourceProvider
 */
public final class DelegatingPropertiesProvider implements PropertiesResourceProvider {
    private static final Logger logger = Logger.getLogger(DelegatingPropertiesProvider.class.getName());
    private static final Object monitor = new Object();
    private static volatile Cloud cloud;

    /**
     * ServiceId of the service to be configured. The serviceId is set by OpenEJB
     * <p>
     * NOTE: The field name must be "serviceId" because that's the name expected by OpenEJB
     * </p>
     */
    private String serviceId;

    /**
     * The default configuration of the service provider with merged overrides. Set by OpenEJB
     * <p>
     * NOTE: The field name must be "properties" because that's the name expected by OpenEJB
     * </p>
     */
    private Properties properties;

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Provide configuration for a TomEE resource.
     *
     * @return The configuration for the TomEE resource associated with the provided <code>serviceId</code>
     * @throws ConfigurationException In case of configuration failure
     * @see PropertiesResourceProvider
     */
    @Override
    public Properties provides() {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Configuring resource: " + serviceId);
        }
        if (null == serviceId) {
            throw new ConfigurationException("The serviceId is null.");
        }

        final ServiceInfo serviceInfo = getBoundService(serviceId);
        final PropertiesProvider propertiesProvider = getPropertiesProvider(serviceInfo);
        return propertiesProvider.provide(serviceInfo, properties);
    }

    private ServiceInfo getBoundService(String serviceId) {
        final Cloud cloud = getCloudInstance();
        final Collection<ServiceInfo> serviceInfos = cloud.getServiceInfos();

        for (ServiceInfo serviceInfo : serviceInfos) {
            if (serviceId.equals(serviceInfo.getId())) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Found matching ServiceInfo for serviceId " + serviceId + ": " + serviceInfo);
                }
                return serviceInfo;
            }
        }
        throw new ConfigurationException("Cannot find ServiceInfo for serviceId: " + serviceId);
    }

    private PropertiesProvider getPropertiesProvider(ServiceInfo serviceInfo) {
        final ServiceLoader<PropertiesProvider> serviceLoader = ServiceLoader.load(PropertiesProvider.class);
        for (PropertiesProvider propertiesProvider : serviceLoader) {
            if (propertiesProvider.canProvide(serviceInfo)) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Found suitable PropertiesProvider for serviceId " + serviceId + ": " + propertiesProvider.getClass());
                }
                return propertiesProvider;
            }
        }

        throw new ConfigurationException("Cannot find suitable PropertiesProvider for serviceId " + serviceId + ": " + serviceInfo);
    }

    private static Cloud getCloudInstance() {
        if (null == cloud) {
            synchronized (monitor) {
                if (null == cloud) {
                    cloud = new CloudFactory().getCloud();
                }
            }
        }
        return cloud;
    }
}
