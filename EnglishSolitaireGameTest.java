import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EnglishSolitaireGame.
 * This class contains unit tests to verify the functionality of the English Solitaire game,
 * including board setup, valid moves, peg placement, and game state checks.
 */
public class EnglishSolitaireGameTest {

    /**
     * Test that the board size is correctly set to 7.
     * The English Solitaire board is a 7x7 grid.
     */
    @Test
    void boardShouldBeSize7() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();
        assertEquals(7, game.getSize());
    }

    /**
     * Test that the English board has the correct shape.
     * The board should have invalid holes in the corners, a valid center,
     * and valid positions along the arms (top, bottom, left, right).
     */
    @Test
    void englishBoardShouldHaveCorrectShape() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();

        // Check that corners are invalid positions
        assertFalse(game.isValidHole(0, 0));
        assertFalse(game.isValidHole(0, 1));
        assertFalse(game.isValidHole(1, 0));
        assertFalse(game.isValidHole(6, 6));

        // Check that the center is a valid position
        assertTrue(game.isValidHole(3, 3));

        // Check that positions along the arms are valid
        assertTrue(game.isValidHole(3, 0));
        assertTrue(game.isValidHole(3, 6));
        assertTrue(game.isValidHole(0, 3));
        assertTrue(game.isValidHole(6, 3));
    }

    /**
     * Test that the center hole starts empty (no peg).
     * In the initial setup, the center position should not have a peg.
     */
    @Test
    void centerShouldStartEmpty() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();
        assertFalse(game.hasPeg(3, 3));
    }

    /**
     * Test that all valid holes except the center start with pegs.
     * The board should be fully populated with pegs in all valid positions except the center.
     */
    @Test
    void validHolesExceptCenterShouldStartWithPegs() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();

        // Check some positions around the center that should have pegs
        assertTrue(game.hasPeg(3, 1));
        assertTrue(game.hasPeg(3, 2));
        assertTrue(game.hasPeg(2, 3));
        assertTrue(game.hasPeg(4, 3));
    }

    /**
     * Test that a valid move works correctly.
     * Moving a peg from (3,1) to (3,3) should remove the jumped peg and place the peg in the destination.
     */
    @Test
    void validMoveShouldWork() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();

        boolean moved = game.tryMove(3, 1, 3, 3);

        assertTrue(moved);
        assertFalse(game.hasPeg(3, 1)); // Source position should now be empty
        assertFalse(game.hasPeg(3, 2)); // Jumped position should be empty
        assertTrue(game.hasPeg(3, 3));  // Destination should now have a peg
    }

    /**
     * Test that an invalid move fails.
     * Attempting to move to an adjacent position (not a jump) should not be allowed.
     */
    @Test
    void invalidMoveShouldFail() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();

        boolean moved = game.tryMove(3, 1, 3, 2); // Only one space, not a valid jump
        assertFalse(moved);
    }

    /**
     * Test that a new board is not in a game over state.
     * At the start of the game, there should be valid moves available.
     */
    @Test
    void newBoardShouldNotBeGameOver() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();
        assertFalse(game.isGameOver());
    }

    /**
     * Test that the starting board has the correct number of pegs.
     * A standard English Solitaire board starts with 32 pegs.
     */
    @Test
    void startingBoardShouldHave32Pegs() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();
        assertEquals(32, game.countPegs());
    }

    /**
     * Test that the peg count decreases by one after a valid move.
     * A successful jump removes one peg from the board.
     */
    @Test
    void pegCountShouldDecreaseAfterMove() {
        EnglishSolitaireGame game = new EnglishSolitaireGame();

        int beforeMove = game.countPegs();

        // Perform a valid move
        boolean moved = game.tryMove(3, 1, 3, 3);

        int afterMove = game.countPegs();

        assertTrue(moved);
        assertEquals(beforeMove - 1, afterMove);
    }
}