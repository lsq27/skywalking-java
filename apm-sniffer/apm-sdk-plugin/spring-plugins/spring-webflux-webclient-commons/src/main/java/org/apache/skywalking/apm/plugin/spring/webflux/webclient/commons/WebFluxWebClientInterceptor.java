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

package org.apache.skywalking.apm.plugin.spring.webflux.webclient.commons;

import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.InstanceMethodsAroundInterceptorV2;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.MethodInvocationContext;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class WebFluxWebClientInterceptor implements InstanceMethodsAroundInterceptorV2 {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInvocationContext context) throws Throwable {

    }

    protected Mono<ClientResponse> createSpan(Object[] allArguments, Optional<Object> optional, Mono<ClientResponse> ret1) {
        ClientRequest request = (ClientRequest) allArguments[0];
        URI uri = request.url();
        final String operationName = getRequestURIString(uri);
        final String remotePeer = getIPAndPort(uri);
        AbstractSpan span = ContextManager.createExitSpan(operationName, remotePeer);

        optional.ifPresent(snapshot -> ContextManager.continued((ContextSnapshot) snapshot));

        //set components name
        span.setComponent(ComponentsDefine.SPRING_WEBCLIENT);
        Tags.URL.set(span, uri.toString());
        Tags.HTTP.METHOD.set(span, request.method().toString());
        SpanLayer.asHttp(span);

        final ContextCarrier contextCarrier = new ContextCarrier();
        ContextManager.inject(contextCarrier);
        if (request instanceof EnhancedInstance) {
            ((EnhancedInstance) request).setSkyWalkingDynamicField(contextCarrier);
        }

        //user async interface
        span.prepareForAsync();
        ContextManager.stopSpan();
        return ret1.doOnSuccess(doOnSuccess(span)).doOnError(span::log).doFinally(s -> span.asyncFinish());
    }

    protected abstract Consumer<ClientResponse> doOnSuccess(AbstractSpan span);

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t, MethodInvocationContext context) {
        AbstractSpan activeSpan = ContextManager.activeSpan();
        activeSpan.errorOccurred();
        activeSpan.log(t);
    }

    private String getRequestURIString(URI uri) {
        String requestPath = uri.getPath();
        return requestPath != null && !requestPath.isEmpty() ? requestPath : "/";
    }

    // return ip:port
    private String getIPAndPort(URI uri) {
        return uri.getHost() + ":" + uri.getPort();
    }
}
