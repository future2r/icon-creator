package name.ulbricht.iconcreator.config;

import java.nio.file.Path;
import java.util.prefs.Preferences;

/// Persists user-specific preferences using the Java [Preferences] API.
public final class UserPreferences {

    private static final String PREF_INPUT_DIRECTORY = "inputDirectory";
    private static final String PREF_OUTPUT_FILE = "outputFile";

    private final Preferences preferences = Preferences.userRoot().node("name/ulbricht/iconcreator");

    /// {@return the last-used input directory, or the current working directory if
    /// none is stored}
    public Path getInputDirectory() {
        return getPath(PREF_INPUT_DIRECTORY);
    }

    /// Persists the input directory.
    ///
    /// @param inputDirectory the directory to store, or `null` to remove
    ///                       the preference
    public void setInputDirectory(final Path inputDirectory) {
        putPath(PREF_INPUT_DIRECTORY, inputDirectory);
    }

    /// {@return the last-used output file path, or the current working directory if
    /// none is stored}
    public Path getOutputFile() {
        return getPath(PREF_OUTPUT_FILE);
    }

    /// Persists the output file path.
    ///
    /// @param outputFile the file path to store, or `null` to remove the preference
    public void setOutputFile(final Path outputFile) {
        putPath(PREF_OUTPUT_FILE, outputFile);
    }

    private Path getPath(final String key) {
        try {
            return Path.of(this.preferences.get(key, "."));
        } catch (Exception ex) {
            // If the stored path is invalid, return the current directory
            return Path.of(".");
        }
    }

    private void putPath(final String key, final Path path) {
        if (path != null)
            this.preferences.put(key, path.toString());
        else
            this.preferences.remove(key);
    }
}
