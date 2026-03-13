package name.ulbricht.iconcreator;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import name.ulbricht.iconcreator.gui.MainFrame;

/// Entry point for the Icon Creator application.
///
/// Configures the system look and feel and opens the main window on the Swing
/// event-dispatch thread.
public final class Application {

    /// Launches the application.
    ///
    /// @param args command-line arguments (currently unused)
    static void main(final String[] args) {
        // Use the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException _) {
            // Fall back to the default look and feel
        }

        SwingUtilities.invokeLater(() -> {
            try {
                final var frame = new MainFrame();
                frame.setVisible(true);
            } catch (final IllegalStateException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        });
    }
}
