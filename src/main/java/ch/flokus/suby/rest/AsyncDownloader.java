package ch.flokus.suby.rest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class AsyncDownloader {

    public AsyncDownloader(URL url, File dest, PropertyChangeListener callback) {
        ThreadedDownloader t = new ThreadedDownloader(url, dest, callback);
        new Thread(t).start();
    }

    private static class ThreadedDownloader implements Runnable {
        private final PropertyChangeListener caller;
        private final URL url;
        private final File destination;

        public ThreadedDownloader(final URL url, final File dest, final PropertyChangeListener callback) {
            this.url = url;
            this.destination = dest;
            this.caller = callback;
        }

        public void run() {
            try {
                FileUtils.copyURLToFile(url, destination);
            } catch (IOException e) {
                System.out.println("Got Error from server: " + url);
                System.out.println(e.getMessage());
            } finally {
                caller.propertyChange(new PropertyChangeEvent(this, "AsyncDownloader", "running", "ended"));
            }
        }
    }
}
