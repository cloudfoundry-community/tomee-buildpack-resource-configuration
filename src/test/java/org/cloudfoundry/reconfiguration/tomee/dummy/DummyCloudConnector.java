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

package org.cloudfoundry.reconfiguration.tomee.dummy;

import org.springframework.cloud.CloudConnector;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.cloud.service.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * CloudConnector implementation for testing purposes
 */
public class DummyCloudConnector implements CloudConnector {

    @Override
    public ApplicationInstanceInfo getApplicationInstanceInfo() {
        return null;
    }

    @Override
    public boolean isInMatchingCloud() {
        return true;
    }

    @Override
    public List<ServiceInfo> getServiceInfos() {
        List<ServiceInfo> availableServices = new ArrayList<>(2);
        availableServices.add(new DummyServiceInfo());
        availableServices.add(new JdbcServiceInfo());
        availableServices.add(new ServiceInfoWithoutPropertiesProvider());
        return availableServices;
    }
}
