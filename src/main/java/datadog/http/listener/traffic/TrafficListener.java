/**
 *  Copyright Murex S.A.S., 2003-2018. All Rights Reserved.
 *
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package datadog.http.listener.traffic;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;

import datadog.http.listener.LogListener;
import datadog.http.log.HttpLog;
import datadog.http.log.Parser;


public class TrafficListener implements LogListener {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    private ZonedDateTime startMonitoringTime;
    private final long trafficThresholdForTenSecs;
    private final long[] trafficPerWindows = new long[12]; // 12 * 10s = 2 min
    private long startingIndex = 0;
    private long requestCount = 0;
    private boolean highTraffic = false;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public TrafficListener(ZonedDateTime startMonitoringTime) {
        this(startMonitoringTime, 10);
    }

    public TrafficListener(ZonedDateTime startMonitoringTime, long trafficThreshold) {
        this.startMonitoringTime = startMonitoringTime;
        this.trafficThresholdForTenSecs = trafficThreshold * 10;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    public void handleLogLine(HttpLog line) {
        ZonedDateTime zonedDateTime = line.getZonedDateTime();
        long between = ChronoUnit.SECONDS.between(startMonitoringTime, zonedDateTime);
        if (between >= 0) {
            requestCount++;
        }
    }

    @Override
    public void handleLogBloc() {
        ZonedDateTime now = ZonedDateTime.now();
        trafficPerWindows[(int) startingIndex % 12] = requestCount;
        requestCount = 0;
        startingIndex++;
        long sum = Arrays.stream(trafficPerWindows).sum();
        long count = (startingIndex >= 12) ? 12 : startingIndex;
        if ((sum / (count * 1.0)) > trafficThresholdForTenSecs) {
            if (!highTraffic) {
                highTraffic = true;
                System.out.println("High traffic generated an alert - hits = {" + sum + "}, triggered at {" + now.format(Parser.DATE_TIME_FORMATTER) + "}");
            }
        } else {
            if (highTraffic) {
                highTraffic = false;
                System.out.println("High traffic alert is recovered.");
            }
        }
    }
}
