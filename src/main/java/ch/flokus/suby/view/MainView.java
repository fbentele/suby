package ch.flokus.suby.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ch.flokus.suby.model.Album;
import ch.flokus.suby.model.Song;
import ch.flokus.suby.player.Player;
import ch.flokus.suby.player.Playlist;
import ch.flokus.suby.rest.Albums;
import ch.flokus.suby.rest.Interprets;
import ch.flokus.suby.rest.RestBase;
import ch.flokus.suby.rest.Songs;
import ch.flokus.suby.settings.AppConstants;

public class MainView implements PropertyChangeListener {
    private RestBase rest = null;
    private Interprets interp = null;
    private Albums alb = null;
    private Songs so = null;
    private Playlist playList = null;
    private Player player = null;

    private Display display = null;
    private Shell shell = null;
    private Table playListTable = null;
    private Table albumTable = null;
    private Label currentArtist = null;
    private Label currentAlbum = null;
    private Label currentSong = null;
    private Label currentAlbumArtContainer = null;
    private List artistList = null;

    private SettingsView sView = null;

    public MainView() {
        rest = RestBase.getInstance();
        so = new Songs();
        alb = new Albums();

        interp = new Interprets();
        playList = new Playlist();
        playList.addChangeListener(this);
        player = new Player(playList);
        player.addChangeListener(this);
    }

