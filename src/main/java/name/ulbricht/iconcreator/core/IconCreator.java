package name.ulbricht.iconcreator.core;

import static java.util.Objects.requireNonNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import net.sf.image4j.codec.ico.ICOEncoder;

/// Creates a Windows ICO file from a set of source images.
public final class IconCreator {

    private final Path iconFile;
    private final List<Path> imageFiles;

    /// Creates a new `IconCreator`.
    ///
    /// @param iconFile   the path of the ICO file to write
    /// @param imageFiles the ordered list of source image files
    public IconCreator(final Path iconFile, final List<Path> imageFiles) {
        this.iconFile = requireNonNull(iconFile);
        this.imageFiles = requireNonNull(imageFiles);
    }

    /// Reads all configured source images and writes them into the ICO file.
    ///
    /// @throws UncheckedIOException if any source image cannot be read or the ICO
    ///                              file cannot be written
    public void create() throws IOException {
        try {
            this.imageFiles.stream()
                    // Read the file
                    .map(this::readImage)
                    // Collect into an ICO file
                    .collect(Collectors.collectingAndThen(Collectors.toList(), this::writeIcon));
        } catch (final UncheckedIOException ex) {
            throw ex.getCause();
        }
    }

    private BufferedImage readImage(final Path file) {
        try {
            return ImageIO.read(file.toFile());
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private Void writeIcon(List<BufferedImage> images) {
        try {
            ICOEncoder.write(images, this.iconFile.toFile());
            return null;
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
