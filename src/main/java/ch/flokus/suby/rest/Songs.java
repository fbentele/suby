package ch.flokus.suby.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.model.Song;

public class Songs {
	private RestBase rest = null;

	public Songs() {
		rest = new RestBase();
	}

	public List<Song> getSongsForAlbum(Integer id) {
		List<Song> songlist = new ArrayList<Song>();
		JSONObject raw = rest.getJson("getAlbum.view", "id", id.toString());
		try {
			JSONArray songs = raw.getJSONObject("subsonic-response").getJSONObject("album")
					.getJSONArray("song");
			for (int i = 0; i < songs.length(); i++) {
				JSONObject current = songs.getJSONObject(i);
				songlist.add(new Song(current));
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return songlist;
	}
}
