package ch.flokus.suby;

import ch.flokus.suby.model.Album;
import ch.flokus.suby.model.Song;
import ch.flokus.suby.rest.Albums;
import ch.flokus.suby.rest.Interprets;
import ch.flokus.suby.rest.Songs;
import junit.framework.TestCase;

public class RestTest extends TestCase {
    public void testInterpret() {
        Interprets i = new Interprets();
        for (String s : i.getAll()) {
            System.out.println(s);
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
