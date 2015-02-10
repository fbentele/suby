package ch.flokus.suby;

import ch.flokus.suby.service.DBSource;
import ch.flokus.suby.view.MainView;

import com.j256.ormlite.support.ConnectionSource;

/**
 * App main class.
 * @author fbentele
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        ConnectionSource connectionSource = DBSource.getConnection();
        MainView w = new MainView();
        w.getMainView();
        connectionSource.close();
    }
}
