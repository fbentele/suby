package ch.flokus.suby.player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import ch.flokus.suby.model.Song;

public class Playlist {
	private List<Song> thePlaylist = null;
	private Integer currentPosition = 0;
	private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

	public Playlist() {
		thePlaylist = new ArrayList<Song>();
	}

	public Song getNext() {
		if (thePlaylist.size() > currentPosition) {
			Song ret = thePlaylist.get(currentPosition++);
			notifyListeners(this, "currentPosition", String.valueOf(currentPosition - 1),
					String.valueOf(currentPosition));
			return ret;
		}
		return null;
	}

	public Song getPrevious() {
		if (thePlaylist.size() > currentPosition && currentPosition > 1) {
			currentPosition--;
			notifyListeners(this, "currentPosition", String.valueOf(currentPosition + 1),
					String.valueOf(currentPosition));
			return thePlaylist.get(currentPosition - 1);
		}
		return thePlaylist.get(0);
	}

	public void append(Song songid) {
		thePlaylist.add(songid);
	}

	public void appendAll(List<Song> list) {
		thePlaylist.addAll(list);
	}

	public List<Song> getAll() {
		return thePlaylist;
	}

	public void reset() {
		thePlaylist = new ArrayList<Song>();
		currentPosition = 0;
	}

	private void notifyListeners(Object object, String property, String oldValue, String newValue) {
		for (PropertyChangeListener name : listener) {
			name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
		}
	}

	public void addChangeListener(PropertyChangeListener newListener) {
		listener.add(newListener);
	}

	public Song getCurrent() {
		if (thePlaylist.size() > currentPosition) {
			return thePlaylist.get(currentPosition - 1);
		}
		return null;
	}

}
