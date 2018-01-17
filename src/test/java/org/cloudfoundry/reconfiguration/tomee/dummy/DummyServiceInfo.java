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

package org.cloudfoundry.reconfiguration.tomee.dummy;

import org.springframework.cloud.service.ServiceInfo;

/**
 * Dummy ServiceInfo for testing purposes
 */
public class DummyServiceInfo implements ServiceInfo {

    public static final String CONFIG_KEY = "set_me";

    public static final String CONFIG_VALUE = DummyServiceInfo.class.getName();

    private final String id;

    public DummyServiceInfo() {
        this.id = DummyServiceInfo.class.getName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DummyServiceInfo{id='" + id + "'}";
    }
}
