package ch.flokus.suby.rest;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class AsyncDownloader {

    public AsyncDownloader(URL url, File dest) {
        ThreadedDownloader t = new ThreadedDownloader(url, dest);
        new Thread(t).start();
    }

    private static class ThreadedDownloader implements Runnable {
        private final URL url;
        private final File destination;

        public ThreadedDownloader(final URL url, final File dest) {
            this.url = url;
            this.destination = dest;
        }

        public void run() {
            try {
                FileUtils.copyURLToFile(url, destination);
            } catch (IOException e) {
                System.out.println("Got Error from server: " + url);
                System.out.println(e.getMessage());
            }
        }
    }
}
