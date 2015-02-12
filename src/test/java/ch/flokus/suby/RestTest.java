package ch.flokus.suby;

import junit.framework.TestCase;
import ch.flokus.suby.model.Album;
import ch.flokus.suby.model.Artist;
import ch.flokus.suby.model.Song;
import ch.flokus.suby.rest.Albums;
import ch.flokus.suby.rest.Artists;
import ch.flokus.suby.rest.Songs;

public class RestTest extends TestCase {
    public void testInterpret() {
        Artists i = new Artists();
        for (Artist s : i.getAll()) {
            System.out.println(s.getName());
        }
    }

    public void testAlbum() {
        Albums a = new Albums();
        for (Album album : a.getAllForArtist(185)) {
            System.out.println(album.getName() + " (" + album.getYear() + ")");
        }
    }

    public void testSongs() {
        Songs s = new Songs();
        for (Song song : s.getSongsForAlbum(15)) {
            System.out.println(song.getTitle());
        }
    }
}
