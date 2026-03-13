package name.ulbricht.iconcreator.gui.main;

import static java.util.Objects.requireNonNull;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import name.ulbricht.iconcreator.config.UserPreferences;
import name.ulbricht.iconcreator.gui.util.FileTableCellRenderer;
import name.ulbricht.iconcreator.gui.util.IconSizeTableCellRenderer;
import name.ulbricht.iconcreator.gui.util.SimpleDocumentListener;
import name.ulbricht.iconcreator.gui.util.SingleClickBooleanEditor;
import name.ulbricht.iconcreator.gui.util.UsageTableCellRenderer;

/// The main view panel containing the input-directory selector, the images
/// table, and the output-file controls. Binds bidirectionally to a
/// [MainViewModel]. UI events are forwarded to the view model and view-model
/// property-change events update the UI.
public final class MainView extends JPanel {

    private static final int VIEW_BORDER = 12;
    private static final int GAP = 8;

    private ResourceBundle resources;
    private final MainViewModel viewModel;

    private final UserPreferences preferences = new UserPreferences();

    private JTextField inputDirectoryTextField;
    private JButton inputDirectoryBrowseButton;
    private JComboBox<String> inputPatternComboBox;

    private JButton findButton;
    private ImagesTableModel imagesTableModel;
    private JTable imagesTable;

    private JTextField outputFileNameTextField;
    private JButton outputFileNameBrowseButton;
    private JButton createIconButton;

    private JFileChooser inputDirectoryChooser;
    private JFileChooser outputFileChooser;

    /// Creates the main view and binds it to the given view model.
    ///
    /// @param resources the resource bundle for localized messages; must not be
    ///                  `null`
    /// @param viewModel the view model to bind to; must not be `null`
    public MainView(final ResourceBundle resources, final MainViewModel viewModel) {
        this.resources = requireNonNull(resources);
        this.viewModel = requireNonNull(viewModel);

        setLayout(new BorderLayout(0, GAP));
        setBorder(BorderFactory.createEmptyBorder(VIEW_BORDER, VIEW_BORDER, VIEW_BORDER, VIEW_BORDER));

        add(createInputPanel(), BorderLayout.CENTER);
        add(createOutputPanel(), BorderLayout.SOUTH);

        setupViewModelBindings();
        SwingUtilities.invokeLater(this::initializeComponents);
    }

    private JPanel createInputPanel() {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(this.resources.getString("main.input.title")));
        final var layout = new GroupLayout(panel);
        panel.setLayout(layout);

        this.inputDirectoryTextField = new JTextField(20);
        this.inputDirectoryTextField.setEditable(false);
        final var inputDirectoryLabel = new JLabel(this.resources.getString("main.input.directory"));
        inputDirectoryLabel.setLabelFor(this.inputDirectoryTextField);
        this.inputDirectoryBrowseButton = new JButton(this.resources.getString("main.input.browse"));
        this.inputDirectoryBrowseButton.addActionListener(_ -> selectInputDirectory());

        this.inputPatternComboBox = new JComboBox<>(new PatternComboBoxModel(this.viewModel.getInputPatterns()));
        this.inputPatternComboBox.setEditable(true);
        final var inputPatternLabel = new JLabel(this.resources.getString("main.input.pattern"));
        inputPatternLabel.setLabelFor(this.inputPatternComboBox);

        this.findButton = new JButton(this.resources.getString("main.input.find"));
        this.findButton.addActionListener(_ -> this.viewModel.findImages());

        this.imagesTableModel = new ImagesTableModel(this.resources, this.viewModel.getImages());
        this.imagesTable = new JTable(this.imagesTableModel);
        this.imagesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        this.imagesTable.setDefaultEditor(Boolean.class, new SingleClickBooleanEditor());

        final var selectedColumn = this.imagesTable.getColumnModel()
                .getColumn(ImagesTableModel.Column.SELECTED.ordinal());
        selectedColumn.setWidth(50);

        final var sizeColumn = this.imagesTable.getColumnModel().getColumn(ImagesTableModel.Column.SIZE.ordinal());
        sizeColumn.setWidth(80);
        sizeColumn.setCellRenderer(new IconSizeTableCellRenderer(this.resources));

        final var usageColumn = this.imagesTable.getColumnModel().getColumn(ImagesTableModel.Column.USAGE.ordinal());
        usageColumn.setWidth(120);
        usageColumn.setCellRenderer(new UsageTableCellRenderer(this.resources));

        final var fileColumn = this.imagesTable.getColumnModel().getColumn(ImagesTableModel.Column.FILE.ordinal());
        fileColumn.setCellRenderer(new FileTableCellRenderer());

