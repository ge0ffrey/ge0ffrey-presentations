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

// Benchmark on Geoffrey's machine                               Mode  Cnt  Score   Error  Units
// MethodHandleBenchmark._000_DirectAccess                       avgt   15  2.788 ± 0.022  ns/op
// MethodHandleBenchmark._101_StaticVirtualAsTypeMethodHandle    avgt   15  2.830 ± 0.032  ns/op
// MethodHandleBenchmark._110_VirtualBareMethodHandle            avgt   15  5.434 ± 0.073  ns/op // Slow
// MethodHandleBenchmark._111_VirtualAsTypeMethodHandle          avgt   15  5.214 ± 0.095  ns/op // Slow
// MethodHandleBenchmark._201_StaticUnreflectAsTypeMethodHandle  avgt   15  2.798 ± 0.026  ns/op
// MethodHandleBenchmark._210_UnreflectBareMethodHandle          avgt   15  5.527 ± 0.095  ns/op // Slow
// MethodHandleBenchmark._211_UnreflectAsTypeMethodHandle        avgt   15  5.244 ± 0.109  ns/op // Slow
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class MethodHandleBenchmark {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final MethodHandle staticVirtualAsTypeMethodHandle;
    private static final MethodHandle staticUnreflectAsTypeMethodHandle;

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
            staticVirtualAsTypeMethodHandle = LOOKUP.findVirtual(getterMethod.getDeclaringClass(), getterMethod.getName(), MethodType.methodType(returnType))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        try {
            staticUnreflectAsTypeMethodHandle = LOOKUP.unreflectGetter(field)
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private final MethodHandle virtualBareMethodHandle;
    private final MethodHandle virtualAsTypeMethodHandle;

    private final MethodHandle unreflectBareMethodHandle;
    private final MethodHandle unreflectAsTypeMethodHandle;

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
            virtualBareMethodHandle = LOOKUP.findVirtual(getterMethod.getDeclaringClass(), getterMethod.getName(), MethodType.methodType(returnType));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        try {
            virtualAsTypeMethodHandle = LOOKUP.findVirtual(getterMethod.getDeclaringClass(), getterMethod.getName(), MethodType.methodType(returnType))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        try {
            unreflectBareMethodHandle = LOOKUP.unreflectGetter(field);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        try {
            unreflectAsTypeMethodHandle = LOOKUP.unreflectGetter(field)
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
    public Object _101_StaticVirtualAsTypeMethodHandle() {
        try {
            return staticVirtualAsTypeMethodHandle.invokeExact(person);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _110_VirtualBareMethodHandle() {
        try {
            // Unusable: the framework needs the class Person and the return type at compile time
            return ((String) virtualBareMethodHandle.invokeExact((Person) person));
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _111_VirtualAsTypeMethodHandle() {
        try {
            return virtualAsTypeMethodHandle.invokeExact(person);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _201_StaticUnreflectAsTypeMethodHandle() {
        try {
            return staticUnreflectAsTypeMethodHandle.invokeExact(person);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _210_UnreflectBareMethodHandle() {
        try {
            // Unusable: the framework needs the class Person and the return type at compile time
            return ((String) unreflectBareMethodHandle.invokeExact((Person) person));
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _211_UnreflectAsTypeMethodHandle() {
        try {
            return unreflectAsTypeMethodHandle.invokeExact(person);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }


}
