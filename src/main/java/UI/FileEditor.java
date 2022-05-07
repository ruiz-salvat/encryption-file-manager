package UI;

import DataObjetcs.DataFileDto;
import Domain.DataFile;
import Domain.PrivateKey;
import UI.Dialogs.BinaryOptionDialog;
import UI.Dialogs.Dialog;
import Util.Constants;
import Util.DataSerializer;
import Util.FileStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import static Util.Constants.NOT_SAVED_MSG;
import static Util.Constants.SAVED_MSG;

public class FileEditor {

    private TableView tableView;
    private Button saveButton;
    private Label saveMessageLabel;
    private Button addRowButton;
    private Button addColumnButton;
    private Button removeRowButton;
    private Button removeColumnButton;
    private Pane addRowPane;
    private Pane addColumnPane;
    private Pane removeRowPane;
    private Pane removeColumnPane;
    private HBox tableHBox;
    private HBox actionButtonsHBox;
    private HBox saveButtonHBox;
    private VBox vBox;
    private Pane pane;
    private Scene scene;
    private Stage stage;

    public FileEditor(String fileContent) {
        tableView = new TableView<Map<Integer, String>>();
        tableView.setEditable(true);
        if (fileContent == null) { // generate empty table
            initializeTableView(tableView);
        } else {
            ArrayList<TableColumn<Map, String>> tableColumns = DataSerializer.getTableColumns(fileContent);
            Iterator<TableColumn<Map, String>> it = tableColumns.iterator();
            while (it.hasNext()) {
                TableColumn<Map, String> tableColumn = it.next();
                tableView.getColumns().add(tableColumn);
            }

            ObservableList<Map<Integer, String>> data = DataSerializer.stringToTableData(fileContent);
            tableView.getItems().addAll(data);
        }

        // change table name
        tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.isPrimaryButtonDown() && e.getClickCount() > 1) {
                EventTarget target = e.getTarget();
                TableColumnBase<?, ?> column = null;
                while (target instanceof Node) {
                    target = ((Node) target).getParent();
                    if (target instanceof TableColumnHeader) {
                        column = ((TableColumnHeader) target).getTableColumn();
                        if (column != null) break;
                    }
                }
                if (column != null) {
                    TableColumnBase<?,?> tableColumn = column;
                    TextField textField = new TextField(column.getText());
                    textField.setMaxWidth(column.getWidth());
                    textField.setOnAction(a -> {
                        tableColumn.setText(textField.getText());
                        tableColumn.setGraphic(null);
                    });
                    textField.focusedProperty().addListener((src, ov, nv) -> {
                        if (!nv) tableColumn.setGraphic(null);
                    });
                    column.setGraphic(textField);
                    textField.requestFocus();
                }
                e.consume();
            }
        });

        // display alert
        tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, e-> {
            if (saveMessageLabel.getText().equals(SAVED_MSG)) {
                saveMessageLabel.setText(NOT_SAVED_MSG);
            }
        });

        tableView.setMaxWidth(Constants.TABLE_SIZE_X);
        tableView.setMinWidth(Constants.TABLE_SIZE_X);
        tableView.setMaxHeight(Constants.TABLE_SIZE_Y);
        tableView.setMinHeight(Constants.TABLE_SIZE_Y);

        addRowButton = new Button("Add row");
        addRowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Map<Integer, String> item = new HashMap<Integer, String>();

                for (int i = 0; i < tableView.getColumns().size(); i++) {
                    item.put(i, " - ");
                }

                tableView.getItems().addAll(item);
            }
        });
        addRowPane = new Pane(addRowButton);
        addRowPane.setPadding(new Insets(0, Constants.BIG_PADDING, 0, Constants.BIG_PADDING));

        addColumnButton = new Button("Add column");
        addColumnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                NameSetter nameSetter = new NameSetter("Introduce the name of the column:");
                nameSetter.showDialog();

                try {
                    TableColumn<Map, String> newColumn = new TableColumn<>(nameSetter.getName());
                    newColumn.setCellValueFactory(new MapValueFactory<>(tableView.getColumns().size()));
                    newColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                    newColumn.setOnEditCommit(
                            new EventHandler<TableColumn.CellEditEvent<Map, String>>() {
                                @Override
                                public void handle(TableColumn.CellEditEvent<Map, String> t) {
                                    ((Map<Integer, String>)t.getTableView().getItems().get( t.getTablePosition().getRow() ))
                                            .replace(t.getTablePosition().getColumn(), t.getNewValue());
                                }
                            }
                    );
                    newColumn.setMinWidth(Constants.MIN_TABLE_COLUMN_SIZE);
                    newColumn.setSortable(false);
                    tableView.getColumns().add(newColumn);

                    Iterator<Map<Integer, String>> it = tableView.getItems().iterator();
                    while (it.hasNext()) {
                        HashMap<Integer, String> row = (HashMap<Integer, String>) it.next();
                        row.put(tableView.getColumns().size() - 1, " - ");
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    UI.Dialogs.Dialog dialog = new UI.Dialogs.Dialog("Error", "Name not set.");
                    dialog.showDialog();
                }
            }
        });
        addColumnPane = new Pane(addColumnButton);
        addColumnPane.setPadding(new Insets(0, Constants.BIG_PADDING, 0, Constants.BIG_PADDING));

        removeRowButton = new Button("Remove row");
        removeRowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
            }
        });
        removeRowPane = new Pane(removeRowButton);
        removeRowPane.setPadding(new Insets(0, Constants.BIG_PADDING, 0, Constants.BIG_PADDING));

        removeColumnButton = new Button("Remove column");
        removeColumnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tableView.getColumns().remove(tableView.getColumns().size() -1);
            }
        });
        removeColumnPane = new Pane(removeColumnButton);
        removeColumnPane.setPadding(new Insets(0, Constants.BIG_PADDING, 0, Constants.BIG_PADDING));

        saveButton = new Button("Save");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FileChooser fileChooser = new FileChooser();
                    if (DataFile.isSet())
                        fileChooser.setInitialFileName(DataFile.getName());
                    else
                        fileChooser.setInitialFileName(PrivateKey.getName());
                    File selectedFile = fileChooser.showSaveDialog(stage);
                    String filePath = selectedFile.getPath() + Constants.DATA_FILE_FILE_EXTENSION;

                    String tableData = DataSerializer.tableToString(tableView.getColumns(), tableView.getItems());
                    String cypherText = null;
                    String name = null;
                    if (PrivateKey.isSet()) {
                        cypherText = PrivateKey.encrypt(tableData);
                        name = selectedFile.getName() + "_(key: " + PrivateKey.getName() + ")";
                    } else {
                        NameSetter nameSetter = new NameSetter("No key has yet been set. Set a new one with the name:");
                        nameSetter.showDialog();
                        PrivateKey.createNewPrivateKey(nameSetter.getName());
                        PrivateKey.save(nameSetter.getName());
                        cypherText = PrivateKey.encrypt(tableData);
                        name = nameSetter.getName();
                    }

                    DataFileDto dataFileDto = new DataFileDto(name, cypherText);
                    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    String jsonStr = objectWriter.writeValueAsString(dataFileDto);

                    FileStorage.saveFile(filePath, jsonStr);

                    String oldFileName = DataFile.getName();
                    if (oldFileName.equals(name)) { // the file is overridden, therefore the app needs to load it back
                        try {
                            String jsonFile = FileStorage.readFile(selectedFile.getPath() + Constants.DATA_FILE_FILE_EXTENSION);
                            ObjectMapper objectMapper = new ObjectMapper();
                            DataFileDto dataFileDtoReadOnly = objectMapper.readValue(jsonFile, DataFileDto.class);
                            String fileContent = PrivateKey.decrypt(dataFileDtoReadOnly.content);
                            DataFile.setNewDataFile(dataFileDtoReadOnly.name, fileContent);
                        } catch (FileNotFoundException | JsonProcessingException | NoSuchPaddingException |
                                 UnsupportedEncodingException | NoSuchAlgorithmException e) {
                            UI.Dialogs.Dialog dialog = new Dialog("Error", "Error reading an overridden file.");
                            dialog.showDialog();
                        }
                    }

                    PrivateKey.resetKey();
                    saveMessageLabel.setText(SAVED_MSG);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
                        | JsonProcessingException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });

        saveMessageLabel = new Label(NOT_SAVED_MSG);
        saveMessageLabel.setTextFill(Color.web("#ff0000"));
        saveMessageLabel.setPadding(new Insets(Constants.SMALL_PADDING, 0, Constants.SMALL_PADDING, Constants.BIG_PADDING));

        tableHBox = new HBox(tableView);
        tableHBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING));
        actionButtonsHBox = new HBox(addRowPane, addColumnPane, removeRowPane, removeColumnPane);
        actionButtonsHBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING));
        saveButtonHBox = new HBox(saveButton, saveMessageLabel);
        saveButtonHBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING));

        vBox = new VBox(tableHBox, actionButtonsHBox, saveButtonHBox);

        pane = new Pane(vBox);

        scene = new Scene(pane, Constants.FILE_EDITOR_WINDOW_SIZE_X, Constants.FILE_EDITOR_WINDOW_SIZE_Y);

        stage = new Stage();

        stage.setTitle("File editor");
        stage.setResizable(false);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (saveMessageLabel.getText().equals(NOT_SAVED_MSG)) {
                    BinaryOptionDialog dialog = new BinaryOptionDialog("File not saved", "The changes were not saved.\n" +
                            "Do you want close the editor anyway?");
                    dialog.showDialog();
                    if (dialog.isCancelled())
                        windowEvent.consume();
                }
            }
        });
        stage.setScene(scene);
    }

    private TableView initializeTableView(TableView tableView) {
        TableColumn<Map, String> attr1Column = new TableColumn<>("Attr. 1");
        attr1Column.setCellValueFactory(new MapValueFactory<>(0));
        attr1Column.setCellFactory(TextFieldTableCell.forTableColumn());
        attr1Column.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Map, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Map, String> t) {
                        ((Map<Integer, String>)t.getTableView().getItems().get( t.getTablePosition().getRow() ))
                                .replace(t.getTablePosition().getColumn(), t.getNewValue());
                    }
                }
        );
        attr1Column.setMinWidth(Constants.MIN_TABLE_COLUMN_SIZE);
        attr1Column.setSortable(false);

        TableColumn<Map, String> attr2Column = new TableColumn<>("Attr. 2");
        attr2Column.setCellValueFactory(new MapValueFactory<>(1));
        attr2Column.setCellFactory(TextFieldTableCell.forTableColumn());
        attr2Column.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Map, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Map, String> t) {
                        ((Map<Integer, String>)t.getTableView().getItems().get( t.getTablePosition().getRow() ))
                                .replace(t.getTablePosition().getColumn(), t.getNewValue());
                    }
                }
        );
        attr2Column.setMinWidth(Constants.MIN_TABLE_COLUMN_SIZE);
        attr2Column.setSortable(false);

        tableView.getColumns().add(attr1Column);
        tableView.getColumns().add(attr2Column);

        ObservableList<Map<Integer, String>> data = FXCollections.<Map<Integer, String>>observableArrayList();

        Map<Integer, String> item1 = new HashMap<>();
        item1.put(0, "Value 1");
        item1.put(1, "Value 2");

        data.add(item1);

        Map<Integer, String> item2 = new HashMap<>();
        item2.put(0, "Value 3");
        item2.put(1, "Value 4");

        data.add(item2);

        tableView.getItems().addAll(data);

        return tableView;
    }

    public void openFileEditor() {
        stage.showAndWait();
    }

}
