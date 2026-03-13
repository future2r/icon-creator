package name.ulbricht.iconcreator.gui.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

/// A transparent glass pane that blocks all user input while a background task
/// is running. When visible, it paints a semi-transparent dark overlay over the
/// content pane to give a visual indication that the application is busy.
public final class BusyGlassPane extends JComponent {

    /// Creates a new `BusyGlassPane` and installs input-swallowing listeners.
    public BusyGlassPane() {
        setOpaque(false);

        // Swallow all events
        addMouseListener(new MouseAdapter() {
        });
        addMouseMotionListener(new MouseMotionAdapter() {
        });
        addKeyListener(new KeyAdapter() {
        });
        setFocusTraversalKeysEnabled(false);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final var g2 = (Graphics2D) g.create();
        try {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } finally {
            g2.dispose();
        }
    }
}
