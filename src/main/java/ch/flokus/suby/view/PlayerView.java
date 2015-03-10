package ch.flokus.suby.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.util.Duration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ch.flokus.suby.model.Song;
import ch.flokus.suby.player.Player;
import ch.flokus.suby.player.Playlist;
import ch.flokus.suby.service.AppUtils;

public class PlayerView implements PropertyChangeListener {
    private Playlist playList;
    private Player player;

    private Composite composite;
    private Label currentArtist;
    private Label currentAlbum;
    private Label currentSong;
    private Label currentTime;
    private Label maxTime;
    private ProgressBar currentProgressbar;
    private Label currentAlbumArtContainer;
    private Table playListTable;

    public PlayerView(Composite comp) {
        this.composite = comp;
        playList = new Playlist();
        playList.addChangeListener(this);
        player = new Player(playList);
        player.addChangeListener(this);
    }

    public void getPlayerView() {
        currentArtist = new Label(composite, SWT.BORDER);
        currentArtist.setBounds(280, 10, 200, 20);
        currentAlbum = new Label(composite, SWT.BORDER);
        currentAlbum.setBounds(280, 30, 200, 20);
        currentSong = new Label(composite, SWT.BORDER);
        currentSong.setBounds(280, 50, 220, 20);
        currentTime = new Label(composite, SWT.BORDER);
        currentTime.setBounds(280, 70, 50, 20);
        maxTime = new Label(composite, SWT.BORDER);
        maxTime.setBounds(440, 70, 50, 20);
        currentProgressbar = new ProgressBar(composite, SWT.SMOOTH);
        currentProgressbar.setBounds(280, 80, 200, 30);
        currentProgressbar.setVisible(false);
        currentProgressbar.addMouseListener(new MouseListener() {
            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
                player.setSeek(e.x * 100 / currentProgressbar.getBounds().width);
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });

        currentAlbumArtContainer = new Label(composite, SWT.BORDER);
        currentAlbumArtContainer.setBounds(170, 10, 100, 100);
        Image currentAlbumArt = new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/nocover.png"));
        currentAlbumArtContainer.setImage(currentAlbumArt);

        playListTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
        playListTable.setBounds(500, 10, 450, 100);
        TableColumn plTc1 = new TableColumn(playListTable, SWT.NONE);
        TableColumn plTc2 = new TableColumn(playListTable, SWT.NONE);
        TableColumn plTc3 = new TableColumn(playListTable, SWT.NONE);
        plTc1.setWidth(160);
        plTc2.setWidth(230);
        plTc3.setWidth(40);
        playListTable.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDown(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                player.play(playList.jumpTo(playListTable.getSelectionIndex()).getId());
            }
        });

        Button play = new Button(composite, SWT.PUSH);
        play.setImage(new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/play.png")));
        play.setBounds(30, 10, 50, 50);
        play.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.resume();
            }
        });

        Button pause = new Button(composite, SWT.PUSH);
        pause.setImage(new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/pause.png")));
        pause.setBounds(80, 10, 50, 50);
        pause.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.pause();
            }
        });
        Button previous = new Button(composite, SWT.PUSH);
        previous.setImage(new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/previous.png")));
        previous.setBounds(30, 60, 50, 50);
        previous.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.playPrevious();
            }
        });

        Button next = new Button(composite, SWT.PUSH);
        next.setImage(new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/next.png")));
        next.setBounds(80, 60, 50, 50);
        next.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.playNext();
            }
        });
    }

    public void setDownloading() {
        currentAlbum.setText("Downloading...");
    }

    private void updateCurrentlyPlaying() {
        Song cur = player.getCurrentlyPlaying();
        if (cur != null) {
            currentAlbum.setText(cur.getAlbum());
            currentArtist.setText(cur.getArtist());
            currentSong.setText(cur.getTitle());
            currentTime.setText("00:00");
            currentProgressbar.setVisible(true);
            maxTime.setText(AppUtils.getNiceTime(cur.getDuration()));
            try {
                Image currentAlbumArt = new Image(Display.getCurrent(), cur.getFullCoverArtPath());
                currentAlbumArtContainer.setImage(currentAlbumArt);
            } catch (SWTException e) {
                Image currentAlbumArt = new Image(Display.getCurrent(), Thread.currentThread().getContextClassLoader().getResourceAsStream("img/nocover.png"));
                currentAlbumArtContainer.setImage(currentAlbumArt);
            }
        }
    }

    private void updateTime(Duration newValue) {
        Song cur = player.getCurrentlyPlaying();
        currentTime.setText(AppUtils.getNiceTime(newValue.toSeconds()));
        currentProgressbar.setSelection(AppUtils.getPercentage(cur.getDuration(), newValue.toSeconds()));
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
            Integer currently = Integer.parseInt(evt.getNewValue().toString()) - 1;
            playListTable.setSelection(currently);
            updateCurrentlyPlaying();
        }
        if (evt.getPropertyName().equals("download")) {
            updateCurrentlyPlaying();
        }
        if (evt.getPropertyName().equals("currentTime")) {
            if (evt.getNewValue() instanceof Duration) {
                Duration d = (Duration) evt.getNewValue();
                updateTime(d);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }
}
