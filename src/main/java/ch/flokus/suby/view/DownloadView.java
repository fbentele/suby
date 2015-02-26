package ch.flokus.suby.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DownloadView {
    private Composite composite;

    public DownloadView(Composite composite) {
        this.composite = composite;
    }

    public void getDownloadView() {
        Label lDownload = new Label(composite, SWT.BORDER);
        lDownload.setText("Downloading");
        lDownload.setBounds(new Rectangle(10, 10, 75, 20));
    }
}
