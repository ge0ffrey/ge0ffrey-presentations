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

package be.ge0ffrey.presentations.cornercasecheatsheet.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        // Changing Locale.setDefault() does not impact the timezone!
        TimeZone defaultTimeZone = TimeZone.getDefault();
        for (String[] inputs : new String[][]{{"2017-02-01", "2017-02-02"}, {"2017-03-12", "2017-03-13"}, {"2017-03-26", "2017-03-27"}}) {
            for (String timeZoneId : Arrays.asList("US/Eastern", "Europe/London", "Europe/Paris")) {
                TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date a = dateFormat.parse(inputs[0]);
                Date b = dateFormat.parse(inputs[1]);
                long hours = (b.getTime() - a.getTime()) / 3_600_000L;
                if (hours % 24 != 0) {
                    System.out.printf("  In time zone %16s, there are %d hours between %s and %s.\n",
                            timeZoneId, hours, inputs[0], inputs[1]);
                }
            }
        }
        System.out.println();

        for (String input : Arrays.asList("2017-01-01", "2017-03-25", "2017-03-26", "2017-03-27")) {
            for (String timeZoneId1 : Arrays.asList("America/New_York", "Europe/London", "Europe/Paris")) {
                for (String timeZoneId2 : Arrays.asList("America/New_York", "Europe/London", "Europe/Paris")) {
                    TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId1));
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat1.parse(input);
                    TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId2));
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                    String output = dateFormat2.format(date);
                    if (!input.equals(output)) {
                        System.out.printf("  Parsed in time zone %16s and formatted in time zone %16s, %s becomes %s.\n",
                                timeZoneId1, timeZoneId2, input, output);
                    }
                }
            }
        }
        System.out.println();
        TimeZone.setDefault(defaultTimeZone);
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
