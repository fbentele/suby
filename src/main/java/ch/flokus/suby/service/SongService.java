package ch.flokus.suby.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.flokus.suby.model.SongModel;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

public class SongService {
    private static SongService instance;
    Dao<SongModel, String> songDao = null;

    private SongService() {
        try {
            songDao = DaoManager.createDao(DBSource.getConnection(), SongModel.class);
            TableUtils.createTableIfNotExists(DBSource.getConnection(), SongModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SongService getInstance() {
        if (instance == null) {
            instance = new SongService();
        }
        return instance;
    }

    public SongModel getSong(String id) {
        try {
            SongModel s = songDao.queryForId(id);
            return s;
        } catch (SQLException e) {
            System.out.println("SQL Problem:" + e.getMessage());
            return null;
        }
    }

    public void insertSong(SongModel song) {
        try {
            songDao.create(song);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrUpdateSong(SongModel song) {
        try {
            SongModel s = songDao.queryForId(song.getId());
            if (s == null) {
                songDao.create(song);
                System.out.println("Inserted song " + song.getId());
            } else {
                songDao.update(song);
                System.out.println("Updated song " + song.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<SongModel> getAll() {
        try {
            return songDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<SongModel>();
    }
}
