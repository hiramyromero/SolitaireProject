import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class records the solitaire game into a text file.
 * It saves the game settings and every move that is made.
 */
public class GameRecorder {

    private PrintWriter writer;
    private boolean recording;

    public GameRecorder() {
        recording = false;
    }

    // Starts recording the current game settings
    public void startRecording(AbstractSolitaireGame game, String mode) {
        try {
            writer = new PrintWriter(new FileWriter("recorded_game.txt"));
            recording = true;

            writer.println("Mode: " + mode);
            writer.println("Board Type: " + game.getBoardType());
            writer.println("Board Size: " + game.getBoardSize());
            writer.println("Diagonal Enabled: " + game.isDiagonalEnabled());
            writer.println("Moves:");
            writer.flush();

        } catch (IOException e) {
            System.out.println("Error starting recording: " + e.getMessage());
        }
    }

    // Records one move
    public void recordMove(int fromR, int fromC, int toR, int toC) {
        if (recording && writer != null) {
            writer.println(fromR + "," + fromC + " -> " + toR + "," + toC);
            writer.flush();
        }
    }

    // Records that the board was randomized
    public void recordRandomize() {
        if (recording && writer != null) {
            writer.println("Board randomized");
            writer.flush();
        }
    }

    // Stops recording and closes the file
    public void stopRecording() {
        if (writer != null) {
            writer.println("Recording ended.");
            writer.close();
        }

        writer = null;
        recording = false;
    }

    public boolean isRecording() {
        return recording;
    }
}