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

package org.apache.skywalking.apm.plugin.spring.webflux.v5.webclient;

import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.MethodInvocationContext;
import org.apache.skywalking.apm.plugin.spring.webflux.webclient.commons.WebFluxWebClientInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

public class WebFluxWebClientInterceptor5x extends WebFluxWebClientInterceptor {
    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret, MethodInvocationContext context) throws Throwable {
        // fix the problem that allArgument[0] may be null
        if (allArguments[0] == null) {
            return ret;
        }
        Mono<ClientResponse> ret1 = (Mono<ClientResponse>) ret;
        return Mono.subscriberContext().flatMap(ctx -> {
            // get ContextSnapshot from reactor context,  the snapshot is set to reactor context by any other plugin
            // such as DispatcherHandlerHandleMethodInterceptor in spring-webflux-5.x-plugin
            final Optional<Object> optional = ctx.getOrEmpty("SKYWALKING_CONTEXT_SNAPSHOT");

            return createSpan(allArguments, optional, ret1);
        });
    }

    @Override
    protected Consumer<ClientResponse> doOnSuccess(AbstractSpan span) {
        return clientResponse -> {
            HttpStatus httpStatus = clientResponse.statusCode();
            if (httpStatus != null) {
                Tags.HTTP_RESPONSE_STATUS_CODE.set(span, httpStatus.value());
                if (httpStatus.isError()) {
                    span.errorOccurred();
                }
            }
        };
    }
}
