package ch.flokus.suby.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class DownloadView {
    private Composite composite;
    private Table downloadTable;

    public DownloadView(Composite composite) {
        this.composite = composite;
        downloadTable = new Table(composite, SWT.NONE);
    }

    public void getDownloadView() {
        Label lDownload = new Label(composite, SWT.BORDER);
        lDownload.setText("Downloading:");
        lDownload.setBounds(new Rectangle(10, 10, 75, 20));

        downloadTable.setBounds(100, 10, 500, 250);
        TableColumn dlTc1 = new TableColumn(downloadTable, SWT.NONE);
        TableColumn dlTc2 = new TableColumn(downloadTable, SWT.NONE);
        TableColumn dlTc3 = new TableColumn(downloadTable, SWT.NONE);
        dlTc1.setWidth(200);
        dlTc2.setWidth(200);
        dlTc3.setWidth(200);
        TableItem dlTi1 = new TableItem(downloadTable, SWT.NONE);
        dlTi1.setText(0, "currently not downloading anything");
    }
}
