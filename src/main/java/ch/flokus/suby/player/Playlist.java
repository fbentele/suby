package ch.flokus.suby.player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import ch.flokus.suby.model.SongModel;

public class Playlist {
    private List<SongModel> thePlaylist = null;
    private Integer currentPosition = 0;
    private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

    public Playlist() {
        thePlaylist = new ArrayList<SongModel>();
    }

    public SongModel getNext(boolean update) {
        if (thePlaylist.size() > currentPosition) {
            SongModel ret = thePlaylist.get(currentPosition);
            if (update)
                currentPosition++;
            notifyListeners(this, "currentPosition", String.valueOf(currentPosition), String.valueOf(currentPosition));
            return ret;
        }
        return thePlaylist.get(thePlaylist.size() - 1);
    }

    public SongModel getPrevious(boolean update) {
        if (thePlaylist.size() >= currentPosition && currentPosition > 1) {
            if (update)
                currentPosition--;
            notifyListeners(this, "currentPosition", String.valueOf(currentPosition + 1), String.valueOf(currentPosition));
            return thePlaylist.get(currentPosition - 1);
        }
        return thePlaylist.get(0);
    }

    public SongModel jumpTo(int index) {
        if (index >= 0 && index < thePlaylist.size()) {
            currentPosition = index;
            notifyListeners(this, "currentPosition", String.valueOf(currentPosition), String.valueOf(currentPosition + 1));
            return thePlaylist.get(currentPosition++);
        }
        return null;
    }

    public void append(SongModel songid) {
        thePlaylist.add(songid);
    }

    public void appendAll(List<SongModel> list) {
        thePlaylist.addAll(list);
    }

    public List<SongModel> getAll() {
        return thePlaylist;
    }

    public void reset() {
        thePlaylist = new ArrayList<SongModel>();
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

    public SongModel getCurrent() {
        if (thePlaylist.size() > currentPosition && currentPosition > 0) {
            return thePlaylist.get(currentPosition);
        }
        return null;
    }

    public boolean hasNext() {
        return thePlaylist.size() > currentPosition;
    }

}
