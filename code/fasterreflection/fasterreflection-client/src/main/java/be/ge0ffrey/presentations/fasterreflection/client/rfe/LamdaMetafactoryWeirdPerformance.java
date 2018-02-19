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

package be.ge0ffrey.presentations.fasterreflection.client.rfe;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

//Benchmark on JDK 9     Mode  Cnt  Score   Error  Units
//staticMethodHandle     avgt   30  2.770 ± 0.023  ns/op // Baseline
//lambdaMetafactory      avgt   30  3.052 ± 0.004  ns/op // 10% slower
//nonStaticMethodHandle  avgt   30  5.250 ± 0.137  ns/op // 90% slower

//Benchmark on JDK 8     Mode  Cnt  Score   Error  Units
//staticMethodHandle     avgt   30  2.772 ± 0.022  ns/op // Baseline
//lambdaMetafactory      avgt   30  3.060 ± 0.007  ns/op // 10% slower
//nonStaticMethodHandle  avgt   30  5.037 ± 0.022  ns/op // 81% slower

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class LamdaMetafactoryWeirdPerformance {

    // ************************************************************************
    // Set up of the 3 approaches.
    // ************************************************************************

    // Unusable for Java framework developers. Only usable by JVM language developers. Baseline.
    private static final MethodHandle staticMethodHandle;

    // Usuable for Java framework developers. 30% slower
    private final Function lambdaMetafactoryFunction;

    // Usuable for Java framework developers. 100% slower
    private final MethodHandle nonStaticMethodHandle;

    static {
        // Static MethodHandle setup
        try {
            staticMethodHandle = MethodHandles.lookup()
                    .findVirtual(Dog.class, "getName", MethodType.methodType(String.class))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public LamdaMetafactoryWeirdPerformance() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            // LambdaMetafactory setup
            CallSite site = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    lookup.findVirtual(Dog.class, "getName", MethodType.methodType(String.class)),
                    MethodType.methodType(String.class, Dog.class));
            lambdaMetafactoryFunction = (Function) site.getTarget().invokeExact();

            // Non-static MethodHandle setup
            nonStaticMethodHandle = lookup
                    .findVirtual(Dog.class, "getName", MethodType.methodType(String.class))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    // ************************************************************************
    // Benchmark
    // ************************************************************************

    private Object dogObject = new Dog("Fido");


    @Benchmark
    public Object _1_staticMethodHandle() throws Throwable {
        return staticMethodHandle.invokeExact(dogObject);
    }

    @Benchmark
    public Object _2_lambdaMetafactory() {
        return lambdaMetafactoryFunction.apply(dogObject);
    }

    @Benchmark
    public Object _3_nonStaticMethodHandle() throws Throwable {
        return nonStaticMethodHandle.invokeExact(dogObject);
    }

    private static class Dog {
        private String name;

        public Dog(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
