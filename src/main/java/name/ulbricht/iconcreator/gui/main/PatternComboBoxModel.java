package name.ulbricht.iconcreator.gui.main;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/// A [ComboBoxModel] backed by an immutable list of filename patterns. Used to
/// populate the pattern combo box in [MainView].
final class PatternComboBoxModel extends AbstractListModel<String> implements ComboBoxModel<String> {

    private final List<String> patterns;
    private String selectedPattern;

    /// Creates a new model from the given list of patterns.
    ///
    /// @param patterns the available patterns; must not be `null`
    public PatternComboBoxModel(final List<String> patterns) {
        this.patterns = requireNonNull(patterns);
    }

    @Override
    public int getSize() {
        return this.patterns.size();
    }

    @Override
    public String getElementAt(int index) {
        return this.patterns.get(index);
    }

    @Override
    public void setSelectedItem(final Object anItem) {
        if (anItem instanceof String pattern && !pattern.equals(this.selectedPattern)) {
            this.selectedPattern = pattern;
            fireContentsChanged(this, -1, -1);
        }
    }

    @Override
    public Object getSelectedItem() {
        return this.selectedPattern;
    }
}
