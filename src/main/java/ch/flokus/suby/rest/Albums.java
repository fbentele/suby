package ch.flokus.suby.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.model.Album;

public class Albums {
    private RestBase rest = null;

    public Albums() {
        rest = new RestBase();
    }

    public List<Album> getAllForArtist(Integer id) {
        List<Album> interprets = new ArrayList<Album>();
        JSONObject obj = rest.getJson("getArtist.view", "id", id.toString());
        JSONArray albumJson;
        try {
            albumJson = obj.getJSONObject("subsonic-response").getJSONObject("artist").getJSONArray("album");
        } catch (JSONException e) {
            albumJson = new JSONArray(Collections.singletonList(obj.getJSONObject("subsonic-response").getJSONObject("artist").getJSONObject("album")));
        }
        for (int i = 0; i < albumJson.length(); i++) {
            JSONObject album = albumJson.getJSONObject(i);
            try {
                interprets.add(new Album(album));
            } catch (JSONException e) {
                System.out.println("album err on interpret " + id);
                continue;
            }
        }
        try {
            String error = obj.getJSONObject("subsonic-response").getJSONObject("error").getString("message");
            System.out.println(error);
        } catch (Exception e) {

        }
        return interprets;
    }

    public Album getAlbum(String id) {
        JSONObject raw = rest.getJson("getAlbum.view", "id", id.toString()).getJSONObject("subsonic-response").getJSONObject("album");
        Album a = new Album(raw);
        return a;
    }
}
