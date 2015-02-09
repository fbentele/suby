package ch.flokus.suby.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
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
import org.eclipse.swt.widgets.Text;

import ch.flokus.suby.App;
import ch.flokus.suby.model.Album;
import ch.flokus.suby.model.Song;
import ch.flokus.suby.player.Player;
import ch.flokus.suby.player.Playlist;
import ch.flokus.suby.rest.Albums;
import ch.flokus.suby.rest.Interprets;
import ch.flokus.suby.rest.Songs;
import ch.flokus.suby.rest.Status;
import ch.flokus.suby.service.SettingsService;
import ch.flokus.suby.settings.AppConstants;
import ch.flokus.suby.settings.SettingsConstants;

public class MainView implements PropertyChangeListener {
    private SettingsService setService = null;
    private Interprets interp = null;
    private Albums alb = null;
    private Songs so = null;
    private Status status = null;
    private Playlist playList = null;
    private Player player = null;

    private Display display = null;
    private Shell shell = null;
    private Table playListTable = null;
    private Table albumTable = null;

    public MainView() {
        so = new Songs();
        alb = new Albums();
        status = new Status();
        setService = new SettingsService();
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
        shell.addKeyListener(new KeyListener() {
            
            @Override
            public void keyReleased(KeyEvent e) {
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e.keyCode);
            }
        });
        
        Menu bar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(bar);
        MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
        fileItem.setText("&File");
        Menu submenu = new Menu(shell, SWT.DROP_DOWN);
        fileItem.setMenu(submenu);
        MenuItem item = new MenuItem(submenu, SWT.PUSH);
        item.setText("Connect to Se&rver\tCtrl+R");
        item.setAccelerator(SWT.MOD1 + 'R');

        Label lServer = new Label(shell, SWT.BORDER);
        lServer.setText("Server:");
        lServer.setBounds(new Rectangle(10, 20, 75, 20));
        Label lUsername = new Label(shell, SWT.BORDER);
        lUsername.setText("Username:");
        lUsername.setBounds(10, 50, 75, 20);
        Label lPassword = new Label(shell, SWT.BORDER);
        lPassword.setText("Password:");
        lPassword.setBounds(10, 80, 75, 20);
        Label lAppname = new Label(shell, SWT.BORDER);
        lAppname.setText("Appname:");
        lAppname.setBounds(10, 110, 75, 20);
        final Text server = new Text(shell, SWT.NONE);
        server.setText(setService.getSetting(SettingsConstants.SERVER));
        server.setBounds(100, 20, 140, 20);
        final Text username = new Text(shell, SWT.NONE);
        username.setText(setService.getSetting(SettingsConstants.USERNAME));
        username.setBounds(100, 50, 140, 20);
        final Text pass = new Text(shell, SWT.NONE);
        pass.setText(setService.getSetting(SettingsConstants.PASSWORD));
        pass.setBounds(100, 80, 140, 20);
        pass.setEchoChar('*');
        final Text appname = new Text(shell, SWT.NONE);
        appname.setText(setService.getSetting(SettingsConstants.APPNAME));
        appname.setBounds(100, 110, 140, 20);

        playListTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        playListTable.setBounds(540, 20, 450, 150);
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
        albumCol1.setWidth(100);
        albumCol2.setWidth(210);
        albumCol3.setWidth(40);

        final Table songTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        songTable.setBounds(690, 180, 300, 300);
        TableColumn col1 = new TableColumn(songTable, SWT.NONE);
        TableColumn col2 = new TableColumn(songTable, SWT.NONE);
        TableColumn col3 = new TableColumn(songTable, SWT.NONE);
        TableItem songitem = new TableItem(songTable, SWT.NONE);
        songitem.setText(new String[] { "", "", "" });

        col1.setWidth(60);
        col2.setWidth(190);
        col3.setWidth(40);

        final List interpretList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        interpretList.setBounds(10, 180, 300, 300);
        for (String i : interp.getAll()) {
            interpretList.add(i);
        }

