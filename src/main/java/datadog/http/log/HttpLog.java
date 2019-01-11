/**
 *  Copyright Murex S.A.S., 2003-2018. All Rights Reserved.
 *
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package datadog.http.log;

import java.time.ZonedDateTime;


public class HttpLog {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    private final String host;
    private final String ident;
    private final String authuser;
    private final ZonedDateTime zonedDateTime;
    private final String request;
    private final String resource;
    private final String protocol;
    private final int status;
    private final long bytes;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public HttpLog(String host, String ident, String authuser, ZonedDateTime zonedDateTime, String request, String resource, String protocol, int status, long bytes) {
        this.host = host;
        this.ident = ident;
        this.authuser = authuser;
        this.zonedDateTime = zonedDateTime;
        this.request = request;
        this.resource = resource;
        this.protocol = protocol;
        this.status = status;
        this.bytes = bytes;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public String getHost() {
        return host;
    }

    public String getIdent() {
        return ident;
    }

    public String getAuthuser() {
        return authuser;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public String getResource() {
        return resource;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRequest() {
        return request;
    }

    public int getStatus() {
        return status;
    }

    public long getBytes() {
        return bytes;
    }
}
