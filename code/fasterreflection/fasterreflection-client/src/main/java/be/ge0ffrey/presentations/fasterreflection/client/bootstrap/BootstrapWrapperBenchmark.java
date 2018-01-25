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

package be.ge0ffrey.presentations.fasterreflection.client.bootstrap;

import java.util.concurrent.TimeUnit;

import be.ge0ffrey.presentations.fasterreflection.client.model.Person;
import be.ge0ffrey.presentations.fasterreflection.framework.JavaCompilerBeanPropertyReaderFactory;
import be.ge0ffrey.presentations.fasterreflection.framework.LambdaMetafactoryBeanPropertyReader;
import be.ge0ffrey.presentations.fasterreflection.framework.MethodHandleBeanPropertyReader;
import be.ge0ffrey.presentations.fasterreflection.framework.ReflectionBeanPropertyReader;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

// Benchmark on ge0ffrey's machine                            Mode  Cnt        Score        Error  Units
// BootstrapWrapperBenchmark._100_ReflectionBootstrap         avgt   60      268.510 ±     25.271  ns/op
// BootstrapWrapperBenchmark._200_MethodHandleBootstrap       avgt   60     1519.177 ±     46.644  ns/op
// BootstrapWrapperBenchmark._300_JavaCompilerBootstrap       avgt   60  4814526.314 ± 503770.574  ns/op
// BootstrapWrapperBenchmark._400_LamdbaMetafactoryBootstrap  avgt   60    38904.287 ±   1330.080  ns/op
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class BootstrapWrapperBenchmark {

    @Benchmark()
    public Object _100_ReflectionBootstrap() {
        return new ReflectionBeanPropertyReader(Person.class, "name");
    }

    @Benchmark
    public Object _200_MethodHandleBootstrap() {
        return new MethodHandleBeanPropertyReader(Person.class, "name");
    }

    @Benchmark
    public Object _300_JavaCompilerBootstrap() {
        return JavaCompilerBeanPropertyReaderFactory.generate(Person.class, "name");
    }

    @Benchmark
    public Object _400_LamdbaMetafactoryBootstrap() {
        return new LambdaMetafactoryBeanPropertyReader(Person.class, "name");
    }

}
