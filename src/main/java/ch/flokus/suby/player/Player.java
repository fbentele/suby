package ch.flokus.suby.player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import ch.flokus.suby.model.SongModel;
import ch.flokus.suby.rest.RestBase;
import ch.flokus.suby.service.AppUtils;

public class Player {
    private Media current = null;
    private SongModel currentSong = null;
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
        rest = RestBase.getInstance();
    }

    public void play(String id) {
        currentSong = rest.download(id, false);

        notifyListeners(this, "download", "", currentSong.getCoverArt());
        if (mediaPlayer != null)
            mediaPlayer.stop();
        try {
            current = new Media(currentSong.getPath());
            mediaPlayer = new MediaPlayer(current);

            // preload next song
            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    if (playList.hasNext()) {
                        rest.download(playList.getNext(false).getId(), true);
                    }
                }
            });

            // play next song
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    playNext();
                }
            });

            // error case
            mediaPlayer.setOnError(new Runnable() {
                public void run() {
                    playNext();
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    notifyListeners(this, "currentTime", oldValue, newValue);
                }
            });

            mediaPlayer.play();
        } catch (MediaException e) {
            playNext();
        }
    }

    public void playNext() {
        SongModel next = playList.getNext(true);
        if (next != null && next.getId() != null) {
            play(next.getId());
        } else {
            if (mediaPlayer != null)
                mediaPlayer.stop();
        }
    }

    public void playPrevious() {
        SongModel previousSong = playList.getPrevious(true);
        if (previousSong != null && previousSong.getId() != null) {
            play(previousSong.getId());
        } else {
            mediaPlayer.stop();
        }
    }

    public void pause() {
        notifyListeners(this, "playState", String.valueOf(Status.PLAYING), String.valueOf(Status.PAUSED));
        mediaPlayer.pause();
    }

    public void resume() {
        if (mediaPlayer.getStatus().equals(Status.PAUSED)) {
            mediaPlayer.play();
        }
        notifyListeners(this, "playState", String.valueOf(Status.PAUSED), String.valueOf(Status.PLAYING));
    }

    public void setSeek(int percent) {
        int i = Integer.valueOf(currentSong.getDuration()) * percent * 10;
        mediaPlayer.seek(new Duration(i));
    }

    public void addToPlaylist(List<SongModel> list) {
        playList.appendAll(list);
    }

    public void startPlaylist() {
        if (playList.getAll().size() > 0) {
            this.play(playList.getNext(true).getId());
        }
    }

    public void resetPlaylist() {
        playList.reset();
    }

    private void notifyListeners(Object object, String property, Object oldValue, Object newValue) {
        for (PropertyChangeListener name : listener) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }

    public SongModel getCurrentlyPlaying() {
        return currentSong;
    }

    public String getCurrentTime() {
        String ret = "";
        if (mediaPlayer != null) {
            ret = AppUtils.getNiceTime(String.valueOf(mediaPlayer.getCurrentTime().toSeconds()));
        }
        return ret;
    }
}
