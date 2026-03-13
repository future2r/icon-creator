package name.ulbricht.iconcreator.gui.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import name.ulbricht.iconcreator.gui.util.TaskRunner;

final class MainViewModelTest {

    private MainViewModel viewModel;

    @BeforeEach
    void beforeEach() {
        this.viewModel = new MainViewModel(TaskRunner::sync);
    }

    @Test
    void init() {
        assertNull(this.viewModel.getInputDirectory());
        assertEquals("application_%1$d.png", this.viewModel.getInputPattern());
        assertFalse(this.viewModel.canFindImages());

        assertEquals(9, this.viewModel.getImages().size());

        assertNull(this.viewModel.getOutputFile());
        assertFalse(this.viewModel.canCreate());

        assertFalse(this.viewModel.isBusy());
    }

    @Test
    void canFindImages() {
        assertFalse(this.viewModel.canFindImages());

        this.viewModel.setInputDirectory(Path.of("./test"));
        assertTrue(this.viewModel.canFindImages());

        this.viewModel.setInputPattern(null);
        assertFalse(this.viewModel.canFindImages());

        this.viewModel.setInputPattern("");
        assertFalse(this.viewModel.canFindImages());

        this.viewModel.setInputPattern("  ");
        assertFalse(this.viewModel.canFindImages());

        this.viewModel.setInputPattern("pattern");
        assertTrue(this.viewModel.canFindImages());

        this.viewModel.setInputDirectory(null);
        assertFalse(this.viewModel.canFindImages());
    }

    @Test
    void canCreate() {
        assertFalse(this.viewModel.canCreate());

        this.viewModel.getImages().stream()
                .filter(ImageItem::isSelected)
                .forEach(image -> image.setFile(Path.of("file.png")));

        assertFalse(this.viewModel.canCreate());

        this.viewModel.setOutputFile(Path.of("output.ico"));
        assertTrue(this.viewModel.canCreate());

        this.viewModel.getImages().stream()
                .filter(ImageItem::isSelected)
                .forEach(image -> image.setFile(null));
        assertFalse(this.viewModel.canCreate());
    }
}
