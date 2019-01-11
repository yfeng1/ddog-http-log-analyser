/**
 *  Copyright Murex S.A.S., 2003-2018. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package datadog.http.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;


public class ParserTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Test
    public void test() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        String dt = "09/Jul/2018:16:00:42 -0700";
        LocalDateTime parse = LocalDateTime.parse(dt, dateTimeFormatter);

//        String format = OffsetDateTime.now().format(dateTimeFormatter);
        System.out.println(parse);

        HttpLog httpLog = Parser.parse("127.0.0.1 - mary [" + dt + "] \"POST /api/user HTTP/1.0\" 503 12");
        Assert.assertEquals("127.0.0.1", httpLog.getHost());
        Assert.assertEquals("-", httpLog.getIdent());
        Assert.assertEquals("mary", httpLog.getAuthuser());
        Assert.assertEquals("mary", httpLog.getAuthuser());
        Assert.assertEquals("POST", httpLog.getRequest());
        Assert.assertEquals("/api/user", httpLog.getResource());
        Assert.assertEquals("HTTP/1.0", httpLog.getProtocol());
        Assert.assertEquals(503, httpLog.getStatus());
        Assert.assertEquals(12, httpLog.getBytes());
    }

}
