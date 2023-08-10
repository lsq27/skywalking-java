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

package org.apache.skywalking.apm.plugin.spring.webflux.v5.webclient.define;

import org.apache.skywalking.apm.plugin.spring.webflux.webclient.define.WebFluxWebClientInstrumentation;

import static org.apache.skywalking.apm.plugin.spring.commons.Constants.SPRING_5_WITNESS_CLASSES;

public class WebFluxWebClientInstrumentation5x extends WebFluxWebClientInstrumentation {
    private static final String INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.spring.webflux.v5.webclient.WebFluxWebClientInterceptor5x";

    @Override
    protected String getInterceptClass() {
        return INTERCEPT_CLASS;
    }

    @Override
    protected String[] witnessClasses() {
        return SPRING_5_WITNESS_CLASSES;
    }
}