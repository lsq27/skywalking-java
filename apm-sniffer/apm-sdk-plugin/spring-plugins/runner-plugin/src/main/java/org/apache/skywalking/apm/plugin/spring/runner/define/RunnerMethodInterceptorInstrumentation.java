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

package org.apache.skywalking.apm.plugin.spring.runner.define;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.plugin.spring.runner.RunnerMethodInterceptor;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;
import static org.apache.skywalking.apm.agent.core.plugin.match.NameMatch.byName;

/**
 * Enhance {@code org.springframework.scheduling.support.ScheduledMethodRunnable} instance,
 * and intercept {@code org.springframework.scheduling.support.ScheduledMethodRunnable#run()} method,
 * this method is a unified entrance of execute schedule task.
 *
 * @see RunnerMethodInterceptor
 */
@Slf4j
public class RunnerMethodInterceptorInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    public static final String METHOD_INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.spring.runner.RunnerMethodInterceptor";
    public static final String ENHANC_CLASS = "org.springframework.boot.CommandLineRunner";

    @Override
    public ClassMatch enhanceClass() {
        log.error("enhanceClass");
        return byName(ENHANC_CLASS);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        log.error("getConstructorsInterceptPoints");
        return null;
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        log.error("getMethodsMatcher");
                        return named("run")
                                .and(isPublic())
                                .and(takesArguments(1)).and(takesArgument(0, String[].class));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        log.error("getMethodsInterceptor");
                        return METHOD_INTERCEPTOR_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        log.error("isOverrideArgs");
                        return false;
                    }
                }
        };
    }
}
