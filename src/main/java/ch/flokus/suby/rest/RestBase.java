package ch.flokus.suby.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.enums.ServerStatus;
import ch.flokus.suby.model.Album;
import ch.flokus.suby.model.Song;
import ch.flokus.suby.service.AppUtils;
import ch.flokus.suby.service.SettingsService;
import ch.flokus.suby.settings.SettingsConstants;

public class RestBase {
    private SettingsService setService = null;
    private String user = "";
    private String pass = "";
    private String server = "";
    private String appname = "";
    private static RestBase instance = null;

    public static RestBase getInstance() {
        if (instance == null) {
            instance = new RestBase();
        }
        return instance;
    }

    private RestBase() {
        setService = new SettingsService();
        refresh();
    }

    public void refresh() {
        user = setService.getSetting(SettingsConstants.USERNAME);
        pass = toHex(setService.getSetting(SettingsConstants.PASSWORD));
        server = setService.getSetting(SettingsConstants.SERVER);
        appname = setService.getSetting(SettingsConstants.APPNAME);
    }

    public JSONObject getJson(String view) {
        String restbase = server + "/rest/" + view + "?u=" + user + "&p=enc:" + pass + "&v=1.10.0&c=" + appname + "&f=json";
        return request(restbase);
    }

    public JSONObject getJson(String view, String key, String value) {
        String restbase = server + "/rest/" + view + "?u=" + user + "&p=enc:" + pass + "&v=1.10.0&c=" + appname + "&f=json&" + key + "=" + value;
        return request(restbase);
    }

    public Song download(String songId) {
        // get song meta information
        JSONObject songmeta = getJson("getSong.view", "id", songId);
        songmeta = songmeta.getJSONObject("subsonic-response").getJSONObject("song");
        Song song = new Song(songmeta);
        // url for song download
        String restbase = server + "/rest/download.view?u=" + user + "&p=enc:" + pass + "&v=1.10.0&c=" + appname + "&f=json&id=" + songId;
        try {
            URL server = new URL(restbase);
            String absolute = System.getProperty("user.home") + "/Music/Suby/" + song.getPath();
            absolute = absolute.replaceAll("[^a-zA-ZÄäÖöÜüéèà0-9!().-/\\ ]", "_");
            File mp = new File(absolute);
            if (mp.exists()) {
                System.out.println("already downloaded");
            } else {
                System.out.println("downloading...");
                new AsyncDownloader(server, mp);
                System.out.println("done!");
            }
            absolute = "file://" + absolute.replaceAll(" ", "%20");
            song.setPath(absolute);
            return song;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getAlbumCover(Album a) {
        String restbase = server + "/rest/getCoverArt.view?u=" + user + "&p=enc:" + pass + "&v=1.10.0&c=" + appname + "&f=json&id=" + a.getCoverArt()
                + "&size=100";
        try {
            JSONObject o = request(restbase);
            if (o.getJSONObject("subsonic-response").get("status").equals("failed"))
                return;
        } catch (JSONException e) {
            URL server;
            try {
                server = new URL(restbase);
                String coverPath = System.getProperty("user.home") + "/Music/Suby/" + a.getArtist().replace("/", "_") + "/" + a.getName() + "/al-" + a.getId()
                        + ".jpg";
                File cover = new File(coverPath);
                if (!cover.exists()) {
                    new AsyncDownloader(server, cover);
                    // FileUtils.copyURLToFile(server, cover);
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
    }

    private JSONObject request(String restbase) {
        URL server;
        try {
            server = new URL(restbase);
            URLConnection connection = server.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            String json = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                json += inputLine;
            in.close();
            json = AppUtils.replaceAsciiChars(json);
            JSONObject obj = new JSONObject(json);
            if (obj.getJSONObject("subsonic-response").getString("status").equals("ok")) {
                Status.getInstance().setState(ServerStatus.OK);
            } else {
                Status.getInstance().setState(ServerStatus.ERROR);
            }
            return obj;
        } catch (MalformedURLException e1) {
            Status.getInstance().setState(ServerStatus.ERROR);
            e1.printStackTrace();
        } catch (IOException e1) {
            Status.getInstance().setState(ServerStatus.ERROR);
            e1.printStackTrace();
        }
        return new JSONObject();
    }

    public String toHex(String string) {
        return String.format("%040x", new BigInteger(1, string.getBytes(Charset.defaultCharset())));
    }
}
