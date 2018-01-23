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

package be.ge0ffrey.presentations.fasterreflection.client.barebenchmarks;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import be.ge0ffrey.presentations.fasterreflection.client.model.Person;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class MethodHandleBenchmark {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final MethodHandle virtualStaticMethodHandle;
    private static final MethodHandle unreflectStaticMethodHandle;

    static {
        Field field;
        try {
            field = Person.class.getDeclaredField("name");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        field.setAccessible(true);
        Method getterMethod;
        try {
            getterMethod = Person.class.getMethod("getName");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        Class<?> returnType = getterMethod.getReturnType();

        try {
            virtualStaticMethodHandle = LOOKUP.findVirtual(getterMethod.getDeclaringClass(), getterMethod.getName(), MethodType.methodType(returnType))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        try {
            unreflectStaticMethodHandle = LOOKUP.unreflectGetter(field)
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private final MethodHandle virtualMethodHandle;
    private final MethodHandle unreflectMethodHandle;

    private Object person;

    public MethodHandleBenchmark() {
        Field field;
        try {
            field = Person.class.getDeclaredField("name");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        field.setAccessible(true);
        Method getterMethod;
        try {
            getterMethod = Person.class.getMethod("getName");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        Class<?> returnType = getterMethod.getReturnType();

        try {
            virtualMethodHandle = LOOKUP.findVirtual(getterMethod.getDeclaringClass(), getterMethod.getName(), MethodType.methodType(returnType))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        try {
            unreflectMethodHandle = LOOKUP.unreflectGetter(field)
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Setup
    public void setup() {
        person = new Person("Ann");
    }

    @Benchmark
    public Object _000_DirectAccess() {
        return ((Person) person).getName();
    }

    @Benchmark
    public Object _000_VirtualStaticMethodHandle() {
        try {
            return ((Object) virtualStaticMethodHandle.invokeExact(person));
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _000_UnreflectStaticMethodHandle() {
        try {
            return ((Object) unreflectStaticMethodHandle.invokeExact(person));
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _000_VirtualMethodHandle() {
        try {
            return ((Object) virtualMethodHandle.invokeExact(person));
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _000_UnreflectMethodHandle() {
        try {
            return ((Object) unreflectMethodHandle.invokeExact(person));
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }


}
