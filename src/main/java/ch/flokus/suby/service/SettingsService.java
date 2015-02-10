package ch.flokus.suby.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.flokus.suby.model.Settings;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

public class SettingsService {

	Dao<Settings, String> settingsDao = null;

	public SettingsService() {
		try {
			settingsDao = DaoManager.createDao(DBSource.getConnection(), Settings.class);
			TableUtils.createTableIfNotExists(DBSource.getConnection(), Settings.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSetting(String key) {
		String ret = "";
		try {
			Settings s = settingsDao.queryForId(key);
			if (s != null) {
				ret = s.getValue();
			}
		} catch (SQLException e) {
		}
		return ret;
	}

	public void insertSetting(String key, String value) {
		Settings s = new Settings();
		s.setKey(key);
		s.setValue(value);
		try {
			settingsDao.create(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void isertOrUpdateSetting(String key, String value) {
		// special treatment when server url is changed
		if (key.equals("server") && !value.startsWith("http")) {
			value = "http://" + value;
		}

		try {
			Settings s = settingsDao.queryForId(key);
			if (s == null) {
				s = new Settings();
				s.setKey(key);
				s.setValue(value);
				settingsDao.create(s);
			} else {
				s.setValue(value);
				settingsDao.update(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Settings> getAll() {
		try {
			return settingsDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<Settings>();
	}
}
