package ch.flokus.suby.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Album {
	private String id;
	private String name;
	private String coverArt;
	private String songCount;
	private String artistId;
	private String artist;
	private String year;

	public Album() {
	}

	public Album(String id, String name, String coverArt, String songCount, String artistId,
			String artist, String year) {
		super();
		this.id = id;
		this.name = name;
		this.coverArt = coverArt;
		this.songCount = songCount;
		this.artistId = artistId;
		this.artist = artist;
		this.year = year;
	}

	public Album(JSONObject raw) {
		this.id = raw.get("id").toString();
		this.name = raw.get("name").toString();
		try {
			this.coverArt = raw.get("coverArt").toString();
		} catch (JSONException e) {

		}
		this.songCount = raw.get("songCount").toString();
		this.artistId = raw.get("artistId").toString();
		this.artist = raw.get("artist").toString();
		this.year = raw.get("year").toString();
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

	public String getCoverArt() {
		return coverArt;
	}

	public void setCoverArt(String coverArt) {
		this.coverArt = coverArt;
	}

	public String getSongCount() {
		return songCount;
	}

	public void setSongCount(String songCount) {
		this.songCount = songCount;
	}

	public String getArtistId() {
		return artistId;
	}

	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getFullCoverArtPath() {
		String s = System.getProperty("user.home") + "/Music/Suby/" + getArtist().replace("/", "_")
				+ "/" + getName() + "/al-" + getId() + ".jpg";
		return s;
	}
}
