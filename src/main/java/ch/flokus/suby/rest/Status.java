package ch.flokus.suby.rest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ch.flokus.suby.enums.ServerStatus;

public class Status {
    private RestBase rest = null;
    private ServerStatus state = null;
    private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

    private static Status instance = null;
    
    private Status() {
        rest = new RestBase();
        state = ServerStatus.UNKNOWN;
    }
    
    public static Status getInstance(){
        if (instance == null){
            instance = new Status();
        }
        return instance;
    }

    public String testConnection() {
        JSONObject obj = rest.getJson("ping.view");
        String status = obj.getJSONObject("subsonic-response").getString("status");
        if (status.equals("failed")) {
            setState(ServerStatus.ERROR);
            status = obj.getJSONObject("subsonic-response").getJSONObject("error").getString("message");
        } else if (status.equals("ok")) {
            setState(ServerStatus.OK);
        }
        return status;
    }

    public void setState(ServerStatus s) {
        notifyListeners(this, "serverState", state.toString(), s.toString());
        this.state = s;
    }

    public ServerStatus getState() {
        return state;
    }

    private void notifyListeners(Object object, String property, String oldValue, String newValue) {
        for (PropertyChangeListener name : listener) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }
}
