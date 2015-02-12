package ch.flokus.suby.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.model.Artist;

public class Artists {
    private RestBase rest = null;

    public Artists() {
        rest = RestBase.getInstance();
    }

    public List<Artist> getAll() {
        List<Artist> artists = new ArrayList<Artist>();
        JSONObject raw = rest.getJson("getArtists.view");
        try {
            JSONArray ijson = raw.getJSONObject("artists").getJSONArray("index");
            for (int i = 0; i < ijson.length(); i++) {
                JSONObject letter = ijson.getJSONObject(i);
                try {
                    JSONArray letart = letter.getJSONArray("artist");
                    for (int j = 0; j < letart.length(); j++) {
                        JSONObject thei = letart.getJSONObject(j);
                        artists.add(new Artist(thei));
                    }
                } catch (JSONException e) {
                    continue;
                }
            }
        } catch (JSONException e) {

        }
        try {
            String error = raw.getJSONObject("error").getString("message");
            System.out.println(error);
        } catch (Exception e) {

        }
        return artists;
    }
}
