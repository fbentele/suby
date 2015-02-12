package ch.flokus.suby.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.model.Album;

public class RestAlbums {
    private RestBase rest = null;

    public RestAlbums() {
        rest = RestBase.getInstance();
    }

    public List<Album> getAllForArtist(Integer id) {
        List<Album> interprets = new ArrayList<Album>();
        JSONObject obj = rest.getJson("getArtist.view", "id", id.toString());
        JSONArray albumJson = new JSONArray();
        try {
            albumJson = obj.getJSONObject("artist").getJSONArray("album");
        } catch (JSONException e) {
            if (e.getMessage().contains("not a JSONArray")) {
                albumJson = new JSONArray(Collections.singletonList(obj.getJSONObject("artist").getJSONObject("album")));
            } else {
                System.out.println("Somthing is really wrong:" + e.getMessage());
            }
        }
        for (int i = 0; i < albumJson.length(); i++) {
            JSONObject album = albumJson.getJSONObject(i);
            try {
                interprets.add(new Album(album));
            } catch (JSONException e) {
                System.out.println("album err on interpret: " + id + " and album: " + album.get("name"));
                System.out.println(e.getMessage());
                continue;
            }
        }

        return interprets;
    }

    public Album getAlbum(String id) {
        JSONObject raw = rest.getJson("getAlbum.view", "id", id).getJSONObject("album");
        Album a = new Album(raw);
        return a;
    }
}
