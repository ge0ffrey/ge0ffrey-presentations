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
import java.util.Date;
import java.util.TimeZone;

public class RogueDateTimeApp {

    public static void main(String[] args) {
        all();
    }

    public static void all() {
        dstJavaUtilDate();
    }

    private static void dstJavaUtilDate() {
        TimeZone defaultTimeZone = TimeZone.getDefault();

        System.out.println("Daylight saving time and java.util.Date");
        System.out.println("=======================================\n");

        printDaysBetween("America/New_York", "2017-02-01", "2017-02-02");
        printDaysBetween("America/New_York", "2017-03-12", "2017-03-13");
        printDaysBetween("America/New_York", "2017-03-26", "2017-03-27");
        printDaysBetween("Europe/London", "2017-02-01", "2017-02-02");
        printDaysBetween("Europe/London", "2017-03-12", "2017-03-13");
        printDaysBetween("Europe/London", "2017-03-26", "2017-03-27");
        System.out.println();

        TimeZone.setDefault(defaultTimeZone);
    }

    private static void printDaysBetween(String timeZoneId, String fromDateString, String toDateString) {
        final long MILLISECONDS_IN_DAY = 24L * 60L * 60L * 1000L;
        final long MILLISECONDS_IN_HOUR = 60L * 60L * 1000L;
        // Changing Locale.setDefault() does not impact the timezone!
        TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date fromDate = dateFormat.parse(fromDateString);
            Date toDate = dateFormat.parse(toDateString);
            System.out.printf("    %d days (or %d hours) between %s and %s in timezone %s.\n",
                    (toDate.getTime() - fromDate.getTime()) / MILLISECONDS_IN_DAY,
                    (toDate.getTime() - fromDate.getTime()) / MILLISECONDS_IN_HOUR,
                    fromDateString, toDateString, timeZoneId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



}