        interpretList.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                int[] selection = interpretList.getSelectionIndices();
                if (selection.length == 1) {
                    String interpret = interpretList.getItem(selection[0]);
                    Integer id = Integer.parseInt(interpret.subSequence(interpret.indexOf("(") + 1, interpret.indexOf(")")).toString());
                    albumTable.removeAll();
                    songTable.removeAll();
                    for (Album album : alb.getAllForArtist(id)) {
                        TableItem titem = new TableItem(albumTable, SWT.NONE);
                        titem.setText(new String[] { album.getId(), album.getName(), album.getYear() });
                        File f = new File(album.getFullCoverArtPath());
                        Image albumArt;
                        if (f.exists()) {
                            albumArt = new Image(display, album.getFullCoverArtPath());
                        } else {
                            albumArt = new Image(display, App.class.getClassLoader().getResourceAsStream("img/nocover.png"));
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
                songTable.removeAll();
                for (Song song : so.getSongsForAlbum(id)) {
                    TableItem soitem = new TableItem(songTable, SWT.NONE);
                    soitem.setText(new String[] { song.getId(), song.getTitle(), "" });
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

        final Canvas canvas = new Canvas(shell, SWT.NONE);
        canvas.setBounds(250, 20, 15, 15);
        canvas.addListener(SWT.Paint, new Listener() {
            public void handleEvent(Event event) {
                event.gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
                event.gc.fillOval(0, 0, 12, 12);
            }
        });

        Button play = new Button(shell, SWT.PUSH);
        play.setImage(new Image(display, App.class.getClassLoader().getResourceAsStream("img/play.png")));
        play.setBounds(300, 20, 50, 50);
        play.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.resume();
            }
        });

        Button pause = new Button(shell, SWT.PUSH);
        pause.setImage(new Image(display, App.class.getClassLoader().getResourceAsStream("img/pause.png")));
        pause.setBounds(350, 20, 50, 50);
        pause.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.pause();
            }
        });
        Button previous = new Button(shell, SWT.PUSH);
        previous.setImage(new Image(display, App.class.getClassLoader().getResourceAsStream("img/previous.png")));
        previous.setBounds(300, 70, 50, 50);
        previous.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.playPrevious();
            }
        });

        Button next = new Button(shell, SWT.PUSH);
        next.setImage(new Image(display, App.class.getClassLoader().getResourceAsStream("img/next.png")));
        next.setBounds(350, 70, 50, 50);
        next.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.playNext();
            }
        });

        Button save = new Button(shell, SWT.PUSH);
        save.setText("Save Settings");
        save.setBounds(5, 140, 120, 30);
        save.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setService.isertOrUpdateSetting(SettingsConstants.USERNAME, username.getText());
                setService.isertOrUpdateSetting(SettingsConstants.PASSWORD, pass.getText());
                setService.isertOrUpdateSetting(SettingsConstants.SERVER, server.getText());
                setService.isertOrUpdateSetting(SettingsConstants.APPNAME, appname.getText());
            }
        });

        Button connect = new Button(shell, SWT.PUSH);
        connect.setText("Test Connection");
        connect.setBounds(125, 140, 130, 30);
        connect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (status.testConnection().equals("ok")) {
                    GC tempgc = new GC(canvas);
                    tempgc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
                    tempgc.fillOval(0, 0, 12, 12);
                } else {
                    GC tempgc = new GC(canvas);
                    tempgc.setBackground(display.getSystemColor(SWT.COLOR_RED));
                    tempgc.fillOval(0, 0, 12, 12);
                }
            }
        });

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
        }
        if (evt.getPropertyName().equals("download")) {
            TableItem selected = albumTable.getSelection()[0];
            Album a = alb.getAlbum(selected.getText(0));
            File f = new File(a.getFullCoverArtPath());
            Image albumArt;
            if (f.exists()) {
                albumArt = new Image(display, a.getFullCoverArtPath());
            } else {
                albumArt = new Image(display, App.class.getClassLoader().getResourceAsStream("img/nocover.png"));
            }
            selected.setImage(0, albumArt);
        }
    }

}
