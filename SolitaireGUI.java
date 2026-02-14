import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class SolitaireGUI extends Application {

    private SolitaireGame game = new SolitaireGame();
    private Label status = new Label("Status: Ready");

    @Override
    public void start(Stage stage) {

        Label title = new Label("Peg Solitaire GUI");

        // Required: Line
        Line divider = new Line(0, 0, 300, 0);

        // Required: CheckBox
        CheckBox diagonalCheck = new CheckBox("Enable Diagonal Moves");
        diagonalCheck.setOnAction(e -> {
            game.setDiagonalEnabled(diagonalCheck.isSelected());
            status.setText("Diagonal Enabled: " + game.isDiagonalEnabled());
        });

        // Required: RadioButtons
        ToggleGroup difficultyGroup = new ToggleGroup();

        RadioButton easy = new RadioButton("Easy");
        RadioButton medium = new RadioButton("Medium");
        RadioButton hard = new RadioButton("Hard");

        easy.setToggleGroup(difficultyGroup);
        medium.setToggleGroup(difficultyGroup);
        hard.setToggleGroup(difficultyGroup);

        medium.setSelected(true);

        Button addButton = new Button("Add Point");
        addButton.setOnAction(e -> {
            game.addPoint();
            status.setText("Score: " + game.getScore());
        });

        VBox root = new VBox(10,
                title,
                divider,
                diagonalCheck,
                easy,
                medium,
                hard,
                addButton,
                status
        );

        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("Solitaire Starter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

