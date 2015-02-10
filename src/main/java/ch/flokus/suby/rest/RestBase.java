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

import ch.flokus.suby.model.Song;
import ch.flokus.suby.service.SettingsService;
import ch.flokus.suby.settings.SettingsConstants;

public class RestBase {
	SettingsService setService = null;
	String user = "";
	String pass = "";
	String server = "";
	String appname = "";

	public RestBase() {
		setService = new SettingsService();
	}

	public void refresh() {
		user = setService.getSetting(SettingsConstants.USERNAME);
		pass = toHex(setService.getSetting(SettingsConstants.PASSWORD));
		server = setService.getSetting(SettingsConstants.SERVER);
		appname = setService.getSetting(SettingsConstants.APPNAME);
	}

	public JSONObject getJson(String view) {
		refresh();
		String restbase = server + "/rest/" + view + "?u=" + user + "&p=enc:" + pass
				+ "&v=1.10.0&c=" + appname + "&f=json";
		return request(restbase);
	}

	public JSONObject getJson(String view, String key, String value) {
		refresh();
		String restbase = server + "/rest/" + view + "?u=" + user + "&p=enc:" + pass
				+ "&v=1.10.0&c=" + appname + "&f=json&" + key + "=" + value;
		return request(restbase);
	}

	public Song download(String songId) {
		// get song meta information
		JSONObject songmeta = getJson("getSong.view", "id", songId);
		songmeta = songmeta.getJSONObject("subsonic-response").getJSONObject("song");
		Song s = new Song(songmeta);
		checkSongCover(s);
		// url for song download
		String restbase = server + "/rest/download.view?u=" + user + "&p=enc:" + pass
				+ "&v=1.10.0&c=" + appname + "&f=json&id=" + songId;
		try {
			URL server = new URL(restbase);
			String absolute = System.getProperty("user.home") + "/Music/Suby/" + s.getPath();
			absolute = absolute.replaceAll("[^a-zA-ZÄäÖöÜüéèà0-9.-/\\ ]", "_");
			File mp = new File(absolute);
			if (mp.exists()) {
				System.out.println("already downloaded");
			} else {
				System.out.println("downloading...");
				FileUtils.copyURLToFile(server, mp);
				System.out.println("done!");
			}
			absolute = "file://" + absolute.replaceAll(" ", "%20");
			s.setPath(absolute);
			return s;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void checkSongCover(Song s) {
		String restbase = server + "/rest/getCoverArt.view?u=" + user + "&p=enc:" + pass
				+ "&v=1.10.0&c=" + appname + "&f=json&id=" + s.getCoverArt() + "&size=100";
		try {
			JSONObject o = request(restbase);
			if (o.getJSONObject("subsonic-response").get("status").equals("failed"))
				return;
		} catch (JSONException e) {
			URL server;
			try {
				server = new URL(restbase);
				String coverPath = System.getProperty("user.home") + "/Music/Suby/"
						+ s.getArtist().replace("/", "_") + "/" + s.getAlbum() + "/al-"
						+ s.getAlbumId() + ".jpg";
				File cover = new File(coverPath);
				if (!cover.exists())
					FileUtils.copyURLToFile(server, cover);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private JSONObject request(String restbase) {
		URL server;
		try {
			server = new URL(restbase);
			URLConnection connection = server.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), Charset.forName("UTF-8")));
			String json = "";
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				json += inputLine;
			in.close();
			json = json.replaceAll("&#244;", "ô");
			json = json.replaceAll("&#242;", "ó");
			json = json.replaceAll("&#246;", "ö");
			json = json.replaceAll("&#776;", "ö");
			json = json.replaceAll("&#252;", "ü");
			json = json.replaceAll("&#228;", "ä");
			json = json.replaceAll("&#196;", "Ä");
			json = json.replaceAll("&#233;", "é");
			json = json.replaceAll("&#229;", "å");
			JSONObject obj = new JSONObject(json);
			return obj;
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new JSONObject();
	}

	public String toHex(String string) {
		return String.format("%040x", new BigInteger(1, string.getBytes(Charset.defaultCharset())));
	}
}
