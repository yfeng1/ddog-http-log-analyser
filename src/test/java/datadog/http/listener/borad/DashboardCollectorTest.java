/**
 *  Copyright Murex S.A.S., 2003-2018. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package datadog.http.listener.borad;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.time.ZonedDateTime;

import datadog.http.log.HttpLog;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class DashboardCollectorTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static final String LINE_SEPATATOR = System.getProperty("line.separator");

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final DashboardCollector dashboardCollector = new DashboardCollector(ZonedDateTime.now());

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void test() {
        dashboardCollector.handleLogLine(new HttpLog("host", "ident", "authuser", ZonedDateTime.now(), "", "/test1", "", 200, 10));
        dashboardCollector.handleLogBloc();
        Assert.assertEquals("Most hit section(s) during last 10 seconds is(are): /test1" + LINE_SEPATATOR, outContent.toString());
    }

    @Test
    public void test1() {
        dashboardCollector.handleLogLine(new HttpLog("host", "ident", "authuser", ZonedDateTime.now(), "", "/test1", "", 200, 10));
        dashboardCollector.handleLogLine(new HttpLog("host", "ident", "authuser", ZonedDateTime.now(), "", "/test1", "", 200, 10));
        dashboardCollector.handleLogLine(new HttpLog("host", "ident", "authuser", ZonedDateTime.now(), "", "/test2", "", 200, 10));
        dashboardCollector.handleLogBloc();
        Assert.assertEquals("Most hit section(s) during last 10 seconds is(are): /test1" + LINE_SEPATATOR, outContent.toString());
    }

    @Test
    public void test2() {
        dashboardCollector.handleLogLine(new HttpLog("host", "ident", "authuser", ZonedDateTime.now(), "", "/test1", "", 200, 10));
        dashboardCollector.handleLogLine(new HttpLog("host", "ident", "authuser", ZonedDateTime.now(), "", "/test2", "", 200, 10));
        dashboardCollector.handleLogBloc();
        Assert.assertEquals("Most hit section(s) during last 10 seconds is(are): /test1, /test2" + LINE_SEPATATOR, outContent.toString());
    }

}
