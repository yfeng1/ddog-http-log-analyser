/**
 *  Copyright Murex S.A.S., 2003-2018. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package datadog.http.listener.traffic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.time.ZonedDateTime;

import datadog.http.log.HttpLog;
import datadog.http.log.Parser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TrafficListenerTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static final String LINE_SEPATATOR = System.getProperty("line.separator");

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final TrafficListener trafficListener = new TrafficListener(ZonedDateTime.now(), 1); // 1 * 10 for 10 s

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
    public void testAlertMessage1() throws InterruptedException {
        ZonedDateTime now = ZonedDateTime.now();
        for (int i = 0; i < 11; i++) {
            trafficListener.handleLogLine(new HttpLog("host", "ident", "authuser", now, "", "", "", 200, 10));
        }
        trafficListener.handleLogBloc();
        Assert.assertEquals("High traffic generated an alert - hits = {11}, triggered at {" + now.format(Parser.DATE_TIME_FORMATTER) + "}" + LINE_SEPATATOR, outContent.toString());
    }

    @Test
    public void testAlertMessage2() {
        ZonedDateTime now = ZonedDateTime.now();
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 10; j++) {
                trafficListener.handleLogLine(new HttpLog("host", "ident", "authuser", now, "", "", "", 200, 10));
            }
            trafficListener.handleLogBloc();
        }
        for (int j = 0; j < 11; j++) {
            trafficListener.handleLogLine(new HttpLog("host", "ident", "authuser", now, "", "", "", 200, 10));
        }
        trafficListener.handleLogBloc();

        Assert.assertEquals("High traffic generated an alert - hits = {121}, triggered at {" + now.format(Parser.DATE_TIME_FORMATTER) + "}" + LINE_SEPATATOR, outContent.toString());
    }

    @Test
    public void testNoAlertMessage() {
        ZonedDateTime now = ZonedDateTime.now();
        for (int i = 0; i < 13; i++) {
            trafficListener.handleLogLine(new HttpLog("host", "ident", "authuser", now, "", "", "", 200, 10));
            trafficListener.handleLogBloc();
        }
        Assert.assertEquals("", outContent.toString());
    }

    @Test
    public void testAlertRecoveredMessage() {
        ZonedDateTime now = ZonedDateTime.now();
        for (int i = 0; i < 11; i++) {
            trafficListener.handleLogLine(new HttpLog("host", "ident", "authuser", now, "", "", "", 200, 10));
        }
        trafficListener.handleLogBloc();
        Assert.assertEquals("High traffic generated an alert - hits = {11}, triggered at {" + now.format(Parser.DATE_TIME_FORMATTER) + "}" + LINE_SEPATATOR, outContent.toString());
        outContent.reset();
        trafficListener.handleLogBloc();
        Assert.assertEquals("High traffic alert is recovered." + LINE_SEPATATOR, outContent.toString());
    }

}
