public class SolitaireGame {

    private int score;
    private boolean diagonalEnabled;

    public SolitaireGame() {
        score = 0;
        diagonalEnabled = false;
    }

    public int getScore() {
        return score;
    }

    public void addPoint() {
        score++;
    }

    public void resetScore() {
        score = 0;
    }

    public boolean isDiagonalEnabled() {
        return diagonalEnabled;
    }

    public void setDiagonalEnabled(boolean value) {
        diagonalEnabled = value;
    }
}
