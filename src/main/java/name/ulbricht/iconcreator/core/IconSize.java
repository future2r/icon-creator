package name.ulbricht.iconcreator.core;

/// Standard icon sizes for Windows ICO files, together with their recommended [Usage].
public enum IconSize {

    /// Small icons, classic lists, Details view in Explorer, menus.
    SIZE_16(16, Usage.REQUIRED),

    /// Modern list views; rarely required but used on some high‑DPI UIs.
    SIZE_24(24, Usage.OPTIONAL),

    /// Default medium icon size in Explorer.
    SIZE_32(32, Usage.REQUIRED),

    ///Large icon view, Control Panel.
    SIZE_48(48, Usage.REQUIRED),

    /// Mainly for scaling interpolation on high‑DPI displays.
    SIZE_64(64, Usage.OPTIONAL),

    /// Rarely used, DPI smooth scaling.
    SIZE_72(72, Usage.OPTIONAL),

    ///  150% DPI (144 DPI) scaling target.
    SIZE_96(96, Usage.RECOMMENDED),

    /// High‑DPI interpolation
    SIZE_128(128, Usage.OPTIONAL),

    /// Modern Windows uses PNG‑compressed 256×256 for Start Menu, Explorer large thumbnails.
    SIZE_256(256, Usage.RECOMMENDED);

    private final int size;
    private final Usage usage;

    /// @param size  the pixel dimension
    /// @param usage the recommended usage
    IconSize(final int size, final Usage usage) {
        this.size = size;
        this.usage = usage;
    }

    /// {@return the pixel dimension (width and height are equal) of this icon size}
    public int size() {
        return this.size;
    }

    /// {@return the recommended usage classification for this icon size}
    public Usage usage() {
        return this.usage;
    }
}
