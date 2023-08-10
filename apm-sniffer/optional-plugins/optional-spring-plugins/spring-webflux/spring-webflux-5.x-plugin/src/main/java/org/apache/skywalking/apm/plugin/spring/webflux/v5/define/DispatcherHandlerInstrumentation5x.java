/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.plugin.spring.webflux.v5.define;

import org.apache.skywalking.apm.plugin.spring.webflux.define.DispatcherHandlerInstrumentation;

import static org.apache.skywalking.apm.plugin.spring.commons.Constants.SPRING_5_WITNESS_CLASSES;

public class DispatcherHandlerInstrumentation5x extends DispatcherHandlerInstrumentation {
    @Override
    protected String methodsInterceptor() {
        return "org.apache.skywalking.apm.plugin.spring.webflux.v5.DispatcherHandlerHandleMethodInterceptor5x";
    }

    @Override
    protected String[] witnessClasses() {
        return SPRING_5_WITNESS_CLASSES;
    }
}
