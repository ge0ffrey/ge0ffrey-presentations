/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package be.ge0ffrey.presentations.cornercasecheatsheet.number;

public class CompoundRoundingErrorDoubleFinder {

    public static void main(String[] args) {
        int fail = 0;
        int total = 0;
        for (int i = 0; i < 100; i++) {
            double a = toCentiDouble(i);
            for (int j = 0; j < 100; j++) {
                double b = toCentiDouble(j);
                int k = i + j;
                double c = toCentiDouble(k);
                if (a + b != c) {
                    System.out.println("  " + a + " + " + b + " != " + c);
                    fail++;
                }
                total++;
            }

        }
        System.out.printf("%d failures (%1.0f%%) out of %d tries.", fail, (double) fail * 100.0 / total, total);

    }

    private static double toCentiDouble(int i) {
        int modulus = i % 100;
        return Double.parseDouble((i / 100) + "." + (modulus < 10 ? "0" : "") + modulus);
    }

}
