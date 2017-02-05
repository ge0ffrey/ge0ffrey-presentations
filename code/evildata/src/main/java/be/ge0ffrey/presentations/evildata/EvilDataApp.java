/*
 * Copyright 2017 JBoss Inc
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

package be.ge0ffrey.presentations.evildata;

public class EvilDataApp {

    public static void main(String[] args) {
        doubleIncrement();
    }

    private static void doubleIncrement() {
        System.out.println("Double increment");
        System.out.println("================");
        double a = 9_007_199_254_740_992.0;
        System.out.println(" a == (a + 1.0): " + (a == (a + 1.0))); // true
        double b = a + 1.0;
        System.out.printf("\n   %18.1f\n + %18.1f\n = %18.1f\n", a, 1.0, b);
        double c = a + 3.0;
        System.out.printf("\n   %18.1f\n + %18.1f\n = %18.1f\n", a, 3.0, c);
    }

}
