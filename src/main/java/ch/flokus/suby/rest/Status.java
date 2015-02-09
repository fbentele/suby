package ch.flokus.suby.rest;

import org.json.JSONObject;

import ch.flokus.suby.enums.ServerStatus;

public class Status {
    private RestBase rest = null;
    private ServerStatus state = ServerStatus.UNKNOWN;

    public Status() {
        rest = new RestBase();
    }

    public String testConnection() {
        JSONObject obj = rest.getJson("ping.view");
        String status = obj.getJSONObject("subsonic-response").getString("status");
        if (status.equals("failed")) {
            state = ServerStatus.ERROR;
            status = obj.getJSONObject("subsonic-response").getJSONObject("error").getString("message");
        } else if (status.equals("ok")) {
            state = ServerStatus.OK;
        }
        return status;
    }

    public ServerStatus getState() {
        return state;
    }

}
