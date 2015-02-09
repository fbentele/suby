package ch.flokus.suby.player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import ch.flokus.suby.model.Song;
import ch.flokus.suby.rest.RestBase;

public class Player {
	private Media current = null;
	private MediaPlayer mediaPlayer = null;
	private Playlist playList = null;
	private RestBase rest = null;
	private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

	@SuppressWarnings("unused")
	private Player() {
	}

	public Player(Playlist playList) {
		// don't even ask... (init javafx mediaplayer)
		JFXPanel fxPanel = new JFXPanel();
		fxPanel.isEnabled();
		this.playList = playList;
		rest = new RestBase();
	}

	public void play(String id) {
		Song song = rest.download(id);

		notifyListeners(this, "download", "", song.getCoverArt());
		if (mediaPlayer != null)
			mediaPlayer.stop();

		current = new Media(song.getPath());
		mediaPlayer = new MediaPlayer(current);

		// preload next song
		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				rest.download(playList.getNext().getId());
				playList.getPrevious();
			}
		});

		// play next song
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			public void run() {
				playNext();
			}
		});
		mediaPlayer.play();
	}

	public void playNext() {
		Song next = playList.getNext();
		if (next != null && next.getId() != null) {
			play(next.getId());
		} else {
			mediaPlayer.stop();
		}
	}

	public void playPrevious() {
		Song prev = playList.getPrevious();
		if (prev != null && prev.getId() != null) {
			play(prev.getId());
		} else {
			mediaPlayer.stop();
		}
	}

	public void pause() {
		notifyListeners(this, "playState", String.valueOf(Status.PLAYING),
				String.valueOf(Status.PAUSED));
		mediaPlayer.pause();
	}

	public void resume() {
		if (mediaPlayer.getStatus().equals(Status.PAUSED)) {
			mediaPlayer.play();
		}
		notifyListeners(this, "playState", String.valueOf(Status.PAUSED),
				String.valueOf(Status.PLAYING));
	}

	public void addToPlaylist(List<Song> list) {
		playList.appendAll(list);
	}

	public void startPlaylist() {
		if (playList.getAll().size() > 0) {
			this.play(playList.getNext().getId());
		}
	}

	public void resetPlaylist() {
		playList.reset();
	}

	private void notifyListeners(Object object, String property, String oldValue, String newValue) {
		for (PropertyChangeListener name : listener) {
			name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
		}
	}

	public void addChangeListener(PropertyChangeListener newListener) {
		listener.add(newListener);
	}

}