    public void getMainView() {
        Display.setAppName("Suby");
        Display.setAppVersion("v0.2");
        display = new Display();

        shell = new Shell(display);
        shell.setText("Suby - " + AppConstants.VERSION);
        shell.setBounds(100, 100, 1000, 520);
        Menu bar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(bar);
        MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
        fileItem.setText("&File");
        Menu submenu = new Menu(shell, SWT.DROP_DOWN);
        fileItem.setMenu(submenu);
        MenuItem item = new MenuItem(submenu, SWT.PUSH);
        item.setText("Connect to Se&rver\tCtrl+R");
        item.setAccelerator(SWT.MOD1 + 'R');

        sView = new SettingsView(shell);
        sView.getSettingsView();
        playListTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        playListTable.setBounds(540, 10, 450, 150);
        TableColumn plTc1 = new TableColumn(playListTable, SWT.NONE);
        TableColumn plTc2 = new TableColumn(playListTable, SWT.NONE);
        TableColumn plTc3 = new TableColumn(playListTable, SWT.NONE);
        plTc1.setWidth(150);
        plTc2.setWidth(190);
        plTc3.setWidth(50);

        // middle section
        albumTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        albumTable.setBounds(330, 180, 350, 300);
        albumTable.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        TableColumn albumCol1 = new TableColumn(albumTable, SWT.NONE);
        TableColumn albumCol2 = new TableColumn(albumTable, SWT.NONE);
        TableColumn albumCol3 = new TableColumn(albumTable, SWT.NONE);
        TableItem titem = new TableItem(albumTable, SWT.NONE);
        titem.setText(new String[] { " ", " ", " " });
        albumCol1.setWidth(90);
        albumCol2.setWidth(200);
        albumCol3.setWidth(40);

        final Table songTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        songTable.setBounds(690, 180, 300, 300);
        TableColumn col1 = new TableColumn(songTable, SWT.NONE);
        TableColumn col2 = new TableColumn(songTable, SWT.NONE);
        TableColumn col3 = new TableColumn(songTable, SWT.NONE);
        TableItem songitem = new TableItem(songTable, SWT.NONE);
        songitem.setText(new String[] { "", "", "" });

        col1.setWidth(0);
        col2.setWidth(240);
        col3.setWidth(40);

        artistList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        artistList.setBounds(10, 180, 300, 300);

        refreshArtistList();
        sView.updateConnectionState();

        artistList.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                int[] selection = artistList.getSelectionIndices();
                if (selection.length == 1) {
                    String interpret = artistList.getItem(selection[0]);
                    Integer id = Integer.parseInt(interpret.subSequence(interpret.indexOf("(") + 1, interpret.indexOf(")")).toString());
                    albumTable.removeAll();
                    songTable.removeAll();
                    for (Album album : alb.getAllForArtist(id)) {
                        TableItem titem = new TableItem(albumTable, SWT.NONE);
                        titem.setText(new String[] { album.getId(), album.getName(), album.getYear() });
                        Image albumArt;
                        try {
                            albumArt = new Image(display, album.getFullCoverArtPath());
                        } catch (SWTException swte) {
                            albumArt = new Image(display, Thread.currentThread().getContextClassLoader().getResourceAsStream("img/nocover.png"));
                        }
                        titem.setImage(0, albumArt);
                    }
                }
            }
        });

        albumTable.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                String album = albumTable.getSelection()[0].getText(0);
                Integer id = Integer.parseInt(album);
                rest.getAlbumCover(alb.getAlbum(album));
                updateAlbumTable();
                songTable.removeAll();
                for (Song song : so.getSongsForAlbum(id)) {
                    TableItem soitem = new TableItem(songTable, SWT.NONE);
                    soitem.setText(new String[] { song.getId(), song.getTitle(), song.getNiceDuration() });
                }
            }
        });

        albumTable.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                String album = albumTable.getSelection()[0].getText(0);
                Integer id = Integer.parseInt(album);
                player.resetPlaylist();
                player.addToPlaylist(so.getSongsForAlbum(id));
                player.startPlaylist();

            }
        });

        songTable.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                String id = songTable.getSelection()[0].getText(0);
                player.resetPlaylist();
                player.play(id);
            }
        });

        Button play = new Button(shell, SWT.PUSH);

        play.setImage(new Image(display, Thread.currentThread().getContextClassLoader().getResourceAsStream("img/play.png")));
        play.setBounds(280, 10, 50, 50);
        play.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.resume();
            }
        });

        Button pause = new Button(shell, SWT.PUSH);
        pause.setImage(new Image(display, Thread.currentThread().getContextClassLoader().getResourceAsStream("img/pause.png")));
        pause.setBounds(330, 10, 50, 50);
        pause.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.pause();
            }
        });
        Button previous = new Button(shell, SWT.PUSH);
        previous.setImage(new Image(display, Thread.currentThread().getContextClassLoader().getResourceAsStream("img/previous.png")));
        previous.setBounds(280, 60, 50, 50);
        previous.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.playPrevious();
            }
        });

        Button next = new Button(shell, SWT.PUSH);
        next.setImage(new Image(display, Thread.currentThread().getContextClassLoader().getResourceAsStream("img/next.png")));
        next.setBounds(330, 60, 50, 50);
        next.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.playNext();
            }
        });

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

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
        System.out.println("cleanup");
    }

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
            updateAlbumTable();
            updateCurrentlyPlaying();
        }
    }

    private void refreshArtistList() {
        for (String i : interp.getAll()) {
            artistList.add(i);
        }
    }

    private void updateAlbumTable() {
        TableItem selected = albumTable.getSelection()[0];
        Album a = alb.getAlbum(selected.getText(0));
        File f = new File(a.getFullCoverArtPath());
        Image albumArt;
        if (f.exists()) {
            albumArt = new Image(display, a.getFullCoverArtPath());
        } else {
            albumArt = new Image(display, Thread.currentThread().getContextClassLoader().getResourceAsStream("img/nocover.png"));
        }
        selected.setImage(0, albumArt);
    }

    private void updateCurrentlyPlaying() {
        Song cur = playList.getCurrent();
        if (cur != null) {
            currentAlbum.setText(cur.getAlbum());
            currentArtist.setText(cur.getArtist());
            currentSong.setText(cur.getTitle());
            try {
                Image currentAlbumArt = new Image(display, cur.getFullCoverArtPath());
                currentAlbumArtContainer.setImage(currentAlbumArt);
            } catch (SWTException e) {
                Image currentAlbumArt = new Image(display, Thread.currentThread().getContextClassLoader().getResourceAsStream("img/nocover.png"));
                currentAlbumArtContainer.setImage(currentAlbumArt);
            }
        }
    }

}
