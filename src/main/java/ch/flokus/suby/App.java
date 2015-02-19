package ch.flokus.suby;

import ch.flokus.suby.service.DBSource;
import ch.flokus.suby.view.MainView;

import com.j256.ormlite.support.ConnectionSource;

/**
 * App main class.
 * 
 * @author fbentele
 *
 */
public class App {
    public static MainView mainView;

    public static void main(String[] args) throws Exception {
        ConnectionSource connectionSource = DBSource.getConnection();
        mainView = new MainView();
        mainView.getMainView();
        connectionSource.close();
    }
}
