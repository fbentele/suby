package ch.flokus.suby.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.model.Artist;

public class RestArtists {
    private RestBase rest = null;

    public RestArtists() {
        rest = RestBase.getInstance();
    }

    // TODO refactoring needed
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
                        JSONObject theArtist = letart.getJSONObject(j);
                        artists.add(new Artist(theArtist));
                    }
                } catch (JSONException e) {
                    System.out.println("Problem parsing Artist JSON: " + e.getMessage());
                    continue;
                }
            }
        } catch (JSONException e) {
            System.out.println("Problem getting Artist JSON: " + e.getMessage());
        }
        try {
            String error = raw.getJSONObject("error").getString("message");
            System.out.println(error);
        } catch (Exception e) {

        }
        return artists;
    }
}
