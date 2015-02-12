package ch.flokus.suby;

import junit.framework.TestCase;
import ch.flokus.suby.model.Album;
import ch.flokus.suby.model.Artist;
import ch.flokus.suby.model.Song;
import ch.flokus.suby.rest.RestAlbums;
import ch.flokus.suby.rest.RestArtists;
import ch.flokus.suby.rest.RestSongs;

public class RestTest extends TestCase {
    public void testInterpret() {
        RestArtists i = new RestArtists();
        for (Artist s : i.getAll()) {
            System.out.println(s.getName());
        }
    }

    public void testAlbum() {
        RestAlbums a = new RestAlbums();
        for (Album album : a.getAllForArtist(185)) {
            System.out.println(album.getName() + " (" + album.getYear() + ")");
        }
    }

    public void testSongs() {
        RestSongs s = new RestSongs();
        for (Song song : s.getSongsForAlbum(15)) {
            System.out.println(song.getTitle());
        }
    }
}
