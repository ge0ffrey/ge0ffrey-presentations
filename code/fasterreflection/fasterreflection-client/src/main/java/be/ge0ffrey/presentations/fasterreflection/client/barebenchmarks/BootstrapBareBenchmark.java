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

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class BootstrapBareBenchmark {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private Object person;

    public BootstrapBareBenchmark() {
    }

    @Setup
    public void setup() {
        person = new Person("Ann");
    }

    @Benchmark
    public Object _100_reflectionBootstrap() {
        try {
            return Person.class.getMethod("getName");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _101_reflectionBootstrapWithCall() {
        Method getterMethod;
        try {
            getterMethod = Person.class.getMethod("getName");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        try {
            return getterMethod.invoke(person);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _200_methodHandleBootstrap() {
        try {
            return LOOKUP.findVirtual(Person.class, "getName", MethodType.methodType(String.class))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _201_methodHandleBootstrapWithCall() {
        MethodHandle methodHandle;
        try {
            methodHandle = LOOKUP.findVirtual(Person.class, "getName", MethodType.methodType(String.class))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        try {
            return methodHandle.invokeExact(person);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _300_lamdbaMetafactoryBootstrap() {
        CallSite site;
        try {
            site = LambdaMetafactory.metafactory(LOOKUP,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    LOOKUP.findVirtual(Person.class, "getName", MethodType.methodType(String.class)),
                    MethodType.methodType(String.class, Person.class));
        } catch (LambdaConversionException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        try {
            return (Function) site.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public Object _301_lamdbaMetafactoryBootstrapWithCall() {
        Function getterFunction;
        CallSite site;
        try {
            site = LambdaMetafactory.metafactory(LOOKUP,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    LOOKUP.findVirtual(Person.class, "getName", MethodType.methodType(String.class)),
                    MethodType.methodType(String.class, Person.class));
        } catch (LambdaConversionException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        try {
            getterFunction = (Function) site.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        return getterFunction.apply(person);
    }

}
