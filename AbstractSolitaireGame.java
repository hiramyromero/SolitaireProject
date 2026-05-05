import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the shared game logic for both manual and automated solitaire.
 * It stores the board, checks moves, randomizes the board,
 * and determines when the game is over.
 */
public abstract class AbstractSolitaireGame {

    public enum BoardType {
        ENGLISH,
        DIAMOND
    }

    protected BoardType boardType;
    protected int boardSize;
    protected boolean diagonalEnabled;

    // validHole[r][c] = true means that cell is part of the board
    // hasPeg[r][c]    = true means that cell currently has a peg
    protected boolean[][] validHole;
    protected boolean[][] hasPeg;

    protected int moveCount;
    protected Random rand = new Random();

    public AbstractSolitaireGame(BoardType boardType, int boardSize, boolean diagonalEnabled) {
        this.boardType = boardType;
        this.boardSize = boardSize;
        this.diagonalEnabled = diagonalEnabled;

        validHole = new boolean[boardSize][boardSize];
        hasPeg = new boolean[boardSize][boardSize];

        buildBoardShape();
        initPegsToStartingPosition();
        moveCount = 0;
    }

    // Build board shape based on boardType + size
    private void buildBoardShape() {
        if (boardType == BoardType.ENGLISH) {
            buildEnglishShape();
        } else {
            buildDiamondShape();
        }
    }

    // English cross shape
    private void buildEnglishShape() {
        int band = boardSize / 3;

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                boolean valid = !((r < band || r >= boardSize - band) &&
                                  (c < band || c >= boardSize - band));
                validHole[r][c] = valid;
            }
        }
    }

    // Diamond shape using Manhattan distance from center
    private void buildDiamondShape() {
        int mid = boardSize / 2;

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                validHole[r][c] = Math.abs(r - mid) + Math.abs(c - mid) <= mid;
            }
        }
    }

    // Starting layout: all valid holes get pegs except center
    public void initPegsToStartingPosition() {
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                hasPeg[r][c] = validHole[r][c];
            }
        }

        int mid = boardSize / 2;
        hasPeg[mid][mid] = false;
    }

    // Restart same game settings
    public void restartGame() {
        initPegsToStartingPosition();
        moveCount = 0;
    }

    // Randomize board state
    public void randomizeBoard() {
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                if (validHole[r][c]) {
                    hasPeg[r][c] = rand.nextBoolean();
                } else {
                    hasPeg[r][c] = false;
                }
            }
        }

        // Make sure there is at least one empty valid hole
        int mid = boardSize / 2;
        if (validHole[mid][mid]) {
            hasPeg[mid][mid] = false;
        }

        moveCount = 0;
    }

    // Try to make a move
    public boolean tryMove(int fromR, int fromC, int toR, int toC) {
        if (!inBounds(fromR, fromC) || !inBounds(toR, toC)) return false;
        if (!validHole[fromR][fromC] || !validHole[toR][toC]) return false;
        if (!hasPeg[fromR][fromC]) return false;
        if (hasPeg[toR][toC]) return false;

        int dr = toR - fromR;
        int dc = toC - fromC;

        boolean isOrthogonalJump =
                (Math.abs(dr) == 2 && dc == 0) ||
                (Math.abs(dc) == 2 && dr == 0);

        boolean isDiagonalJump =
                diagonalEnabled && (Math.abs(dr) == 2 && Math.abs(dc) == 2);

        if (!isOrthogonalJump && !isDiagonalJump) return false;

        int midR = fromR + dr / 2;
        int midC = fromC + dc / 2;

        if (!inBounds(midR, midC)) return false;
        if (!validHole[midR][midC]) return false;
        if (!hasPeg[midR][midC]) return false;

        // Execute move
        hasPeg[fromR][fromC] = false;
        hasPeg[midR][midC] = false;
        hasPeg[toR][toC] = true;
        moveCount++;

        return true;
    }

    // Check if game is over
    public boolean isGameOver() {
        return getAllLegalMoves().isEmpty();
    }

    // Return all legal moves (used by automated game too)
    public List<Move> getAllLegalMoves() {
        List<Move> moves = new ArrayList<>();

        int[][] dirs = diagonalEnabled
                ? new int[][]{
                    {-2, 0}, {2, 0}, {0, -2}, {0, 2},
                    {-2, -2}, {-2, 2}, {2, -2}, {2, 2}
                }
                : new int[][]{
                    {-2, 0}, {2, 0}, {0, -2}, {0, 2}
                };

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                if (!validHole[r][c] || !hasPeg[r][c]) continue;

                for (int[] d : dirs) {
                    int toR = r + d[0];
                    int toC = c + d[1];

                    if (canMove(r, c, toR, toC)) {
                        moves.add(new Move(r, c, toR, toC));
                    }
                }
            }
        }

        return moves;
    }

    private boolean canMove(int fromR, int fromC, int toR, int toC) {
        if (!inBounds(fromR, fromC) || !inBounds(toR, toC)) return false;
        if (!validHole[fromR][fromC] || !validHole[toR][toC]) return false;
        if (!hasPeg[fromR][fromC]) return false;
        if (hasPeg[toR][toC]) return false;

        int dr = toR - fromR;
        int dc = toC - fromC;

        boolean isOrthogonalJump =
                (Math.abs(dr) == 2 && dc == 0) ||
                (Math.abs(dc) == 2 && dr == 0);

        boolean isDiagonalJump =
                diagonalEnabled && (Math.abs(dr) == 2 && Math.abs(dc) == 2);

        if (!isOrthogonalJump && !isDiagonalJump) return false;

        int midR = fromR + dr / 2;
        int midC = fromC + dc / 2;

        return inBounds(midR, midC) &&
               validHole[midR][midC] &&
               hasPeg[midR][midC];
    }

    protected boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < boardSize && c < boardSize;
    }

    public int countPegs() {
        int count = 0;
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                if (validHole[r][c] && hasPeg[r][c]) count++;
            }
        }
        return count;
    }

    public boolean[][] getValidHole() {
        return validHole;
    }

    public boolean[][] getHasPeg() {
        return hasPeg;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setDiagonalEnabled(boolean diagonalEnabled) {
        this.diagonalEnabled = diagonalEnabled;
    }

    public boolean isDiagonalEnabled() {
        return diagonalEnabled;
    }

    // Small helper class to represent a move
    public static class Move {
        public final int fromR;
        public final int fromC;
        public final int toR;
        public final int toC;

        public Move(int fromR, int fromC, int toR, int toC) {
            this.fromR = fromR;
            this.fromC = fromC;
            this.toR = toR;
            this.toC = toC;
        }
    }
}
//test print