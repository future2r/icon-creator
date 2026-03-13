package name.ulbricht.iconcreator.gui.main;

import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.swing.table.AbstractTableModel;

import name.ulbricht.iconcreator.core.IconSize;
import name.ulbricht.iconcreator.core.Usage;

/// Table model backing the images table in [MainView].
/// Each row corresponds to an [ImageItem] (one per [IconSize]).
/// The model subscribes to item property-change events and fires the appropriate
/// table-changed notifications so the table repaints in real time.
final class ImagesTableModel extends AbstractTableModel {

    /// Identifies the columns of the images table.
    public enum Column {
        /// Check-box column controlling whether a size is included in the output ICO file.
        SELECTED,
        /// Pixel dimension of the icon size (e.g. 32 × 32).
        SIZE,
        /// Recommended usage classification for the icon size.
        USAGE,
        /// Resolved source image file path, or empty if no file was found.
        FILE
    }

    private final List<String> columnNames;
    private final List<ImageItem> items;

    /// Creates a new table model.
    ///
    /// @param resources the resource bundle used to look up localised column header names
    /// @param items    the list of image items to display; must not be `null`
    ImagesTableModel(final ResourceBundle resources, final List<ImageItem> items) {
        this.columnNames = Stream.of(Column.values())
                .map(Enum::name)
                .map(name -> resources.getString("main.images.table." + name.toLowerCase()))
                .toList();

        this.items = requireNonNull(items);

        final PropertyChangeListener itemChangeListener = this::itemPropertyChanged;
        this.items.forEach(item -> item.addPropertyChangeListener(itemChangeListener));
    }

    private void itemPropertyChanged(final PropertyChangeEvent event) {
        final var row = this.items.indexOf(event.getSource());
        switch (event.getPropertyName()) {
            case ImageItem.PROPERTY_SELECTED -> fireTableCellUpdated(row, Column.SELECTED.ordinal());
            case ImageItem.PROPERTY_FILE -> fireTableCellUpdated(row, Column.FILE.ordinal());
        }
    }

    @Override
    public int getRowCount() {
        return this.items.size();
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.size();
    }

    @Override
    public String getColumnName(final int column) {
        return this.columnNames.get(column);
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return switch (Column.values()[columnIndex]) {
            case SELECTED -> Boolean.class;
            case SIZE -> IconSize.class;
            case USAGE -> Usage.class;
            case FILE -> Path.class;
        };
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final var item = this.items.get(rowIndex);
        return switch (Column.values()[columnIndex]) {
            case SELECTED -> item.isSelected();
            case SIZE -> item.size();
            case USAGE -> item.size().usage();
            case FILE -> item.getFile();
        };
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return switch (Column.values()[columnIndex]) {
            case SELECTED -> true;
            default -> false;
        };
    }

    @Override
    public void setValueAt(final Object aValue, int rowIndex, int columnIndex) {
        switch (Column.values()[columnIndex]) {
            case SELECTED -> this.items.get(rowIndex).setSelected((Boolean) aValue);
            default -> throw new IllegalStateException("Column is not editable");
        }
    }
}
