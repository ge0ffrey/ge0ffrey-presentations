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

package be.ge0ffrey.presentations.cornercasecheatsheet.text;

public class RogueTextApp {

    public static void main(String[] args) {
        all();
    }

    public static void all() {
        regexI18n();
    }

    private static void regexI18n() {
        System.out.println("I18n in regular expressions");
        System.out.println("===========================\n");

        String validName = "ValidName";
        String invalidName = "Invalid/name";
        String japaneseValidName = "有効名";
        String japaneseInvalidName = "無効/名";

        String badRegex = "\\w+";
        String goodRegex = "(?U)\\w+";

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
