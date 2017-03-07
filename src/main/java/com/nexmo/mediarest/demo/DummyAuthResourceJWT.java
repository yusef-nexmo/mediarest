package com.nexmo.mediarest.demo;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@javax.ws.rs.Path("/auth/json")   
@javax.ws.rs.Produces(MediaType.APPLICATION_JSON)
public class DummyAuthResourceJWT {
    @javax.ws.rs.GET
    public String validate(@QueryParam("cmd") String cmd, @QueryParam("token") String token) {
        String apikey = "jwtuser1";
        String appid = "jwtapp1";
        int result = 0;
        String ac = ", \"token\":{\"api-key\":\""+apikey+"\", \"application\":\""+appid+"\"}";
        return "{\"command\":\""+cmd+"\",\"result-code\":"+result+ac+"}";
    }
}