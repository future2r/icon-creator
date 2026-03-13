package name.ulbricht.iconcreator.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import name.ulbricht.iconcreator.Application;
import name.ulbricht.iconcreator.gui.main.MainView;
import name.ulbricht.iconcreator.gui.main.MainViewModel;
import name.ulbricht.iconcreator.gui.util.BusyGlassPane;
import name.ulbricht.iconcreator.gui.util.TaskRunner;

/// The application's main window.
public final class MainFrame extends JFrame {

    private final ResourceBundle resources = ResourceBundle.getBundle("name.ulbricht.iconcreator.gui.i18n.messages");

    private final MainViewModel viewModel;
    private final MainView view;
    private final BusyGlassPane busyGlassPane;

    /// Creates and initialises the main window.
    public MainFrame() {
        setTitle("Icon Creator");
        setIconImages(loadIcons());

        this.viewModel = new MainViewModel(TaskRunner::async);
        this.view = new MainView(resources, this.viewModel);

        setContentPane(view);
        setMinimumSize(new Dimension(450, 350));
        setSize(700, 500);
        setLocationRelativeTo(null);

        this.busyGlassPane = new BusyGlassPane();
        setGlassPane(this.busyGlassPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                if (!MainFrame.this.viewModel.isBusy()) {
                    MainFrame.this.dispose();
                }
            }
        });

        this.viewModel.addPropertyChangeListener(this::viewModelChanged);
    }

    private static List<BufferedImage> loadIcons() {
        return IntStream.of(16, 32, 48, 64, 128, 256)
                .mapToObj(size -> "/name/ulbricht/iconcreator/gui/image/application_%d.png".formatted(size))
                .map(Application.class::getResource)
                .map(resource -> {
                    try {
                        return ImageIO.read(resource);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }).toList();
    }

    private void viewModelChanged(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case MainViewModel.PROPERTY_BUSY -> {
                final var busy = ((Boolean) event.getNewValue()).booleanValue();
                this.busyGlassPane.setVisible(busy);
                setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
            }
            case MainViewModel.PROPERTY_ERROR -> {
                final var cause = (Exception) event.getNewValue();
                final var message = cause.getLocalizedMessage() != null ? cause.getLocalizedMessage()
                        : cause.getClass().getSimpleName();
                JOptionPane.showMessageDialog(this, message, this.resources.getString("error.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
