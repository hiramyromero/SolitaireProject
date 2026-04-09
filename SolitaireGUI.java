// SolitaireGUI.java
// This is where the game is shown on screen.
// It handles the UI (buttons, labels, clicks) and works with either
// a manual game object or an automated game object.

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SolitaireGUI extends Application {

    // --- GUI pieces I need to access later ---
    private GridPane boardGrid;
    private Label statusLabel;
    private Label statsLabel;

    private CheckBox diagonalCheck;

    private ToggleGroup boardTypeGroup;
    private RadioButton englishBoard;
    private RadioButton diamondBoard;

    private ToggleGroup modeGroup;
    private RadioButton manualMode;
    private RadioButton automatedMode;

    private ComboBox<Integer> boardSizeCombo;

    private Button newGameBtn;
    private Button restartBtn;
    private Button randomizeBtn;
    private Button autoMoveBtn;

    // The current game object (manual or automated)
    private AbstractSolitaireGame game;

    // I store the actual button objects so I can update their text/style after moves.
    private CellButton[][] buttons;

    // Selection for manual game
    private int selR = -1;
    private int selC = -1;

    @Override
    public void start(Stage stage) {

        // -------------------------------
        // Left panel (controls + labels)
        // -------------------------------

        Label title = new Label("Solitaire GUI");
        title.setFont(Font.font(18));

        Line divider = new Line(0, 0, 280, 0);

        // Game mode
        modeGroup = new ToggleGroup();

        manualMode = new RadioButton("Manual");
        manualMode.setToggleGroup(modeGroup);
        manualMode.setSelected(true);

        automatedMode = new RadioButton("Automated");
        automatedMode.setToggleGroup(modeGroup);

        // Board type
        boardTypeGroup = new ToggleGroup();

        englishBoard = new RadioButton("English");
        englishBoard.setToggleGroup(boardTypeGroup);
        englishBoard.setSelected(true);

        diamondBoard = new RadioButton("Diamond");
        diamondBoard.setToggleGroup(boardTypeGroup);

        // Board size
        boardSizeCombo = new ComboBox<>();
        boardSizeCombo.getItems().addAll(7, 9);
        boardSizeCombo.setValue(7);

        // Diagonal moves
        diagonalCheck = new CheckBox("Allow diagonal moves");

        // Buttons
        newGameBtn = new Button("New Game");
        restartBtn = new Button("Restart");
        randomizeBtn = new Button("Randomize");
        autoMoveBtn = new Button("Auto Move");

        HBox buttonRow1 = new HBox(10, newGameBtn, restartBtn);
        buttonRow1.setAlignment(Pos.CENTER_LEFT);

        HBox buttonRow2 = new HBox(10, randomizeBtn, autoMoveBtn);
        buttonRow2.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("Choose your game settings and start.");
        statusLabel.setWrapText(true);

        statsLabel = new Label("");
        statsLabel.setWrapText(true);

        VBox leftPanel = new VBox(12,
            title,
            divider,
            new Label("Game Mode:"),
            manualMode,
            automatedMode,
            new Separator(),
            new Label("Board Size:"),
            boardSizeCombo,
            new Separator(),
            new Label("Board Type:"),
            englishBoard,
            diamondBoard,
            diagonalCheck,
            buttonRow1,
            buttonRow2,
            new Separator(),
            new Label("Status:"),
            statusLabel,
            new Separator(),
            new Label("Stats:"),
            statsLabel
        );
        leftPanel.setPadding(new Insets(12));
        leftPanel.setPrefWidth(260);

        // -------------------------------
        // Center panel (board grid)
        // -------------------------------

        boardGrid = new GridPane();
        boardGrid.setHgap(6);
        boardGrid.setVgap(6);
        boardGrid.setPadding(new Insets(12));
        boardGrid.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        root.setCenter(boardGrid);

        // -------------------------------
        // Wire up actions
        // -------------------------------

        newGameBtn.setOnAction(e -> startNewGame());

        restartBtn.setOnAction(e -> restartGame());

        randomizeBtn.setOnAction(e -> randomizeBoard());

        autoMoveBtn.setOnAction(e -> makeAutoMove());

        // Start initial game
        startNewGame();

        Scene scene = new Scene(root, 980, 620);
        stage.setTitle("Solitaire GUI");
        stage.setScene(scene);
        stage.show();
    }

    // Create a fresh game object based on current controls
    private void startNewGame() {
        clearSelection();

        AbstractSolitaireGame.BoardType selectedBoardType =
                englishBoard.isSelected()
                        ? AbstractSolitaireGame.BoardType.ENGLISH
                        : AbstractSolitaireGame.BoardType.DIAMOND;

        int selectedSize = boardSizeCombo.getValue();
        boolean allowDiagonal = diagonalCheck.isSelected();

        if (manualMode.isSelected()) {
            game = new ManualSolitaireGame(selectedBoardType, selectedSize, allowDiagonal);
        } else {
            game = new AutomatedSolitaireGame(selectedBoardType, selectedSize, allowDiagonal);
        }

        buildBoardButtons();
        refreshBoardUI();
        updateStats();

        if (manualMode.isSelected()) {
            statusLabel.setText("New manual game started. Select a peg, then select an empty hole.");
        } else {
            statusLabel.setText("New automated game started. Click Auto Move to let the game play.");
        }

        if (game.isGameOver()) {
            statusLabel.setText("Game over: no moves available.");
        }
    }

    // Restart same settings
    private void restartGame() {
        if (game == null) return;

        clearSelection();
        game.setDiagonalEnabled(diagonalCheck.isSelected());
        game.restartGame();
        refreshBoardUI();
        updateStats();

        if (manualMode.isSelected()) {
            statusLabel.setText("Game restarted. Select a peg, then select an empty hole.");
        } else {
            statusLabel.setText("Automated game restarted. Click Auto Move to continue.");
        }

        if (game.isGameOver()) {
            statusLabel.setText("Game over: no moves available.");
        }
    }

    // Randomize state of board
    private void randomizeBoard() {
        if (game == null) return;

        clearSelection();
        game.setDiagonalEnabled(diagonalCheck.isSelected());
        game.randomizeBoard();
        refreshBoardUI();
        updateStats();

        if (game.isGameOver()) {
            statusLabel.setText("Board randomized. Game over: no legal moves.");
        } else {
            statusLabel.setText("Board randomized.");
        }
    }

    // Make one automated move
    private void makeAutoMove() {
        if (game == null) return;

        if (!(game instanceof AutomatedSolitaireGame autoGame)) {
            statusLabel.setText("Auto Move only works in Automated mode.");
            return;
        }

        autoGame.setDiagonalEnabled(diagonalCheck.isSelected());

        boolean moved = autoGame.makeAutomatedMove();
        refreshBoardUI();
        updateStats();

        if (!moved) {
            statusLabel.setText("Automated game is over. No legal moves.");
        } else if (autoGame.isGameOver()) {
            statusLabel.setText("Automated move made. Game over.");
        } else {
            statusLabel.setText("Automated move made.");
        }
    }

    // Build button grid to match current game size
    private void buildBoardButtons() {
        boardGrid.getChildren().clear();

        int size = game.getBoardSize();
        buttons = new CellButton[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                CellButton cellBtn = new CellButton(r, c);
                cellBtn.setPrefSize(48, 48);
                cellBtn.setStyle(baseCellStyle());

                // Clicks matter only in manual mode
                cellBtn.setOnAction(e -> onCellClicked(cellBtn.row, cellBtn.col));

                buttons[r][c] = cellBtn;
                boardGrid.add(cellBtn, c, r);
            }
        }
    }

    // Manual click behavior
    private void onCellClicked(int r, int c) {
        if (game == null) return;

        // Board clicks only work in manual mode
        if (!(game instanceof ManualSolitaireGame)) {
            statusLabel.setText("Board clicks are only for Manual mode.");
            return;
        }

        boolean[][] validHole = game.getValidHole();
        boolean[][] hasPeg = game.getHasPeg();

        if (!validHole[r][c]) return;

        if (game.isGameOver()) {
            statusLabel.setText("Game over: no moves available. Start a New Game or Restart.");
            return;
        }

        // No peg selected yet
        if (selR == -1) {
            if (!hasPeg[r][c]) {
                statusLabel.setText("That hole is empty. Select a peg first.");
                return;
            }

            setSelection(r, c);
            statusLabel.setText("Peg selected. Now click a destination empty hole.");
            return;
        }

        // Clicking same peg again clears selection
        if (r == selR && c == selC) {
            clearSelection();
            statusLabel.setText("Selection cleared. Select a peg.");
            return;
        }

        // Clicking another peg switches selection
        if (hasPeg[r][c]) {
            setSelection(r, c);
            statusLabel.setText("Switched selection. Now click a destination empty hole.");
            return;
        }

        // Clicking empty hole tries a move
        game.setDiagonalEnabled(diagonalCheck.isSelected());

        if (game.tryMove(selR, selC, r, c)) {
            clearSelection();
            refreshBoardUI();
            updateStats();

            if (game.isGameOver()) {
                statusLabel.setText("Move made. Game over: no moves available.");
            } else {
                statusLabel.setText("Move made. Select a peg for the next move.");
            }
        } else {
            statusLabel.setText("Invalid move. Jump over exactly one peg into an empty hole.");
        }
    }

    // Refresh board display from current game state
    private void refreshBoardUI() {
        if (game == null) return;

        boolean[][] validHole = game.getValidHole();
        boolean[][] hasPeg = game.getHasPeg();
        int size = game.getBoardSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                CellButton b = buttons[r][c];

                if (!validHole[r][c]) {
                    b.setDisable(true);
                    b.setText("");
                    b.setVisible(false);
                    continue;
                }

                b.setVisible(true);
                b.setDisable(false);

                b.setText(hasPeg[r][c] ? "●" : "○");

                if (r == selR && c == selC) {
                    b.setStyle(selectedCellStyle());
                } else {
                    b.setStyle(baseCellStyle());
                }
            }
        }
    }

    private void setSelection(int r, int c) {
        selR = r;
        selC = c;
        refreshBoardUI();
    }

    private void clearSelection() {
        selR = -1;
        selC = -1;
        if (buttons != null) refreshBoardUI();
    }

    // Stats
    private void updateStats() {
        if (game == null) return;

        String modeText = (game instanceof AutomatedSolitaireGame) ? "Automated" : "Manual";
        String typeText = game.getBoardType() == AbstractSolitaireGame.BoardType.ENGLISH ? "English" : "Diamond";

        statsLabel.setText(
            "Mode: " + modeText +
            "\nBoard Size: " + game.getBoardSize() +
            "\nBoard Type: " + typeText +
            "\nMoves: " + game.getMoveCount() +
            "\nPegs remaining: " + game.countPegs()
        );
    }

    // Simple styles
    private String baseCellStyle() {
        return "-fx-font-size: 18px; -fx-font-weight: bold;";
    }

    private String selectedCellStyle() {
        return "-fx-font-size: 18px; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 3px;";
    }

    // Custom board button
    private static class CellButton extends Button {
        final int row;
        final int col;

        CellButton(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}