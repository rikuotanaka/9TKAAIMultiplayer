import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GeneralController {

    public void changeScene(Node accessor, String fxml, String windowTitle, int width, int height) throws Exception{
        Stage stage = (Stage) accessor.getScene().getWindow();
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        Scene scene = new Scene(pane,width,height);
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.hide();
        stage.show();
        //

    }

    public void changeScene(Node accessor, String fxml, String windowTitle) throws Exception {
        Stage stage = (Stage) accessor.getScene().getWindow();
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stage.getScene().setRoot(pane);
        stage.setTitle(windowTitle);
    }
}
