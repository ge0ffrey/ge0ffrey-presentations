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

import java.math.BigDecimal;

public class RogueNumberApp {

    public static void main(String[] args) {
        all();
    }

    public static void all() {
        intOverflow();
        doubleRounding();
        doubleAssociative();
        doubleIncrement();
    }

    private static void intOverflow() {
        System.out.println("Int overflow");
        System.out.println("================\n");
        System.out.println("    (1000000000 + 200000000) / 2 == 1500000000: " + ((1_000_000_000 + 2_000_000_000) / 2 == 1_500_000_000)); // false
        System.out.printf("\n    %s\n +  %s\n = %s\n", 1_000_000_000, 2_000_000_000, 1_000_000_000 + 2_000_000_000);
        System.out.println();
    }

    private static void doubleRounding() {
        System.out.println("Double rounding");
        System.out.println("================\n");
        System.out.println("    0.01 + 0.09 == 0.10: " + ((0.01 + 0.09) == 0.10)); // false
        System.out.printf("\n   %s\n + %s\n = %s\n", 0.01, 0.09, 0.01 + 0.09);
        System.out.printf("\n   %s\n + %s\n = %s\n", 0.01, 0.05, 0.01 + 0.05);
        System.out.println();


        System.out.println();
        System.out.println("                   0.01 = " + new BigDecimal(0.01).toString());
        System.out.println();
        System.out.println("                   0.09 = " + new BigDecimal(0.09).toString());
        System.out.println();
        System.out.println("            0.01 + 0.09 = " + new BigDecimal(0.01 + 0.09).toString());
        System.out.println("                   0.10 = " + new BigDecimal(0.10).toString());
    }

    private static void doubleAssociative() {
        System.out.println("Double associative");
        System.out.println("==================\n");
        System.out.println("    (0.01 + 0.02) + 0.03 == 0.01 + (0.02 + 0.03): " + ((0.01 + 0.02) + 0.03 == 0.01 + (0.02 + 0.03))); // false
        System.out.printf("\n   %s\n + %s\n + %s\n = %s\n", 0.01, 0.02, 0.03, 0.01 + 0.02 + 0.03);
        System.out.printf("\n   %s\n + %s\n + %s\n = %s\n", 0.03, 0.02, 0.01, 0.03 + 0.02 + 0.01);
        System.out.println();

//        double a = 0.0;
//        for (int i = 0; i < 100000; i++) {
//            a += 0.03 + 0.02 + 0.01;
//            System.out.println(a);
//            a -= 0.01 + 0.02 + 0.03;
//        }
    }

    private static void doubleIncrement() {
        System.out.println("Double increment");
        System.out.println("================\n");
        double a = 9_007_199_254_740_992.0;
        System.out.println("    a == (a + 1.0): " + (a == (a + 1.0))); // true
        System.out.println("    a + 3.0 - a == 4.0: " + (a + 3.0 - a == 4.0)); // true
        double b = a + 1.0;
        System.out.printf("\n   %18.1f\n + %18.1f\n = %18.1f\n", a, 1.0, b);
        double c = a + 3.0;
        System.out.printf("\n   %18.1f\n + %18.1f\n = %18.1f\n", a, 3.0, c);
        System.out.printf("\n   (double) %dL = %18.1f\n", 9_007_199_254_740_993L, (double) 9_007_199_254_740_993L);
        System.out.println();
    }

}
