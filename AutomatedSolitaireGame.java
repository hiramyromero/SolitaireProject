import java.util.List;

/**
 * This is the automated version of solitaire.
 * The program can choose and perform legal moves by itself.
 */
public class AutomatedSolitaireGame extends AbstractSolitaireGame {

    public AutomatedSolitaireGame(BoardType boardType, int boardSize, boolean diagonalEnabled) {
        super(boardType, boardSize, diagonalEnabled);
    }

    // Pick a random legal move and make it
    public boolean makeAutomatedMove() {
        List<Move> legalMoves = getAllLegalMoves();

        if (legalMoves.isEmpty()) {
            return false;
        }

        Move chosen = legalMoves.get(rand.nextInt(legalMoves.size()));
        return tryMove(chosen.fromR, chosen.fromC, chosen.toR, chosen.toC);
    }
}