package name.ulbricht.iconcreator.gui.main;

import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;

import name.ulbricht.iconcreator.core.IconSize;
import name.ulbricht.iconcreator.core.Usage;

/// Represents a single row in the images table, combining an [IconSize] with an
/// optional resolved file path and a user-controlled selection flag. Changes to
/// the selection flag and the file path are broadcast
/// via [PropertyChangeSupport].
final class ImageItem {

    /// Property name fired when [#getFile()] changes.
    public static final String PROPERTY_FILE = "file";

    /// Property name fired when [#isSelected()] changes.
    public static final String PROPERTY_SELECTED = "selected";

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private final IconSize size;
    private boolean selected;
    private Path file;

    /// Creates a new item for the given icon size.
    ///
    /// The selection flag is pre-set to `true` for [Usage#REQUIRED] and
    /// [Usage#RECOMMENDED] sizes.
    ///
    /// @param size the icon size this item represents
    ImageItem(final IconSize size) {
        this.size = requireNonNull(size);
        this.selected = size.usage() == Usage.REQUIRED || size.usage() == Usage.RECOMMENDED;
    }

    /// Registers a listener that will be notified of property changes.
    ///
    /// @param listener the listener to add
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /// Removes a previously registered property-change listener.
    ///
    /// @param listener the listener to remove
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /// {@return the icon size this item represents}
    public IconSize size() {
        return this.size;
    }

    /// {@return `true` if this size is selected for inclusion in the output
    /// ICO file}
    public boolean isSelected() {
        return this.selected;
    }

    /// Sets the selection flag and fires a {@value #PROPERTY_SELECTED}
    /// property-change event.
    ///
    /// @param selected `true` to include this size in the output ICO file
    public void setSelected(final boolean selected) {
        final var oldValue = this.selected;
        this.selected = selected;
        this.propertyChangeSupport.firePropertyChange(PROPERTY_SELECTED, oldValue, selected);
    }

    /// {@return the resolved image file path, or `null` if no file has been
    /// found yet}
    public Path getFile() {
        return this.file;
    }

    /// Sets the resolved file path and fires a {@value #PROPERTY_FILE}
    /// property-change event.
    ///
    /// @param file the file path, or `null` to clear
    public void setFile(final Path file) {
        final var oldValue = this.file;
        this.file = file;
        this.propertyChangeSupport.firePropertyChange(PROPERTY_FILE, oldValue, file);
    }
}
