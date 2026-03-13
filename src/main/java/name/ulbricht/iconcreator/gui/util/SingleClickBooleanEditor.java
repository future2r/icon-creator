package name.ulbricht.iconcreator.gui.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

/// A table cell editor for [Boolean] values that commits the toggle on a single
/// mouse click or keyboard space press, without requiring a double-click. The
/// editor renders a centred, non-opaque [JCheckBox].
public final class SingleClickBooleanEditor extends AbstractCellEditor implements TableCellEditor {

    private final JCheckBox checkBox = new JCheckBox();

    /// Creates a new editor and installs the single-click commit behaviour.
    public SingleClickBooleanEditor() {
        this.checkBox.setHorizontalAlignment(SwingConstants.CENTER);

        // Toggle immediately on press and commit
        this.checkBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                SingleClickBooleanEditor.this.checkBox
                        .setSelected(!SingleClickBooleanEditor.this.checkBox.isSelected());
                // Commit immediately; JTable will call setValueAt(newValue)
                fireEditingStopped();
            }
        });

        // Also commit when toggled via keyboard (space)
        this.checkBox.addActionListener(e -> fireEditingStopped());

        // Optional, but reduces focus funkiness
        this.checkBox.setFocusPainted(false);
        this.checkBox.setOpaque(false);
    }

    @Override
    public boolean isCellEditable(final EventObject e) {
        // Always editable; no double-click requirement
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return this.checkBox.isSelected();
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
            final int row, final int column) {
        this.checkBox.setSelected(Boolean.TRUE.equals(value));
        return this.checkBox;
    }
}
