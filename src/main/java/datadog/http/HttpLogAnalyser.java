/**
 *  Copyright Murex S.A.S., 2003-2018. All Rights Reserved.
 *
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package datadog.http;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

import datadog.http.listener.borad.DashboardCollector;
import datadog.http.listener.traffic.TrafficListener;

import org.apache.commons.cli.*;
import org.apache.commons.io.input.Tailer;


/**
 * Hello world!
 */
public class HttpLogAnalyser {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static String inputFilePath;
    private static long trafficThreshold;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        parseArgs(args);
        File file = new File(inputFilePath);
        Preconditions.checkArgument(file.exists(), "File: " + inputFilePath + " doesn't exists.");
        Preconditions.checkArgument(file.canRead(), "File: " + inputFilePath + " cannot be read.");
        ZonedDateTime startMonitoringTime = ZonedDateTime.now();
        PipelineTimer pipelineTimer = new PipelineTimer();
        pipelineTimer.subscribe(new DashboardCollector(startMonitoringTime));
        pipelineTimer.subscribe(new TrafficListener(startMonitoringTime, trafficThreshold));
        // Schedule a handle log bloc every 10 seconds
        scheduler.scheduleAtFixedRate(pipelineTimer::handleLogs, 10, 10, TimeUnit.SECONDS);
//        String pathname = "src/main/resources/httpaccess.log";
        Tailer tailer = new Tailer(file, pipelineTimer);
        tailer.run();
    }

    private static void parseArgs(String[] args) {
        Options options = new Options();
        Option input = new Option("i", "input", true, "input log borad path");
        input.setRequired(true);
        options.addOption(input);

        Option traffic = new Option("t", "traffic", true, "traffic threshold per second");
        traffic.setRequired(false);
        options.addOption(traffic);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            inputFilePath = cmd.getOptionValue("input");
            String trafficOption = cmd.getOptionValue("traffic");
            trafficThreshold = (trafficOption == null) ? 10 : Long.parseLong(trafficOption);
            System.out.println(inputFilePath);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("HttpLogAnalyser", options);
            System.exit(1);
        }
    }

}
