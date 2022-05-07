package Util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataSerializer {

    public static String tableToString(ObservableList<TableColumn<Map, String>> tableColumns, ObservableList<Map<Integer, String>> data) {
        String contentStr = "";

        Iterator<TableColumn<Map, String>> columnIt = tableColumns.iterator();
        while (columnIt.hasNext()) {
            TableColumn<Map, String> column = columnIt.next();
            contentStr = contentStr + column.getText() + ",";
        }
        contentStr = contentStr.substring(0, contentStr.length() - 1) + "\n";

        Iterator<Map<Integer, String>> dataIt = data.iterator();
        while (dataIt.hasNext()) {
            Map<Integer, String> row = dataIt.next();
            for (Map.Entry<Integer, String> entry : row.entrySet()) {
                contentStr = contentStr + entry.getValue() + ",";
            }
            contentStr = contentStr.substring(0, contentStr.length() - 1) + "\n";
        }
        contentStr = contentStr.substring(0, contentStr.length() - 1);
        return contentStr;
    }

    public static ObservableList<Map<Integer, String>> stringToTableData(String strData) {
        ObservableList<Map<Integer, String>> data = FXCollections.<Map<Integer, String>>observableArrayList();
        String[] rows = strData.split("\n");

        for (int i = 1; i < rows.length; i++) {  // exclude header
            String[] instances = rows[i].split(",");

            Map<Integer, String> item = new HashMap<>();

            for (int j = 0; j < instances.length; j++) {
                item.put(j, instances[j]);
            }

            data.add(item);
        }

        return data;
    }

    public static ArrayList<TableColumn<Map, String>> getTableColumns(String strData) {
        ArrayList<TableColumn<Map, String>> columns = new ArrayList<TableColumn<Map, String>>();
        String[] rows = strData.split("\n");
        String header = rows[0];  // take just the first row
        String[] fields = header.split(",");
        for (int i = 0; i < fields.length; i++) {

            TableColumn<Map, String> attrColumn = new TableColumn<>(fields[i]);
            attrColumn.setCellValueFactory(new MapValueFactory<>(i));
            attrColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            attrColumn.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Map, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Map, String> t) {
                            ((Map<Integer, String>)t.getTableView().getItems().get( t.getTablePosition().getRow() ))
                                    .replace(t.getTablePosition().getColumn(), t.getNewValue());
                        }
                    }
            );
            attrColumn.setMinWidth(Constants.MIN_TABLE_COLUMN_SIZE);
            attrColumn.setSortable(false);

            columns.add(attrColumn);
        }
        return columns;
    }

}
