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

package org.apache.skywalking.apm.plugin.spring.commons;

public interface Constants {
    /**
     * Classes in and only in spring-core 5.x
     */
    String[] SPRING_5_WITNESS_CLASSES = new String[] {
        "org.springframework.core.io.buffer.DefaultDataBuffer$DefaultDataBufferInputStream",
        "org.springframework.core.ReactiveAdapterRegistry$RxJava1Registrar"
    };

    /**
     * Classes in and only in spring-core 6.x
     */
    String[] SPRING_6_WITNESS_CLASSES = new String[] {
        "org.springframework.core.style.SimpleValueStyler",
        "org.springframework.util.concurrent.FutureUtils"
    };
}
