package ch.flokus.suby.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import ch.flokus.suby.App;
import ch.flokus.suby.enums.ServerStatus;
import ch.flokus.suby.model.Album;
import ch.flokus.suby.model.SongModel;
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
        pass = AppUtils.toHex(setService.getSetting(SettingsConstants.PASSWORD));
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

<<<<<<< HEAD
    public SongModel download(String songId, boolean async) {
        // get song meta information
        JSONObject songmeta = getJson("getSong.view", "id", songId);
        try {
            songmeta = songmeta.getJSONObject("song");
        } catch (JSONException e) {
            System.out.println("Problem getting song json: " + e.getMessage());
            return null;
        }
        SongModel song = new SongModel(songmeta);
=======
	public SongModel download(String songId, boolean async) {
		// get song meta information
		JSONObject songmeta = getJson("getSong.view", "id", songId).getJSONObject("song");
		SongModel song = new SongModel(songmeta);
		// url for song download
		String restbase = server + "/rest/download.view?u=" + user + "&p=enc:" + pass
				+ "&v=1.10.0&c=" + appname + "&f=json&id=" + songId;
		try {
			URL server = new URL(restbase);
			String absolute = System.getProperty("user.home") + "/Music/Suby/" + song.getPath();
			System.out.println("---- before -----");
			System.out.println(absolute);
			absolute = absolute.replaceAll("[^a-zA-ZÄäÖöÜüéèà0-9!().\\-/\\ ]", "_");
			System.out.println("---- after -----");
			System.out.println(absolute);
			File songFile = new File(absolute);
			if (songFile.exists()) {
				System.out.println("Song with ID: " + songId + " already downloaded");
			} else {
				System.out.println("Start downloading song with ID: " + songId);
				if (async) {
					new AsyncDownloader(server, songFile, App.mainView);
				} else {
					FileUtils.copyURLToFile(server, songFile);
				}
			}
			absolute = "file://" + absolute.replaceAll(" ", "%20");
			song.setPath(absolute);
			return song;
		} catch (MalformedURLException e) {
			System.out.println("URL is malformed: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO exception, server responded not with a file, original message: "
					+ e.getMessage());
		}
		return null;
	}
>>>>>>> 643040bcb219d8c5ac08af1001e826cb8ff08bc7

        // url for song download
        String restbase = server + "/rest/download.view?u=" + user + "&p=enc:" + pass + "&v=1.10.0&c=" + appname + "&f=json&id=" + songId;
        try {
            URL server = new URL(restbase);
            String absolute = System.getProperty("user.home") + "/Music/Suby/" + song.getPath();
            System.out.println("---- before -----");
            System.out.println(absolute);
            absolute = absolute.replaceAll("[^a-zA-ZÄäÖöÜüéèà0-9!().\\-/\\ ]", "_");
            System.out.println("---- after -----");
            System.out.println(absolute);
            File mp = new File(absolute);
            if (mp.exists()) {
                System.out.println(songId + ": already downloaded");
            } else {
                System.out.println("downloading id:" + songId);
                if (async) {
                    new AsyncDownloader(server, mp, App.mainView);
                } else {
                    FileUtils.copyURLToFile(server, mp);
                }
            }
            absolute = "file://" + absolute.replaceAll(" ", "%20");
            song.setPath(absolute);
            return song;
        } catch (MalformedURLException e) {
            System.out.println("URL is malformed: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("io exception" + e.getMessage());
        }
        return null;
    }

    public void getAlbumCover(Album a) {
        String restbase = server + "/rest/getCoverArt.view?u=" + user + "&p=enc:" + pass + "&v=1.10.0&c=" + appname + "&f=json&id=" + a.getCoverArt()
                + "&size=100";
        try {
            URL server = new URL(restbase);
            String coverPath = System.getProperty("user.home") + "/Music/Suby/" + a.getArtist().replaceAll("/", "_") + "/" + a.getName().replaceAll("/", "_")
                    + "/al-" + a.getId() + ".jpg";
            File cover = new File(coverPath);
            if (!cover.exists()) {
                new AsyncDownloader(server, cover, App.mainView);
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
    }

    private JSONObject request(String restbase) {
        URL server;
        BufferedReader in;
        try {
            server = new URL(restbase);
            URLConnection connection = server.openConnection();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            String json = "";
            String inputLine;
            StringBuilder jsonsb = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                jsonsb.append(inputLine);
            in.close();
            json = AppUtils.replaceAsciiChars(jsonsb.toString());
            JSONObject obj = new JSONObject(json);
            if (obj.getJSONObject("subsonic-response").getString("status").equals("ok")) {
                Status.getInstance().setState(ServerStatus.OK);
                return obj.getJSONObject("subsonic-response");
            } else {
                String error = obj.getJSONObject("error").getString("message");
                System.out.println(error);
                Status.getInstance().setState(ServerStatus.UNKNOWN);
            }
        } catch (MalformedURLException e1) {
            Status.getInstance().setState(ServerStatus.ERROR);
            System.out.println(e1.getMessage());
        } catch (IOException e1) {
            Status.getInstance().setState(ServerStatus.ERROR);
            System.out.println(e1.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Can not establish connection to url " + restbase);
        } catch (JSONException e) {
            System.out.println("Json Exception occured: " + e.getMessage());
            System.out.println("for url: " + restbase);
        }
        return new JSONObject();
    }
}
