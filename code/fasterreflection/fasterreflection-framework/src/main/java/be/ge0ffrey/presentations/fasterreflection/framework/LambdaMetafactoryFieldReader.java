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
import java.lang.reflect.Field;
import java.util.function.Function;

public class LambdaMetafactoryFieldReader implements BeanPropertyReader {

    private final Function getterFunction;

    public LambdaMetafactoryFieldReader(Class<?> beanClass, String propertyName) {
        Field field;
        try {
            field = beanClass.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
        field.setAccessible(true);

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site;
        try {
            // TODO Error because this lambda is generated in another class/package and cannot access those private members
            site = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    lookup.unreflectGetter(field),
                    MethodType.methodType(field.getType(), beanClass));
        } catch (LambdaConversionException | IllegalAccessException e) {
            throw new IllegalArgumentException("Lambda creation failed for field (" + field + ").", e);
        }
        try {
            getterFunction = (Function) site.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Lambda creation failed for field (" + field + ").", e);
        }
    }

    public Object executeGetter(Object bean) {
        return getterFunction.apply(bean);
    }

}
