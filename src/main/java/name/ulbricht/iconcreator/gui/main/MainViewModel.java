package name.ulbricht.iconcreator.gui.main;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import javax.swing.SwingWorker;

import name.ulbricht.iconcreator.core.IconCreator;
import name.ulbricht.iconcreator.core.IconSize;
import name.ulbricht.iconcreator.core.ImageCandidate;
import name.ulbricht.iconcreator.core.ImageFinder;

/// View model for [MainView], implementing the JavaBeans property-change
/// notification pattern. Manages the selected input directory, filename pattern,
/// discovered image candidates, output file path, and background-task state. All
/// mutating operations are intended to be called on the Swing
/// event-dispatch thread.
public final class MainViewModel {

    /// Property name fired when [#isBusy()] changes.
    public static final String PROPERTY_BUSY = "busy";

    /// Property name fired when [#getInputDirectory()] changes.
    public static final String PROPERTY_INPUT_DIRECTORY = "inputDirectory";

    /// Property name fired when [#getInputPattern()] changes.
    public static final String PROPERTY_INPUT_PATTERN = "inputPattern";

    /// Property name fired when [#canFindImages()] changes.
    public static final String PROPERTY_CAN_FIND_IMAGES = "canFindImages";

    /// Property name fired when [#getOutputFile()] changes.
    public static final String PROPERTY_OUTPUT_FILE = "outputFile";

    /// Property name fired when [#canCreate()] changes.
    public static final String PROPERTY_CAN_CREATE = "canCreate";

    /// Property name fired when a background task fails. The new value is the
    /// {@link Exception} that caused the failure.
    public static final String PROPERTY_ERROR = "error";

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private boolean busy;
    private Exception error;

    private Path inputDirectory;
    private Path outputFile;

    private final List<String> inputPatterns;
    private String inputPattern;

    private final List<ImageItem> images;

    /// Creates a new view model with the default filename patterns and one
    /// [ImageItem] per [IconSize].
    public MainViewModel() {
        this.inputPatterns = List.of( //
                "application_%1$d.png", //
                "application_%1$dx%1$d.png");
        this.inputPattern = this.inputPatterns.getFirst();

        this.images = Stream.of(IconSize.values())
                .map(ImageItem::new)
                .toList();

        final PropertyChangeListener imageItemChangeListener = this::imageItemChanged;
        this.images.forEach(item -> item.addPropertyChangeListener(imageItemChangeListener));
    }

