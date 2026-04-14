/**
 * This is where I store basic game state for Solitaire.
 * It keeps track of things like the score and whether diagonal moves are allowed.
 * 
 * It does NOT handle the board or game rules — that is done in other classes.
 */

// TEST


public class SolitaireGame {
// This class manages the game state, including the score and whether diagonal moves are enabled.
    private int score;
    private boolean diagonalEnabled;
// Constructor initializes the score to 0 and diagonal moves to disabled.
    public SolitaireGame() {
        score = 0;
        diagonalEnabled = false;
    }
// Getter for the current score.
    public int getScore() {
        return score;
    }
// Method to increment the score by 1.
    public void addPoint() {
        score++;
    }
// Method to reset the score back to 0.
    public void resetScore() {
        score = 0;
    }
// Getter for whether diagonal moves are enabled.
    public boolean isDiagonalEnabled() {
        return diagonalEnabled;
    }
// Setter to enable or disable diagonal moves.
    public void setDiagonalEnabled(boolean value) {
        diagonalEnabled = value;
    }
}
