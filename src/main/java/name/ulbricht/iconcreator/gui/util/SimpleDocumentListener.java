package name.ulbricht.iconcreator.gui.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/// A functional [DocumentListener] that reduces the three
/// insertion/removal/change callbacks to a single [#update()] method. All three
/// default methods delegate to [#update()], so implementors only need to provide
/// that one method.
@FunctionalInterface
public interface SimpleDocumentListener extends DocumentListener {

    default void insertUpdate(DocumentEvent e) {
        update();
    }

    default void removeUpdate(DocumentEvent e) {
        update();
    }

    default void changedUpdate(DocumentEvent e) {
        update();
    }

    /// Called whenever the document content changes (insert, remove, or
    /// attribute change).
    void update();
}
