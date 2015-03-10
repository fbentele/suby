package ch.flokus.suby.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.model.SongModel;

public class RestSongs {
    private RestBase rest;
    private static RestSongs instance;

    private RestSongs() {
        rest = RestBase.getInstance();
    }

    public static RestSongs getInstance() {
        if (instance == null) {
            instance = new RestSongs();
        }
        return instance;
    }

    public List<SongModel> getSongsForAlbum(Integer id) {
        List<SongModel> songlist = new ArrayList<SongModel>();
        JSONObject raw = rest.getJson("getAlbum.view", "id", id.toString());
        try {
            JSONArray songs = raw.getJSONObject("album").getJSONArray("song");
            for (int i = 0; i < songs.length(); i++) {
                JSONObject current = songs.getJSONObject(i);
                SongModel s = new SongModel(current);
                songlist.add(s);
            }
        } catch (JSONException e) {
            System.out.println("getSongsForAlbum in Class: " + this.getClass().getName());
            System.out.println(e.getMessage());
        }
        return songlist;
    }
}
