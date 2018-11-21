package com.dgtz.compress.api;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/22/13
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Resource {
    private static Gson gson	= new Gson();

    protected javax.ws.rs.core.Response createResponse(Object obj) {
        // Response response = null;
        if (obj == null){
            return javax.ws.rs.core.Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity("ERROR PAGE NOT FOUND ").build();
        }
        else {
            // response =
            return javax.ws.rs.core.Response.status(HttpStatus.SC_OK).entity(gson.toJson(obj)).build();
        }
        // TODO: add logging action HERE
        // return response;
    }

    protected javax.ws.rs.core.Response createErrorResponse(JSONObject obj, int errorNum) {

        switch (errorNum)
        {
            case 404:{return javax.ws.rs.core.Response.status(HttpStatus.SC_NOT_FOUND).entity(obj.toString()).build();}
            case 500:{return javax.ws.rs.core.Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(obj.toString()).build();}
            case 503:{return javax.ws.rs.core.Response.status(HttpStatus.SC_SERVICE_UNAVAILABLE).entity(obj.toString()).build();}
            case 403:{return javax.ws.rs.core.Response.status(HttpStatus.SC_FORBIDDEN).entity(obj.toString()).build();}
            default:{
                return javax.ws.rs.core.Response.status(HttpStatus.SC_BAD_REQUEST).entity(obj.toString()).build();
            }
        }


    }

    protected javax.ws.rs.core.Response createResponse(JSONObject obj) {
        // Response response = null;
        if (obj == null){
            return javax.ws.rs.core.Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity("ERROR PAGE NOT FOUND ").build();
        }
        else {
            // response =
            return javax.ws.rs.core.Response.status(HttpStatus.SC_OK).entity(obj.toString()).build();
        }
        // TODO: add logging action HERE
        // return response;
    }
}