    private void imageItemChanged(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ImageItem.PROPERTY_SELECTED, ImageItem.PROPERTY_FILE ->
                this.propertyChangeSupport.firePropertyChange(PROPERTY_CAN_CREATE, null, canCreate());
        }
    }

    /// Registers a listener for property-change notifications.
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

    /// {@return `true` while a background task is running}
    public boolean isBusy() {
        return this.busy;
    }

    /// {@return the exception from the most recent failed background task, or
    /// `null` if the last task completed successfully}
    public Exception getError() {
        return this.error;
    }

    private void setError(final Exception ex) {
        final var oldValue = this.error;
        this.error = switch (ex) {
            case ExecutionException e -> e.getCause() instanceof Exception cause ? cause : e;
            case InterruptedException e -> {
                Thread.currentThread().interrupt();
                yield e;
            }
            default -> ex;
        };
        this.propertyChangeSupport.firePropertyChange(PROPERTY_ERROR, oldValue, this.error);
    }

    private void setBusy(boolean busy) {
        boolean old = this.busy;
        this.busy = busy;
        this.propertyChangeSupport.firePropertyChange(PROPERTY_BUSY, old, busy);
    }

    /// {@return the immutable list of available filename patterns}
    public List<String> getInputPatterns() {
        return this.inputPatterns;
    }

    /// {@return the currently configured input directory, or `null` if none has
    /// been set}
    public Path getInputDirectory() {
        return this.inputDirectory;
    }

    /// Sets the input directory and fires the appropriate property-change events.
    ///
    /// @param inputDirectory the directory to search for source images
    public void setInputDirectory(final Path inputDirectory) {
        final var oldValue = this.inputDirectory;
        this.inputDirectory = inputDirectory;
        this.propertyChangeSupport.firePropertyChange(PROPERTY_INPUT_DIRECTORY, oldValue, inputDirectory);
        this.propertyChangeSupport.firePropertyChange(PROPERTY_CAN_FIND_IMAGES, null, canFindImages());

        if (this.outputFile == null && this.inputDirectory != null) {
            setOutputFile(inputDirectory.resolve("application.ico"));
        }
    }

    /// {@return the current filename pattern used to locate source images}
    public String getInputPattern() {
        return this.inputPattern;
    }

    /// Sets the filename pattern and fires the appropriate property-change events.
    ///
    /// @param inputPattern a [String#formatted] pattern where `%1$d` is replaced by
    ///                     the icon size in pixels
    public void setInputPattern(final String inputPattern) {
        final var oldValue = this.inputPattern;
        this.inputPattern = inputPattern;
        this.propertyChangeSupport.firePropertyChange(PROPERTY_INPUT_PATTERN, oldValue, inputPattern);
        this.propertyChangeSupport.firePropertyChange(PROPERTY_CAN_FIND_IMAGES, null, canFindImages());
    }

    /// {@return the currently configured output ICO file path, or `null` if none
    /// has been set}
    public Path getOutputFile() {
        return this.outputFile;
    }

    /// Sets the output ICO file path and fires the appropriate property-change
    /// events.
    ///
    /// @param outputFile the path of the ICO file to write
    public void setOutputFile(final Path outputFile) {
        final var oldValue = this.outputFile;
        this.outputFile = outputFile;
        this.propertyChangeSupport.firePropertyChange(PROPERTY_OUTPUT_FILE, oldValue, outputFile);
        this.propertyChangeSupport.firePropertyChange(PROPERTY_CAN_CREATE, null, canCreate());
    }

    /// {@return `true` if all prerequisites for [#findImages()] are satisfied}
    ///
    /// Prerequisites: an input directory must be set and the pattern must
    /// be non-blank.
    public boolean canFindImages() {
        return this.inputDirectory != null && this.inputPattern != null && !this.inputPattern.isBlank();
    }

    /// Starts a background worker that searches the input directory for source
    /// images matching the current pattern and updates each [ImageItem] with the
    /// result. Does nothing when a task is already running or
    /// [#canFindImages()] returns `false`.
    public void findImages() {
        if (isBusy() || !canFindImages())
            return;

        final var directory = getInputDirectory();
        final var pattern = getInputPattern();

        final var worker = new SwingWorker<Void, ImageCandidate>() {

            @Override
            protected Void doInBackground() throws Exception {
                try (var imageFinder = new ImageFinder(directory, pattern)) {
                    imageFinder.findImages().forEach(this::publish);
                }
                return null;
            }

            @Override
            protected void process(final List<ImageCandidate> chunks) {
                chunks.forEach(MainViewModel.this::updateImage);
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    get();
                } catch (ExecutionException | InterruptedException ex) {
                    MainViewModel.this.setError(ex);
                }
            }
        };

        setBusy(true);

        clearImages();

        worker.execute();
    }

    private void clearImages() {
        this.images.forEach(image -> image.setFile(null));
        this.propertyChangeSupport.firePropertyChange(PROPERTY_CAN_CREATE, null, canCreate());
    }

    private List<Path> getUsableImageFiles() {
        return this.images.stream()
                .filter(ImageItem::isSelected)
                .map(ImageItem::getFile)
                .filter(Objects::nonNull)
                .toList();
    }

    /// {@return the live list of image items, one per [IconSize]}
    public List<ImageItem> getImages() {
        return this.images;
    }

    /// {@return `true` if an output file is set and at least one selected image has
    /// a resolved file}
    public boolean canCreate() {
        return this.outputFile != null && !getUsableImageFiles().isEmpty();
    }

    /// Starts a background worker that assembles the ICO file from the currently
    /// selected and resolved image files. Does nothing when a task is already
    /// running or [#canCreate()] returns `false`.
    public void createIcon() {
        if (isBusy() || !canCreate())
            return;

        final var file = getOutputFile();
        final var imageFiles = getUsableImageFiles();

        final var worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                final var iconCreator = new IconCreator(file, imageFiles);
                iconCreator.create();
                return null;
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    get();
                } catch (ExecutionException | InterruptedException ex) {
                    MainViewModel.this.setError(ex);
                }
            }
        };

        setBusy(true);

        worker.execute();
    }

    private void updateImage(final ImageCandidate candidate) {
        this.images.stream()
                .filter(item -> item.size() == candidate.size())
                .findFirst()
                .ifPresent(item -> item.setFile(candidate.file()));
    }
}
