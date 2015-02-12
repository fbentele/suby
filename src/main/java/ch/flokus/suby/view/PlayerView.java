package ch.flokus.suby.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ch.flokus.suby.model.Song;
import ch.flokus.suby.player.Player;
import ch.flokus.suby.player.Playlist;

public class PlayerView implements PropertyChangeListener {

    private Playlist playList = null;
    private Player player = null;

    private Shell shell = null;
    private Label currentArtist = null;
    private Label currentAlbum = null;
    private Label currentSong = null;
    private Label currentAlbumArtContainer = null;
    private Table playListTable = null;

    public PlayerView(Shell shell) {
        this.shell = shell;
        playList = new Playlist();
        playList.addChangeListener(this);

        player = new Player(playList);
        player.addChangeListener(this);
    }

    public void getPlayerView() {
        currentArtist = new Label(shell, SWT.BORDER);
        currentArtist.setText("");
        currentArtist.setBounds(390, 10, 130, 20);
        currentAlbum = new Label(shell, SWT.BORDER);
        currentAlbum.setBounds(390, 30, 130, 20);
        currentAlbum.setText("");
        currentSong = new Label(shell, SWT.BORDER);
        currentSong.setBounds(390, 50, 130, 20);
        currentSong.setText("");
        currentAlbumArtContainer = new Label(shell, SWT.BORDER);
        currentAlbumArtContainer.setBounds(390, 70, 100, 100);

        playListTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        playListTable.setBounds(540, 10, 450, 150);
        TableColumn plTc1 = new TableColumn(playListTable, SWT.NONE);
        TableColumn plTc2 = new TableColumn(playListTable, SWT.NONE);
        TableColumn plTc3 = new TableColumn(playListTable, SWT.NONE);
        plTc1.setWidth(160);
        plTc2.setWidth(230);
        plTc3.setWidth(40);
        Button play = new Button(shell, SWT.PUSH);

        play.setImage(new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/play.png")));
        play.setBounds(280, 10, 50, 50);
        play.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.resume();
            }
        });

        Button pause = new Button(shell, SWT.PUSH);
        pause.setImage(new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/pause.png")));
        pause.setBounds(330, 10, 50, 50);
        pause.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.pause();
            }
        });
        Button previous = new Button(shell, SWT.PUSH);
        previous.setImage(new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/previous.png")));
        previous.setBounds(280, 60, 50, 50);
        previous.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.playPrevious();
            }
        });

        Button next = new Button(shell, SWT.PUSH);
        next.setImage(new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/next.png")));
        next.setBounds(330, 60, 50, 50);
        next.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.playNext();
            }
        });
    }

    private void updateCurrentlyPlaying() {
        Song cur = playList.getCurrent();
        if (cur != null) {
            currentAlbum.setText(cur.getAlbum());
            currentArtist.setText(cur.getArtist());
            currentSong.setText(cur.getTitle());
            try {
                Image currentAlbumArt = new Image(Display.getCurrent(), cur.getFullCoverArtPath());
                currentAlbumArtContainer.setImage(currentAlbumArt);
            } catch (SWTException e) {
                Image currentAlbumArt = new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/nocover.png"));
                currentAlbumArtContainer.setImage(currentAlbumArt);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("currentPosition")) {
            playListTable.removeAll();
            for (Song song : playList.getAll()) {
                TableItem plTi1 = new TableItem(playListTable, SWT.NONE);
                plTi1.setText(0, song.getArtist());
                plTi1.setText(1, song.getTitle());
                plTi1.setText(2, song.getNiceDuration());
            }
            playListTable.setSelection(Integer.valueOf((String) evt.getNewValue()) - 1);
            updateCurrentlyPlaying();
        }
        if (evt.getPropertyName().equals("download")) {
            updateCurrentlyPlaying();
        }
    }

    public Player getPlayer() {
        return player;
    }
}
