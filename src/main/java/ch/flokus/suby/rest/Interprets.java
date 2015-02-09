package ch.flokus.suby.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Interprets {
    private RestBase rest = null;

    public Interprets() {
        rest = new RestBase();
    }

    public List<String> getAll() {
        List<String> interprets = new ArrayList<String>();
        JSONObject raw = rest.getJson("getArtists.view");
        try {
            JSONArray ijson = raw.getJSONObject("subsonic-response").getJSONObject("artists").getJSONArray("index");
            for (int i = 0; i < ijson.length(); i++) {
                JSONObject letter = ijson.getJSONObject(i);
                try {
                    JSONArray letart = letter.getJSONArray("artist");
                    for (int j = 0; j < letart.length(); j++) {
                        JSONObject thei = letart.getJSONObject(j);
                        interprets.add(thei.getString("name") + " (" + thei.getInt("id") + ")");
                    }
                } catch (JSONException e) {
                    // no interprets for letter
                    continue;
                }
            }
        } catch (JSONException e) {
            
        }
        try {
            String error = raw.getJSONObject("subsonic-response").getJSONObject("error").getString("message");
            System.out.println(error);
        } catch (Exception e) {

        }
        return interprets;
    }
}
