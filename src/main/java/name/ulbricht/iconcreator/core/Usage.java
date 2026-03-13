package name.ulbricht.iconcreator.core;

/// Classifies the recommended usage of an [IconSize] within a Windows ICO file.
public enum Usage {

    /// The size should always be included; omitting it may cause visual artefacts in the OS shell.
    REQUIRED,

    /// The size is beneficial for high-DPI scenarios but is not strictly necessary.
    RECOMMENDED,

    /// The size is rarely needed and can be omitted for a smaller file size.
    OPTIONAL;
}
