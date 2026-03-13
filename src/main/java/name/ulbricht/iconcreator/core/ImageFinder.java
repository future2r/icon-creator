package name.ulbricht.iconcreator.core;

import static java.util.Objects.requireNonNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/// Scans a directory for image files matching afilename pattern for each
/// [IconSize]. The finder is [AutoCloseable]; the underlying file-system stream
/// is closed when [#close()] is called.
public final class ImageFinder implements AutoCloseable {

    private final Path directory;
    private final String filePattern;
    private Stream<ImageCandidate> images;

    /// Creates a new `ImageFinder`.
    ///
    /// @param directory   the directory to search
    /// @param filePattern a [String#formatted] pattern where `%1$d` is replaced by
    ///                    the icon size in pixels (e.g. `"application_%1$d.png"`)
    public ImageFinder(final Path directory, final String filePattern) {
        this.directory = requireNonNull(directory);
        this.filePattern = requireNonNull(filePattern);
    }

    /// Returns a stream of image candidates, one per [IconSize]. Candidates whose
    /// file was not found carry a `null` file component.
    ///
    /// @return a stream of [ImageCandidate] instances
    public Stream<ImageCandidate> findImages() {
        if (this.images == null)
            this.images = Stream.of(IconSize.values())
                    .map(size -> new ImageCandidate(size, findImageFile(size).orElse(null)));

        return this.images;
    }

    private Optional<Path> findImageFile(IconSize iconSize) {
        final var fileName = this.filePattern.formatted(iconSize.size());
        final var file = this.directory.resolve(fileName);
        if (Files.isRegularFile(file))
            return Optional.of(file);
        return Optional.empty();
    }

    /// Closes the underlying image stream if it has been opened.
    @Override
    public void close() {
        if (this.images != null)
            this.images.close();
    }
}
