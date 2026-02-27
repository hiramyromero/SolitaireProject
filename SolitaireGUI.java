// SolitaireGUI.java
// This file holds my JavaFX GUI + the basic game logic needed for Peg Solitaire.
// Goal of this version:
// - Let user choose board type/size (English 7x7 / European 9x9)
// - Start a new game + restart current game
// - Make a move by clicking a peg then clicking an empty hole
// - Detect if the game is over (no valid moves left)
// - Optional: allow diagonal moves (checkbox)
// TEST COMMIT

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

    // Board types we support (board type decides the board size + shape)
    private enum BoardType {
        ENGLISH,   // 7x7 cross board
        EUROPEAN   // 9x9 cross board
    }

    // --- GUI pieces I need to access later (so I store them as fields) ---
    private GridPane boardGrid;
    private Label statusLabel;
    private Label statsLabel;

    private CheckBox diagonalCheck;
    private ToggleGroup boardTypeGroup;
    private RadioButton englishBoard;
    private RadioButton europeanBoard;

    private Button newGameBtn;
    private Button restartBtn;

    // --- Board state (this is basically my “model” in memory) ---
    // validHole[r][c] = true means that (r,c) is an actual spot on the board.
    // hasPeg[r][c]   = true means there is a peg currently sitting there.
    private boolean[][] validHole;
    private boolean[][] hasPeg;

    // I store the actual button objects so I can update their text/style after moves.
    private CellButton[][] buttons;

    // Selection: user clicks a peg first (select), then clicks a destination hole (move).
    private int selR = -1;
    private int selC = -1;

    // Simple stats
    private BoardType currentType = BoardType.ENGLISH;
    private int moveCount = 0;

    @Override
    public void start(Stage stage) {

        // -------------------------------
        // Left panel (controls + labels)
        // -------------------------------

        // Title section
        Label title = new Label("Solitaire");
        title.setFont(Font.font(18));

        // Simple divider under the title (just to make it look nicer)
        Line divider = new Line(0, 0, 280, 0);

        // Checkbox to allow diagonal moves (this changes move rules)
        diagonalCheck = new CheckBox("Allow diagonal moves");

        // Radio buttons for board type choice
        boardTypeGroup = new ToggleGroup();

        englishBoard = new RadioButton("English Board (7x7)");
        englishBoard.setToggleGroup(boardTypeGroup);
        englishBoard.setSelected(true);

        europeanBoard = new RadioButton("European Board (9x9)");
        europeanBoard.setToggleGroup(boardTypeGroup);

        // Main buttons
        newGameBtn = new Button("New Game");
        restartBtn = new Button("Restart");

        // Put buttons side-by-side
        HBox buttonRow = new HBox(10, newGameBtn, restartBtn);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        // Status shows hints like “select a peg” or “invalid move”
        statusLabel = new Label("Select a peg, then select an empty hole.");
        statusLabel.setWrapText(true);

        // Stats shows moves + pegs left
        statsLabel = new Label("");
        statsLabel.setWrapText(true);

        // Full left panel layout
        VBox leftPanel = new VBox(12,
                title,
                divider,
                new Label("Options:"),
                diagonalCheck,
                englishBoard,
                europeanBoard,
                buttonRow,
                new Separator(),
                new Label("Status:"),
                statusLabel,
                new Separator(),
                new Label("Stats:"),
                statsLabel
        );
        leftPanel.setPadding(new Insets(12));
        leftPanel.setPrefWidth(240);

        // -------------------------------
        // Center panel (board grid)
        // -------------------------------

        // This is the container where I place my board buttons.
        boardGrid = new GridPane();
        boardGrid.setHgap(6);
        boardGrid.setVgap(6);
        boardGrid.setPadding(new Insets(12));
        boardGrid.setAlignment(Pos.CENTER);

        // Main layout: controls on the left, board in the center
        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        root.setCenter(boardGrid);

        // -------------------------------
        // Wire up actions (event handlers)
        // -------------------------------

        // If user changes board type, I start a new game with that board shape.
        boardTypeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) return;

            if (newT == englishBoard) currentType = BoardType.ENGLISH;
            else currentType = BoardType.EUROPEAN;

            startNewGame();
        });

        // New Game = rebuild board + reset stats
        newGameBtn.setOnAction(e -> startNewGame());

        // Restart = keep same board type but reset to starting layout
        restartBtn.setOnAction(e -> restartGame());

        // -------------------------------
        // Start my initial game
        // -------------------------------
        startNewGame();

        Scene scene = new Scene(root, 820, 520);
        stage.setTitle("Solitaire GUI");
        stage.setScene(scene);
        stage.show();
    }

    // -------------------------------
    // This starts a fresh game
    // - resets stats
    // - rebuilds board for currentType
    // - fills pegs to starting position
    // -------------------------------
    private void startNewGame() {
        moveCount = 0;
        clearSelection();

        buildBoardForType(currentType);
        refreshBoardUI();

        statusLabel.setText("New game started. Select a peg, then select an empty hole.");
        updateStats();

        // Just in case, check if the board has moves (normally it will)
        if (isGameOver()) {
            statusLabel.setText("Game over: no moves available.");
        }
    }

    // -------------------------------
    // Restart keeps the board type,
    // but resets pegs back to the starting arrangement.
    // -------------------------------
    private void restartGame() {
        moveCount = 0;
        clearSelection();

        initPegsToStartingPosition();
        refreshBoardUI();

        statusLabel.setText("Game restarted. Select a peg, then select an empty hole.");
        updateStats();

        if (isGameOver()) {
            statusLabel.setText("Game over: no moves available.");
        }
    }

    // -------------------------------
    // Size depends on board type:
    // English = 7x7, European = 9x9
    // -------------------------------
    private int sizeFor(BoardType type) {
        return (type == BoardType.ENGLISH) ? 7 : 9;
    }

    // -------------------------------
    // Build the board arrays and GUI buttons
    // validHole decides which coordinates actually exist on the board.
    // -------------------------------
    private void buildBoardForType(BoardType type) {
        boardGrid.getChildren().clear();

        int size = sizeFor(type);

        // Allocate arrays for this board size
        buttons = new CellButton[size][size];
        validHole = new boolean[size][size];
        hasPeg = new boolean[size][size];

        // -------------------------------
        // Build the board “mask” (cross shape)
        // - validHole[r][c] = true means that cell is part of the board
        // - invalid corners are excluded to make the cross
        // -------------------------------

        if (type == BoardType.ENGLISH) {
            // English 7x7:
            // invalid if (r < 2 || r > 4) AND (c < 2 || c > 4)
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    boolean valid = !((r < 2 || r > 4) && (c < 2 || c > 4));
                    validHole[r][c] = valid;
                }
            }
        } else {
            // European 9x9:
            // invalid if (r < 3 || r > 5) AND (c < 3 || c > 5)
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    boolean valid = !((r < 3 || r > 5) && (c < 3 || c > 5));
                    validHole[r][c] = valid;
                }
            }
        }

        // Fill pegs in starting positions (everything filled except the center)
        initPegsToStartingPosition();

        // -------------------------------
        // Create a button for each coordinate
        // - If the coordinate is invalid, we hide it later
        // - If it is valid, we show ● or ○ based on hasPeg
        // -------------------------------
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                CellButton cellBtn = new CellButton(r, c);
                cellBtn.setPrefSize(48, 48);
                cellBtn.setStyle(baseCellStyle());

                // When clicked, try selection/move logic
                cellBtn.setOnAction(e -> onCellClicked(cellBtn.row, cellBtn.col));

                buttons[r][c] = cellBtn;
                boardGrid.add(cellBtn, c, r);
            }
        }
    }

    // -------------------------------
    // Starting layout for peg solitaire:
    // - All valid holes contain pegs
    // - Center hole is empty
    // -------------------------------
    private void initPegsToStartingPosition() {
        int size = validHole.length;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                hasPeg[r][c] = validHole[r][c];
            }
        }

        // Center is empty at start
        int mid = size / 2;
        hasPeg[mid][mid] = false;
    }

    // -------------------------------
    // Click behavior:
    // 1) If nothing selected: must click a peg to select it.
    // 2) If clicking selected peg again: deselect.
    // 3) If clicking a different peg: switch selection.
    // 4) If clicking an empty hole: attempt a move.
    // -------------------------------
    private void onCellClicked(int r, int c) {
        if (!validHole[r][c]) return;

        // If game is over, stop moves and tell the user.
        if (isGameOver()) {
            statusLabel.setText("Game over: no moves available. Start a New Game or Restart.");
            return;
        }

        // (1) No selection yet: user must choose a peg first
        if (selR == -1) {
            if (!hasPeg[r][c]) {
                statusLabel.setText("That hole is empty. Select a peg first.");
                return;
            }
            setSelection(r, c);
            statusLabel.setText("Peg selected. Now click a destination empty hole.");
            return;
        }

        // (2) Clicking the same peg again clears selection
        if (r == selR && c == selC) {
            clearSelection();
            statusLabel.setText("Selection cleared. Select a peg.");
            return;
        }

        // (3) If they click a different peg, just switch selection
        if (hasPeg[r][c]) {
            setSelection(r, c);
            statusLabel.setText("Switched selection. Now click a destination empty hole.");
            return;
        }

        // (4) They clicked an empty hole: attempt the jump move
        if (tryMove(selR, selC, r, c)) {
            moveCount++;
            clearSelection();
            refreshBoardUI();

            if (isGameOver()) {
                statusLabel.setText("Move made. Game over: no moves available.");
            } else {
                statusLabel.setText("Move made. Select a peg for the next move.");
            }

            updateStats();
        } else {
            statusLabel.setText("Invalid move. Jump over exactly one peg into an empty hole.");
        }
    }

    // -------------------------------
    // Attempt a move:
    // - Move is a “jump” of 2 spaces
    // - Middle spot must contain a peg (the one we remove)
    // - Destination must be empty
    // - Orthogonal moves always allowed; diagonal only if checkbox enabled
    // -------------------------------
    private boolean tryMove(int fromR, int fromC, int toR, int toC) {
        if (!validHole[fromR][fromC] || !validHole[toR][toC]) return false;
        if (!hasPeg[fromR][fromC]) return false;
        if (hasPeg[toR][toC]) return false;

        int dr = toR - fromR;
        int dc = toC - fromC;

        boolean allowDiagonal = diagonalCheck.isSelected();

        boolean isOrthogonalJump =
                (Math.abs(dr) == 2 && dc == 0) ||
                (Math.abs(dc) == 2 && dr == 0);

        boolean isDiagonalJump =
                allowDiagonal && (Math.abs(dr) == 2 && Math.abs(dc) == 2);

        // Must be exactly a 2-cell jump in a legal direction
        if (!isOrthogonalJump && !isDiagonalJump) return false;

        // Middle cell is halfway between from and to
        int midR = fromR + dr / 2;
        int midC = fromC + dc / 2;

        // Middle must be valid AND must have a peg to jump over
        if (!validHole[midR][midC]) return false;
        if (!hasPeg[midR][midC]) return false;

        // Execute move:
        // - from becomes empty
        // - middle peg is removed
        // - destination becomes peg
        hasPeg[fromR][fromC] = false;
        hasPeg[midR][midC] = false;
        hasPeg[toR][toC] = true;

        return true;
    }

    // -------------------------------
    // Game over = no valid moves exist.
    // I scan every peg and see if it can jump somewhere.
    // -------------------------------
    private boolean isGameOver() {
        int size = validHole.length;
        boolean allowDiagonal = diagonalCheck.isSelected();

        // Directions are “2 cells away” because a jump is length 2
        int[][] dirs = allowDiagonal
                ? new int[][]{
                    {-2, 0}, {2, 0}, {0, -2}, {0, 2},
                    {-2, -2}, {-2, 2}, {2, -2}, {2, 2}
                }
                : new int[][]{
                    {-2, 0}, {2, 0}, {0, -2}, {0, 2}
                };

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                // Only consider real holes that currently have pegs
                if (!validHole[r][c] || !hasPeg[r][c]) continue;

                for (int[] d : dirs) {
                    int toR = r + d[0];
                    int toC = c + d[1];

                    // Destination must be on board and empty
                    if (!inBounds(toR, toC)) continue;
                    if (!validHole[toR][toC]) continue;
                    if (hasPeg[toR][toC]) continue;

                    // Middle must have a peg
                    int midR = r + d[0] / 2;
                    int midC = c + d[1] / 2;

                    if (inBounds(midR, midC) && validHole[midR][midC] && hasPeg[midR][midC]) {
                        return false; // found at least one legal move
                    }
                }
            }
        }

        return true; // no moves were found anywhere
    }

    // Bounds check helper
    private boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < validHole.length && c < validHole.length;
    }

    // -------------------------------
    // Refresh the UI text/styling to match current board state.
    // ● = peg, ○ = empty hole
    // Also hides invalid cells so the board looks like a cross.
    // -------------------------------
    private void refreshBoardUI() {
        int size = validHole.length;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                CellButton b = buttons[r][c];

                // If not part of board: hide it
                if (!validHole[r][c]) {
                    b.setDisable(true);
                    b.setText("");
                    b.setVisible(false);
                    continue;
                }

                // Otherwise show it
                b.setVisible(true);
                b.setDisable(false);

                // Display peg vs empty
                b.setText(hasPeg[r][c] ? "●" : "○");

                // Highlight selected peg
                if (r == selR && c == selC) {
                    b.setStyle(selectedCellStyle());
                } else {
                    b.setStyle(baseCellStyle());
                }
            }
        }
    }

    // Set selection then refresh UI to show the highlight on the selected peg.
    private void setSelection(int r, int c) {
        selR = r;
        selC = c;
        refreshBoardUI();
    }

    // Clear selection then refresh UI
    private void clearSelection() {
        selR = -1;
        selC = -1;
        if (buttons != null) refreshBoardUI();
    }

    // -------------------------------
    // Stats helpers (moves + pegs left)
    // -------------------------------
    private void updateStats() {
        statsLabel.setText("Moves: " + moveCount + "\nPegs remaining: " + countPegs());
    }

    private int countPegs() {
        int size = validHole.length;
        int count = 0;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (validHole[r][c] && hasPeg[r][c]) count++;
            }
        }
        return count;
    }

    // -------------------------------
    // Simple styling for buttons.
    // (Later I can move this into a CSS file.)
    // -------------------------------
    private String baseCellStyle() {
        return "-fx-font-size: 18px; -fx-font-weight: bold;";
    }

    private String selectedCellStyle() {
        return "-fx-font-size: 18px; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 3px;";
    }

    // -------------------------------
    // Custom button class so each cell stores its own (row,col)
    // This makes click-handling easier.
    // -------------------------------
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