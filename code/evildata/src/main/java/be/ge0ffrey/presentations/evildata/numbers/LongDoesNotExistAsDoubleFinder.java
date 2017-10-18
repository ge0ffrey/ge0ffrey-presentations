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

package be.ge0ffrey.presentations.evildata.numbers;

public class LongDoesNotExistAsDoubleFinder {

    public static void main(String[] args) {
        // 9007199254740992
        double total = 9_007_199_000_000_000.0;
        for (int i = 0; ; i++) {
            double nextTotal = total + 1.0;
            if ((long) nextTotal != ((long) total + 1L)) {
                System.out.println(">>> NOT OK " + total + " + 1.0 != " + ((long) total + 1L) + " <<<");
                System.out.println("The number " +((long) total + 1L) + " exists as a long, but not as a double.");
                break;
            }
            if (i % 1000000 == 0) {
                System.out.println("    OK: " + total + " +  1.0 = " + ((long) total + 1L));
            }
            total = nextTotal;
        }

    }

}
