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

package be.ge0ffrey.presentations.evildata.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DstDateFinder {

    // UK and France changed DST on 2017-03-26 and 2017-10-29
    // US changed DST on 2017-03-12 and 2017-11-05

    // France did not change DST in 1955
    // UK changed DST on 1955-04-17 and 1955-10-02
    // US changed DST on 1955-04-24 and 1955-10-30

    public static void main(String[] args) throws Exception {
        javaUtilDate();
//        javaTime();
    }

    private static void javaUtilDate() throws ParseException {
        Locale defaultLocale = Locale.getDefault();
        System.out.println("java.util.Date (deprecated)");
        System.out.println("===========================");
        for (Locale locale : Arrays.asList(null, Locale.US, Locale.UK, Locale.FRANCE)) {
            if (locale != null) {
                Locale.setDefault(locale);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date a = dateFormat.parse("2017-03-25");
            Date b = dateFormat.parse("2017-03-27");
            long hours = (b.getTime() - a.getTime()) / 3_600_000L;
            System.out.printf("  In locale %s, there are %d hours between 2017-03-25 and 2017-03-27\n",
                    Locale.getDefault(), hours);
        }
        System.out.println();
        Locale.setDefault(defaultLocale);
    }

    private static void javaTime() {
        System.out.println("java.time.LocalDateTime");
        System.out.println("=======================");
        LocalDateTime a = LocalDateTime.of(2017, 3, 25, 8, 0);
        LocalDateTime b = LocalDateTime.of(2017, 3, 27, 8, 0);
        long hours = Duration.between(a, b).toHours();
        System.out.println(hours);

        ZonedDateTime.parse("2017-03-25T08:00:00+01:00[Europe/Paris]");
    }

}
