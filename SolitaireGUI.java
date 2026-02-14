// libraries I will be needing for this GUI
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SolitaireGUI extends Application {

    // board size for English solitaire
    private static final int SIZE = 7;

    @Override
    public void start(Stage stage) {

        // title section
        Label title = new Label("Solitaire GUI");
        title.setFont(Font.font(18));

        // simple divider under the title
        Line divider = new Line(0, 0, 280, 0);


        // left side controls

        // checkbox goes here (not wired yet)
        CheckBox diagonalCheck = new CheckBox("Allow diagonal moves");

        // radio button setup
        ToggleGroup group = new ToggleGroup();
        RadioButton englishBoard = new RadioButton("English Board");
        englishBoard.setToggleGroup(group);
        englishBoard.setSelected(true);

        // basic buttons
        Button newGame = new Button("New Game");
        Button restart = new Button("Restart");

        // this just holds the two buttons side by side
        HBox buttonRow = new HBox(10, newGame, restart);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        // full left panel layout
        VBox leftPanel = new VBox(12,
                title,
                divider,
                diagonalCheck,
                englishBoard,
                buttonRow
        );
        leftPanel.setPadding(new Insets(12));
        leftPanel.setPrefWidth(220);


        // where my board is built

        GridPane boardGrid = new GridPane();
        boardGrid.setHgap(6);
        boardGrid.setVgap(6);
        boardGrid.setPadding(new Insets(12));
        boardGrid.setAlignment(Pos.CENTER);

        // building the English board shape (cross layout)
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {

                // skip corner blocks so it forms the cross shape
                if ((r < 2 || r > 4) && (c < 2 || c > 4)) {
                    continue;
                }

                Button cell;

                // center hole should look empty
                if (r == 3 && c == 3) {
                    cell = new Button("○");
                } else {
                    cell = new Button("●");
                }

                cell.setPrefSize(45, 45);
                cell.setDisable(true); // just display for now

                boardGrid.add(cell, c, r);
            }
        }


        // main layout setup

        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        root.setCenter(boardGrid);

        Scene scene = new Scene(root, 700, 420);

        stage.setTitle("Solitaire GUI");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
