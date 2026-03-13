package name.ulbricht.iconcreator.gui.util;

import java.nio.file.Path;

import javax.swing.table.DefaultTableCellRenderer;

/// A table cell renderer that displays only the file name portion of a
/// [Path]. Falls back to the default string representation for
/// non-[Path] values.
public final class FileTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    protected void setValue(Object value) {
        if (value instanceof Path path) {
            setText(path.getFileName().toString());
        } else {
            super.setValue(value);
        }
    }
}
