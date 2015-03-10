package ch.flokus.suby.model;

import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.service.AppUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "song")
public class SongModel {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String album;
    @DatabaseField
    private String albumId;
    @DatabaseField
    private String artist;
    @DatabaseField
    private String duration;
    @DatabaseField
    private String path;
    @DatabaseField
    private String coverArt;
    @DatabaseField
    private String suffix;

    public SongModel() {
    }

    public SongModel(JSONObject songJson) {
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

    public SongModel(String id, String title, String album, String artist, String duration, String path, String coverArt) {
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
        return AppUtils.getNiceTime(duration);
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
