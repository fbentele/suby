package ch.flokus.suby.model;

import org.json.JSONObject;

public class Artist {
    private String id;
    private String name;

    public Artist(JSONObject raw) {
        this.id = raw.get("id").toString();
        this.name = raw.get("name").toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
