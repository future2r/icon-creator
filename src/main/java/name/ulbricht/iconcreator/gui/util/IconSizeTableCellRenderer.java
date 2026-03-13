package name.ulbricht.iconcreator.gui.util;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.table.DefaultTableCellRenderer;

import name.ulbricht.iconcreator.core.IconSize;

/// A table cell renderer that displays an [IconSize] as a human-readable
/// dimension string (e.g. "32 × 32") using a localised format pattern from the
/// message bundle.
public final class IconSizeTableCellRenderer extends DefaultTableCellRenderer {

    private final Map<IconSize, String> texts;

    /// Creates a new renderer that uses the given message bundle for localisation.
    ///
    /// @param resources the resource bundle
    public IconSizeTableCellRenderer(final ResourceBundle resources) {
        requireNonNull(resources);

        this.texts = Stream.of(IconSize.values())
                .collect(Collectors.toMap(iconSize -> iconSize,
                        iconSize -> resources.getString("icon.size.pattern").formatted(iconSize.size())));
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof IconSize iconSize) {
            setText(this.texts.get(iconSize));
        } else {
            super.setValue(value);
        }
    }
}
