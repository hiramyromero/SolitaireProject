import java.util.List;

/**
 * This is the automated version of solitaire.
 * The program can choose and perform legal moves by itself.
 */
public class AutomatedSolitaireGame extends AbstractSolitaireGame {

    public AutomatedSolitaireGame(BoardType boardType, int boardSize, boolean diagonalEnabled) {
        super(boardType, boardSize, diagonalEnabled);
    }

    // This returns a random legal move without making it yet.
    // The GUI uses this so it can record the move before/after it happens.
    public Move getRandomMove() {
        List<Move> legalMoves = getAllLegalMoves();

        if (legalMoves.isEmpty()) {
            return null;
        }

        return legalMoves.get(rand.nextInt(legalMoves.size()));
    }

    // Pick a random legal move and make it
    public boolean makeAutomatedMove() {
        Move chosen = getRandomMove();

        if (chosen == null) {
            return false;
        }

        return tryMove(chosen.fromR, chosen.fromC, chosen.toR, chosen.toC);
    }
}