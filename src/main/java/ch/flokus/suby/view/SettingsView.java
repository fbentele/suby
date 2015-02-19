package ch.flokus.suby.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ch.flokus.suby.rest.RestBase;
import ch.flokus.suby.rest.Status;
import ch.flokus.suby.service.SettingsService;
import ch.flokus.suby.settings.AppConstants;
import ch.flokus.suby.settings.SettingsConstants;

public class SettingsView implements PropertyChangeListener {
	private File versionFile;
	private SettingsService setService;
	private RestBase rest;
	private Status status;
	private Composite composite;

	public SettingsView(Composite composite) {
		this.composite = composite;
		status = Status.getInstance();
		status.addChangeListener(this);
		rest = RestBase.getInstance();
		setService = new SettingsService();
		versionFile = new File(System.getProperty("user.home") + "/.suby/autoupdate.ini");
	}

	public void getSettingsView() {
		Label lServer = new Label(composite, SWT.BORDER);
		lServer.setText("Server:");
		lServer.setBounds(new Rectangle(10, 10, 75, 20));

		Label lAppname = new Label(composite, SWT.BORDER);
		lAppname.setText("Appname:");
		lAppname.setBounds(10, 40, 75, 20);

		Label lUsername = new Label(composite, SWT.BORDER);
		lUsername.setText("Username:");
		lUsername.setBounds(280, 10, 75, 20);

		Label lPassword = new Label(composite, SWT.BORDER);
		lPassword.setText("Password:");
		lPassword.setBounds(280, 40, 75, 20);

		final Text server = new Text(composite, SWT.NONE);
		server.setText(setService.getSetting(SettingsConstants.SERVER));
		server.setBounds(100, 10, 140, 20);

		final Text appname = new Text(composite, SWT.NONE);
		appname.setText(setService.getSetting(SettingsConstants.APPNAME));
		appname.setBounds(100, 40, 140, 20);

		final Text username = new Text(composite, SWT.NONE);
		username.setText(setService.getSetting(SettingsConstants.USERNAME));
		username.setBounds(360, 10, 140, 20);

		final Text pass = new Text(composite, SWT.NONE);
		pass.setText(setService.getSetting(SettingsConstants.PASSWORD));
		pass.setBounds(360, 40, 140, 20);
		pass.setEchoChar('*');

		final Button autoUpdate = new Button(composite, SWT.CHECK);
		autoUpdate.setBounds(100, 70, 140, 20);
		autoUpdate.setText("Auto Update");

		if (versionFile.exists()) {
			autoUpdate.setSelection(true);
		}

		autoUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (autoUpdate.getSelection()) {
					try {
						FileUtils.writeStringToFile(versionFile, AppConstants.VERSION, "UTF-8",
								false);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					FileUtils.deleteQuietly(versionFile);
				}
			}
		});

		Button save = new Button(composite, SWT.PUSH);
		save.setText("Save Settings");
		save.setBounds(400, 70, 120, 30);
		save.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setService.isertOrUpdateSetting(SettingsConstants.USERNAME, username.getText());
				setService.isertOrUpdateSetting(SettingsConstants.PASSWORD, pass.getText());
				setService.isertOrUpdateSetting(SettingsConstants.SERVER, server.getText());
				setService.isertOrUpdateSetting(SettingsConstants.APPNAME, appname.getText());
				rest.refresh();
			}
		});

		Button connect = new Button(composite, SWT.PUSH);
		connect.setText("Connect");
		connect.setBounds(270, 70, 120, 30);
		connect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				status.testConnection();
				updateConnectionState();
			}
		});
	}

	public void updateConnectionState() {
		Canvas canvas = new Canvas(composite, SWT.NONE);
		canvas.setBounds(250, 10, 15, 15);
		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				switch (status.getState()) {
				case ERROR:
					event.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					break;
				case OK:
					event.gc.setBackground(Display.getCurrent()
							.getSystemColor(SWT.COLOR_DARK_GREEN));
					break;
				default:
					event.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
					break;
				}
				event.gc.fillOval(0, 0, 12, 12);
			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("serverState")) {
			updateConnectionState();
		}
	}
}
