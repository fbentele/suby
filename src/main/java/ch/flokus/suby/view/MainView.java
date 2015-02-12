package ch.flokus.suby.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ch.flokus.suby.model.Album;
import ch.flokus.suby.model.Artist;
import ch.flokus.suby.model.Song;
import ch.flokus.suby.rest.RestAlbums;
import ch.flokus.suby.rest.RestArtists;
import ch.flokus.suby.rest.RestBase;
import ch.flokus.suby.rest.RestSongs;
import ch.flokus.suby.rest.Status;
import ch.flokus.suby.settings.AppConstants;

public class MainView implements PropertyChangeListener {
    private RestBase rest;
    private RestArtists interp;
    private RestAlbums alb;
    private RestSongs so;

    private Display display;
    private Shell shell;
    private Table albumTable;
    private Table artistTable;
    private Table songTable;
    private SettingsView settingsView;
    private PlayerView playerView;

    public MainView() {
        rest = RestBase.getInstance();
        so = new RestSongs();
        alb = new RestAlbums();
        interp = new RestArtists();
        Status.getInstance().addChangeListener(this);
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

        // inject settingsview
        settingsView = new SettingsView(shell);
        settingsView.getSettingsView();

        // inject player
        playerView = new PlayerView(shell);
        playerView.getPlayerView();

        // middle section
        albumTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        albumTable.setBounds(310, 180, 370, 300);
        albumTable.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        TableColumn albumCol1 = new TableColumn(albumTable, SWT.NONE);
        TableColumn albumCol2 = new TableColumn(albumTable, SWT.NONE);
        TableColumn albumCol3 = new TableColumn(albumTable, SWT.NONE);
        albumCol1.setWidth(105);
        albumCol2.setWidth(205);
        albumCol3.setWidth(40);

        songTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        songTable.setBounds(690, 180, 300, 300);
        TableColumn col1 = new TableColumn(songTable, SWT.NONE);
        TableColumn col2 = new TableColumn(songTable, SWT.NONE);
        TableColumn col3 = new TableColumn(songTable, SWT.NONE);
        col1.setWidth(0);
        col2.setWidth(240);
        col3.setWidth(40);

        artistTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        artistTable.setBounds(10, 180, 280, 300);
        TableColumn aCol1 = new TableColumn(artistTable, SWT.NONE);
        TableColumn aCol2 = new TableColumn(artistTable, SWT.NONE);
        aCol1.setWidth(0);
        aCol2.setWidth(260);

        refreshArtistList();
        settingsView.updateConnectionState();

        artistTable.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                int[] selection = artistTable.getSelectionIndices();
                if (selection.length == 1) {
                    String interpret = artistTable.getItem(selection[0]).getText(0);
                    Integer id = Integer.parseInt(interpret);
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
                playerView.getPlayer().resetPlaylist();
                playerView.getPlayer().addToPlaylist(so.getSongsForAlbum(id));
                playerView.getPlayer().startPlaylist();
            }
        });

        songTable.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                String id = songTable.getSelection()[0].getText(0);
                playerView.getPlayer().resetPlaylist();
                playerView.getPlayer().play(id);
            }
        });

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("download")) {
            updateAlbumTable();
        }
        if (evt.getPropertyName().equals("serverState")) {
            refreshArtistList();
        }
    }

    private void refreshArtistList() {
        artistTable.removeAll();
        albumTable.removeAll();
        songTable.removeAll();

        for (Artist artist : interp.getAll()) {
            TableItem item = new TableItem(artistTable, SWT.NONE);
            item.setText(new String[] { artist.getId(), artist.getName() });
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
}
