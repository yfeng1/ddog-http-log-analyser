/**
 *  Copyright Murex S.A.S., 2003-2018. All Rights Reserved.
 *
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package datadog.http;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.time.ZonedDateTime;

import java.util.Random;

import datadog.http.log.Parser;

import org.junit.Test;


public class WriterTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        Path file = Paths.get("src/main/resources/httpaccess.log");

        while (true) {
            int random = (int) (Math.random() * 10);
            String user = ((random % 3) == 0) ? "jia" : (((random % 3) == 1) ? "yang" : "-");
            String status = ((random % 3) == 0) ? "200" : (((random % 3) == 1) ? "404" : "501");
            String section = ((random % 3) == 0) ? "report" : (((random % 3) == 1) ? "dash" : "board");
            String line = "127.0.0.1 - " + user + " [" + ZonedDateTime.now().format(Parser.DATE_TIME_FORMATTER) + "] \"GET /" + section + " HTTP/1.0\" " + status + " 123" + System.lineSeparator();
            try {
                Files.write(file, line.getBytes(), StandardOpenOption.APPEND);
                Thread.sleep(1);
            } catch (InterruptedException | IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