        final var imagesTableScrollPane = new JScrollPane(this.imagesTable);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(
                                        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(inputDirectoryLabel)
                                                .addComponent(inputPatternLabel))
                                .addGroup(
                                        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(this.inputDirectoryTextField)
                                                .addComponent(this.inputPatternComboBox))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.inputDirectoryBrowseButton)))
                        .addComponent(this.findButton)
                        .addComponent(imagesTableScrollPane));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(inputDirectoryLabel)
                                        .addComponent(this.inputDirectoryTextField)
                                        .addComponent(this.inputDirectoryBrowseButton))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(inputPatternLabel)
                                        .addComponent(this.inputPatternComboBox))
                        .addComponent(this.findButton)
                        .addComponent(imagesTableScrollPane));

        return panel;
    }

    private JPanel createOutputPanel() {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(this.resources.getString("main.output.title")));
        final var layout = new GroupLayout(panel);
        panel.setLayout(layout);

        this.outputFileNameTextField = new JTextField(20);
        this.outputFileNameTextField.setEditable(false);
        final var outputFileNameLabel = new JLabel(this.resources.getString("main.output.fileName"));
        outputFileNameLabel.setLabelFor(this.outputFileNameTextField);
        this.outputFileNameBrowseButton = new JButton(this.resources.getString("main.output.browse"));
        this.outputFileNameBrowseButton.addActionListener(_ -> selectOutputFile());

        this.createIconButton = new JButton(this.resources.getString("main.output.createIcon"));
        this.createIconButton.addActionListener(_ -> this.viewModel.createIcon());

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(outputFileNameLabel)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(this.outputFileNameTextField)
                                .addComponent(this.createIconButton))
                        .addComponent(this.outputFileNameBrowseButton));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(outputFileNameLabel)
                                .addComponent(this.outputFileNameTextField)
                                .addComponent(this.outputFileNameBrowseButton))
                        .addComponent(this.createIconButton));

        return panel;
    }

    private void setupViewModelBindings() {
        this.viewModel.addPropertyChangeListener(this::viewModelChanged);

        if (this.inputPatternComboBox.getEditor().getEditorComponent() instanceof JTextField editor) {
            editor.getDocument().addDocumentListener(
                    (SimpleDocumentListener) () -> this.viewModel.setInputPattern(editor.getText()));
        }
    }

    private void viewModelChanged(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case MainViewModel.PROPERTY_INPUT_DIRECTORY -> {
                final var currentValue = this.inputDirectoryTextField.getText();
                final var newValue = Objects.toString(event.getNewValue(), "");
                if (!Objects.equals(currentValue, newValue))
                    this.inputDirectoryTextField.setText(newValue);
            }
            case MainViewModel.PROPERTY_INPUT_PATTERN -> {
                final var currentValue = this.inputPatternComboBox.getEditor().getItem();
                final var newValue = this.viewModel.getInputPattern();
                if (!Objects.equals(currentValue, newValue))
                    this.inputPatternComboBox.getEditor().setItem(newValue);
            }
            case MainViewModel.PROPERTY_CAN_FIND_IMAGES ->
                this.findButton.setEnabled(((Boolean) event.getNewValue()).booleanValue());
            case MainViewModel.PROPERTY_OUTPUT_FILE -> {
                final var currentValue = this.outputFileNameTextField.getText();
                final var newValue = Objects.toString(event.getNewValue(), "");
                if (!Objects.equals(currentValue, newValue))
                    this.outputFileNameTextField.setText(newValue);
            }
            case MainViewModel.PROPERTY_CAN_CREATE ->
                this.createIconButton.setEnabled(((Boolean) event.getNewValue()).booleanValue());
        }
    }

    private void initializeComponents() {
        this.inputDirectoryBrowseButton.requestFocusInWindow();
        this.inputPatternComboBox.getEditor().setItem(this.viewModel.getInputPattern());
        this.findButton.setEnabled(this.viewModel.canFindImages());
        this.createIconButton.setEnabled(this.viewModel.canCreate());
    }

    /// {@return `true` if the view can be closed, i.e. no background task is
    /// currently running}
    public boolean canClose() {
        return !this.viewModel.isBusy();
    }

    private void selectInputDirectory() {
        if (this.inputDirectoryChooser == null) {
            this.inputDirectoryChooser = new JFileChooser();
            this.inputDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            this.inputDirectoryChooser
                    .setCurrentDirectory(this.preferences.getInputDirectory().toFile());
        }

        if (this.inputDirectoryChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            final var directory = this.inputDirectoryChooser.getSelectedFile().toPath();
            this.preferences.setInputDirectory(directory);
            this.viewModel.setInputDirectory(directory.toAbsolutePath());
            this.inputDirectoryTextField.setText(directory.toAbsolutePath().toString());
        }
    }

    private void selectOutputFile() {
        if (this.outputFileChooser == null) {
            this.outputFileChooser = new JFileChooser();
            this.outputFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            this.outputFileChooser
                    .setCurrentDirectory(this.preferences.getOutputFile().toFile());
        }

        if (this.outputFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            final var file = this.outputFileChooser.getSelectedFile().toPath();
            this.preferences.setOutputFile(file.getParent());
            this.viewModel.setOutputFile(file.toAbsolutePath());
            this.outputFileNameTextField.setText(file.toAbsolutePath().toString());
        }
    }
}
