package name.ulbricht.iconcreator.core;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

/// Associates an [IconSize] with an optional file path found by [ImageFinder].
///
/// @param size the icon size this candidate targets
/// @param file the path of the matching image file, or `null` if none was found
public record ImageCandidate(IconSize size, Path file) {

    /// Creates a new candidate for the given size and file.
    public ImageCandidate {
        requireNonNull(size);
    }
}
