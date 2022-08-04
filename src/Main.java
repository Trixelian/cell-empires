import java.awt.*;

public class Main {

    public static void main(String[] args) {
        Ref.singleton.init();
        boolean imageMode = true; // Change if you want to switch between a 'map' and a blank canvas.
        double inf = Double.POSITIVE_INFINITY;
        Board board;

        if (imageMode) {
            board = new Board("./resources/400px World Map Islands.png",
                    new Color(0,168,28), new Color(83,196,255),
                    3,200,10,
                    10, 1, 1000, 1000, 100);
        } else {
            board = new Board(400,400,2,50,40,10,inf, inf, 1000);
        }
        CellCanvas canvas = new CellCanvas(board, board.cells);
        canvas.createWindow("Cell Empires");

        while (true) {

            board.sortSpeed();

            // Cycle through every cell.
            for (Cell cell : board.speedList) {
                if (board.maxBoats > 0) {
                    // It's possible to travel on the sea, so neighbours can change.
                    cell.getNeighbours();
                }
                cell.action();
            }
            canvas.repaint(); // Update screen.
            try {
                Thread.sleep(40); // Delay so frame rate is reduced.
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
