package com.nexmo.mediarest.demo;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@javax.ws.rs.Path("/provisioning/json")
@javax.ws.rs.Produces(MediaType.APPLICATION_JSON)
public class DummyAuthResourcePassword {
    @javax.ws.rs.GET
    public String validate(@QueryParam("cmd") String cmd, @QueryParam("account") String key) {
        String passwd = "secret1";
        boolean banned = false;
        int result = 0;
        String ac = ", \"account\":{\"sysid\":\""+key+"\", \"password\":\""+passwd+"\", \"banned\":\""+(banned?"true":"false")+"\"}";
        return "{\"command\":\""+cmd+"\",\"result-code\":"+result+ac+"}";
    }
}