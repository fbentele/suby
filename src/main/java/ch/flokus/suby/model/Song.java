package ch.flokus.suby.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Song {
    private String id;
    private String title;
    private String album;
    private String albumId;
    private String artist;
    private String duration;
    private String path;
    private String coverArt;
    private String suffix;

    public Song() {
    }

    public Song(JSONObject songJson) {
        super();
        this.id = songJson.get("id").toString();
        this.title = songJson.get("title").toString();
        this.album = songJson.get("album").toString();
        this.albumId = songJson.get("albumId").toString();
        this.artist = songJson.get("artist").toString();
        this.duration = songJson.get("duration").toString();
        this.suffix = songJson.get("suffix").toString();
        this.path = songJson.get("path").toString();
        
        try {
            this.coverArt = songJson.get("coverArt").toString();
        } catch (JSONException e) {

        }
    }

    public Song(String id, String title, String album, String artist, String duration, String path, String coverArt) {
        super();
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.path = path;
        this.coverArt = coverArt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getNiceDuration() {
        Float raw = Float.valueOf(duration);
        int min = (int) (raw / 60);
        int sec = (int) (raw % 60);
        String mins = "" + min;
        String secs = "" + sec;
        if (min < 10)
            mins = "0" + mins;

        if (sec < 10)
            secs = "0" + secs;

        return mins + ":" + secs;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCoverArt() {
        return coverArt;
    }

    public void setCoverArt(String coverArt) {
        this.coverArt = coverArt;
    }

    public String getFullCoverArtPath() {
        String s = System.getProperty("user.home") + "/Music/Suby/" + getArtist().replace("/", "_") + "/" + getAlbum() + "/al-" + getAlbumId() + ".jpg";
        return s;
    }
}
