/**
 *  Copyright Murex S.A.S., 2003-2018. All Rights Reserved.
 *
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package datadog.http.log;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final String LOG_REGEX = "^(\\S+) (\\S+) (\\S+) \\[([\\w/:]+\\s[+\\-]\\d{4})\\] \"(\\S+)\\s?(\\S+)?\\s?(\\S+)?\" (\\d{3}|-) (\\d+|-)$";
    private static final Pattern LOG_PATTERN = Pattern.compile(LOG_REGEX);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static HttpLog parse(String logLine) {
        final Matcher matcher = LOG_PATTERN.matcher(logLine);

        if (matcher.find()) {
            return new HttpLog(matcher.group(1), matcher.group(2), matcher.group(3), ZonedDateTime.parse(matcher.group(4), DATE_TIME_FORMATTER), matcher.group(5), matcher.group(6), matcher.group(7),
                Integer.parseInt(matcher.group(8)), Long.parseLong(matcher.group(9)));
        }
        return null;
    }
}
