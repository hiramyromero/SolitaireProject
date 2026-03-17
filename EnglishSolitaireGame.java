// Represents the English Solitaire game board, which is a 7x7 grid with a cross shape.
// The center hole starts empty, and players jump pegs to remove them, aiming to leave one peg.
public class EnglishSolitaireGame {
    // The size of the board (7x7 for English Solitaire)
    private final int SIZE = 7;
    // 2D array indicating which positions are valid holes (part of the cross shape)
    private boolean[][] validHole;
    // 2D array indicating which valid holes currently have pegs
    private boolean[][] hasPeg;

    // Constructor: initializes the game by setting up the board
    public EnglishSolitaireGame() {
        setupBoard();
    }

    // Sets up the board: creates the cross shape for valid holes and places pegs in all valid holes except the center
    public void setupBoard() {
        validHole = new boolean[SIZE][SIZE];
        hasPeg = new boolean[SIZE][SIZE];

        // English board shape: a cross where rows/cols 2-4 are always valid, but corners are invalid
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                // A hole is valid if it's not in the corners (where both row and col are outside 2-4)
                boolean valid = !((r < 2 || r > 4) && (c < 2 || c > 4));
                validHole[r][c] = valid;
                hasPeg[r][c] = valid;  // Start with pegs in all valid holes
            }
        }

        // Center starts empty (no peg)
        hasPeg[3][3] = false;
    }

    // Returns the size of the board (always 7 for English Solitaire)
    public int getSize() {
        return SIZE;
    }

    // Checks if the given position is a valid hole on the board
    public boolean isValidHole(int r, int c) {
        return validHole[r][c];
    }

    // Checks if there is a peg at the given position
    public boolean hasPeg(int r, int c) {
        return hasPeg[r][c];
    }

    // Attempts to move a peg from (fromR, fromC) to (toR, toC) by jumping over another peg.
    // Returns true if the move is valid and performed, false otherwise.
    public boolean tryMove(int fromR, int fromC, int toR, int toC) {
        // Check if both positions are within the board bounds
        if (!inBounds(fromR, fromC) || !inBounds(toR, toC)) return false;
        // Check if both positions are valid holes
        if (!validHole[fromR][fromC] || !validHole[toR][toC]) return false;
        // Check if there's a peg at the starting position
        if (!hasPeg[fromR][fromC]) return false;
        // Check if the destination is empty
        if (hasPeg[toR][toC]) return false;

        // Calculate the direction of the move
        int dr = toR - fromR;
        int dc = toC - fromC;

        // Check if it's an orthogonal jump (exactly 2 spaces in one direction)
        boolean isOrthogonalJump =
                (Math.abs(dr) == 2 && dc == 0) ||
                (Math.abs(dc) == 2 && dr == 0);

        if (!isOrthogonalJump) return false;

        // Calculate the middle position that must be jumped over
        int midR = fromR + dr / 2;
        int midC = fromC + dc / 2;

        // Check if the middle position is a valid hole and has a peg
        if (!validHole[midR][midC]) return false;
        if (!hasPeg[midR][midC]) return false;

        // Perform the move: remove peg from start and middle, add to destination
        hasPeg[fromR][fromC] = false;
        hasPeg[midR][midC] = false;
        hasPeg[toR][toC] = true;

        return true;
    }

    // Checks if the game is over by seeing if any valid moves are possible.
    // Returns true if no moves can be made, false otherwise.
    public boolean isGameOver() {
        // Define the four possible jump directions: up, down, left, right (each 2 spaces)
        int[][] dirs = {
            {-2, 0}, {2, 0}, {0, -2}, {0, 2}
        };

        // Loop through every position on the board
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                // Skip if not a valid hole or no peg here
                if (!validHole[r][c] || !hasPeg[r][c]) continue;

                // Check each possible jump direction from this position
                for (int[] d : dirs) {
                    int toR = r + d[0];
                    int toC = c + d[1];

                    // Skip if destination is out of bounds
                    if (!inBounds(toR, toC)) continue;
                    // Skip if destination is not a valid hole
                    if (!validHole[toR][toC]) continue;
                    // Skip if destination already has a peg
                    if (hasPeg[toR][toC]) continue;

                    // Calculate the middle position
                    int midR = r + d[0] / 2;
                    int midC = c + d[1] / 2;

                    // If middle has a peg and is valid, then a move is possible
                    if (validHole[midR][midC] && hasPeg[midR][midC]) {
                        return false;  // Game not over, move available
                    }
                }
            }
        }

        return true;  // No moves possible, game over
    }

    // Counts the number of pegs currently on the board (in valid holes)
    public int countPegs() {
        int count = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (validHole[r][c] && hasPeg[r][c]) {
                    count++;
                }
            }
        }
        return count;
    }

    // Helper method: checks if the given row and column are within the board bounds
    private boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < SIZE && c < SIZE;
    }
}