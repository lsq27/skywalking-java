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

package org.apache.skywalking.apm.plugin.spring.webflux.v5;

import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.plugin.spring.webflux.DispatcherHandlerHandleMethodInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class DispatcherHandlerHandleMethodInterceptor5x extends DispatcherHandlerHandleMethodInterceptor {
    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                              Object ret) throws Throwable {

        ServerWebExchange exchange = (ServerWebExchange) allArguments[0];

        AbstractSpan span = (AbstractSpan) exchange.getAttributes().get("SKYWALKING_SPAN");
        Mono<Void> monoReturn = (Mono<Void>) ret;

        // add skywalking context snapshot to reactor context.
        EnhancedInstance instance = getInstance(allArguments[0]);
        if (instance != null && instance.getSkyWalkingDynamicField() != null) {
            monoReturn = monoReturn.subscriberContext(
                    c -> c.put("SKYWALKING_CONTEXT_SNAPSHOT", instance.getSkyWalkingDynamicField()));
        }

        return monoReturn.doFinally(s -> {

            if (span != null) {
                maybeSetPattern(span, exchange);
                try {

                    HttpStatus httpStatus = exchange.getResponse().getStatusCode();
                    // fix webflux-2.0.0-2.1.0 version have bug. httpStatus is null. not support
                    if (httpStatus != null) {
                        Tags.HTTP_RESPONSE_STATUS_CODE.set(span, httpStatus.value());
                        if (httpStatus.isError()) {
                            span.errorOccurred();
                        }
                    }
                } finally {
                    span.asyncFinish();
                }
            }
        });
    }
}
