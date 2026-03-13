package name.ulbricht.iconcreator.gui.util;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.table.DefaultTableCellRenderer;

import name.ulbricht.iconcreator.core.Usage;

/// A table cell renderer that displays a [Usage] enum constant as a
/// localised string.
public final class UsageTableCellRenderer extends DefaultTableCellRenderer {

    private final Map<Usage, String> texts;

    /// Creates a new renderer that uses the given message bundle for localisation.
    ///
    /// @param resources the resource bundle
    public UsageTableCellRenderer(final ResourceBundle resources) {
        requireNonNull(resources);

        this.texts = Stream.of(Usage.values())
                .collect(Collectors.toMap(usage -> usage,
                        usage -> resources.getString("usage." + usage.name().toLowerCase())));
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof Usage usage) {
            setText(this.texts.get(usage));
        } else {
            super.setValue(value);
        }
    }
}
