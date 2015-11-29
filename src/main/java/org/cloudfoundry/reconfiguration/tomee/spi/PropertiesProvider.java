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

package org.cloudfoundry.reconfiguration.tomee.spi;

import org.springframework.cloud.service.ServiceInfo;

import java.util.Properties;

/**
 * SPI for properties providers which configure a resource
 * using the configuration provided by the {@link ServiceInfo} object
 *
 * @see ServiceInfo
 */
public interface PropertiesProvider {

    /**
     * Check if this {@link PropertiesProvider} instance can configure the TomEE resource associated
     * with the provided {@link ServiceInfo}
     *
     * @param serviceInfo <code>ServiceInfo</code> instance containing configuration information for a bound service
     * @return <code>true</code> if this  <code>PropertiesProvider</code> instance can configure the TomEE resource
     * associate with the provided <code>ServiceInfo</code>
     * @see ServiceInfo
     */
    boolean canProvide(ServiceInfo serviceInfo);

    /**
     * Provide configuration for the TomEE resource associated with the provided {@link ServiceInfo}
     *
     * @param serviceInfo          <code>ServiceInfo</code> instance containing configuration information for a bound service
     * @param defaultConfiguration Default configuration provided by the TomEE <code>ServiceProvider</code>
     * @return The configuration for the TomEE resource associated with the provided <code>ServiceInfo</code>
     * @throws org.cloudfoundry.reconfiguration.tomee.ConfigurationException In case of configuration failure
     * @see ServiceInfo
     */
    Properties provide(ServiceInfo serviceInfo, Properties defaultConfiguration);
}

