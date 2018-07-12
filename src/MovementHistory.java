import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MovementHistory extends TableView {
    public MovementHistory(int playerAmount){
        this.setEditable(false);

        TableColumn[] columnList = new TableColumn[]{
                new TableColumn("Y"),
                new TableColumn("B"),
                new TableColumn("O"),
                new TableColumn("G")
        };


        for(int i = 0; i < playerAmount; i++){
            columnList[i].setSortable(false);
            columnList[i].setMinWidth(90);
            this.getColumns().add(columnList[i]);

        }

        columnList[0].setCellValueFactory(new PropertyValueFactory<>("yellowMove"));
        columnList[1].setCellValueFactory(new PropertyValueFactory<>("blueMove"));
        columnList[2].setCellValueFactory(new PropertyValueFactory<>("orangeMove"));
        columnList[3].setCellValueFactory(new PropertyValueFactory<>("greenMove"));
    }

}
