/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package be.ge0ffrey.presentations.fasterreflection.framework;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.Function;

public class LambdaMetafactoryBeanPropertyReader implements BeanPropertyReader {

    private final Function getterFunction;

    public LambdaMetafactoryBeanPropertyReader(Class<?> beanClass, String propertyName) {
        // Not 100% according to Java Beans spec, contains a bug for getHTTP() IIRC
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method getterMethod;
        try {
            getterMethod = beanClass.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class (" + beanClass + ") has doesn't have the getter method ("
                    + getterName + ").", e);
        }
        Class<?> returnType = getterMethod.getReturnType();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site;
        try {
            site = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    lookup.findVirtual(beanClass, getterName, MethodType.methodType(returnType)),
                    MethodType.methodType(returnType, beanClass));
        } catch (LambdaConversionException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Lambda creation failed for method (" + getterMethod + ").", e);
        }
        try {
            getterFunction = (Function) site.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Lambda creation failed for method (" + getterMethod + ").", e);
        }
    }

    public Object executeGetter(Object bean) {
        return getterFunction.apply(bean);
    }

}
