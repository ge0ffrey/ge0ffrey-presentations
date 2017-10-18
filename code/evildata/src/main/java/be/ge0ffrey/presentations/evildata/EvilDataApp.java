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

import java.util.regex.Pattern;

public class EvilDataApp {

    public static void main(String[] args) {
        doubleRounding();
        doubleAssociate();
        doubleIncrement();
        regexI18n();
    }

    private static void doubleRounding() {
        System.out.println("Double rounding");
        System.out.println("================\n");
        System.out.println("    0.01 + 0.09 == 0.10: " + ((0.01 + 0.09) == 0.10)); // false
        System.out.printf("\n   %s\n + %s\n = %s\n", 0.01, 0.09, 0.01 + 0.09);
        System.out.printf("\n   %s\n + %s\n = %s\n", 0.01, 0.05, 0.01 + 0.05);
        System.out.println();
    }

    private static void doubleAssociate() {
        System.out.println("Double associative");
        System.out.println("==================\n");
        System.out.println("    (0.01 + 0.02) + 0.03 == 0.01 + (0.02 + 0.03): " + ((0.01 + 0.02) + 0.03 == 0.01 + (0.02 + 0.03))); // false
        System.out.printf("\n   %s\n + %s\n + %s\n = %s\n", 0.01, 0.02, 0.03, 0.01 + 0.02 + 0.03);
        System.out.printf("\n   %s\n + %s\n + %s\n = %s\n", 0.03, 0.02, 0.01, 0.03 + 0.02 + 0.01);
        System.out.println();
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
        System.out.println();
    }

    private static void regexI18n() {
        String validName = "ValidName";
        String invalidName = "Invalid/name";
        String japaneseValidName = "有効名";
        String japaneseInvalidName = "無効/名";

        String badRegex = "\\w+";
        String goodRegex = "(?U)\\w+";

        System.out.println("I18n in regular expressions");
        System.out.println("===========================\n");
        System.out.println();
        System.out.println("    Regular expression \"" + badRegex + "\"");
        System.out.println("        \"" + validName + "\" (valid): " + validName.matches(badRegex));
        System.out.println("        \"" + invalidName + "\" (invalid): " + invalidName.matches(badRegex));
        System.out.println("        \"" + japaneseValidName + " (valid)\": " + japaneseValidName.matches(badRegex) + " <---------- false negative");
        System.out.println("        \"" + japaneseInvalidName + "\" (invalid): " + japaneseInvalidName.matches(badRegex));
        System.out.println();
        System.out.println("    Regular expression \"" + goodRegex + "\"");
        System.out.println("        \"" + validName + "\" (valid): " + validName.matches(goodRegex));
        System.out.println("        \"" + invalidName + "\" (invalid): " + invalidName.matches(goodRegex));
        System.out.println("        \"" + japaneseValidName + "\" (valid): " + japaneseValidName.matches(goodRegex));
        System.out.println("        \"" + japaneseInvalidName + "\" (invalid): " + japaneseInvalidName.matches(goodRegex));
        System.out.println();
    }

}
