import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CellCanvas extends JPanel {

    Board board;
    Cell[][] cells;

    String[] colorModes = {"empire", "strength", "speed", "maxHP", "currentHP", "stats"};
    int colorMode = 0;
    String[] guiModes = {"none", "basic"};
    int guiMode = 0;

    // Default constructor
    public CellCanvas(Board b, Cell[][] cellArr) {
        board = b;
        cells = cellArr;
        setBackground(Color.BLACK);
    }

    // The method responsible for displaying the contents of the canvas
    @Override
    public void paintComponent(Graphics graphics) {
        // Draw the component as before
        super.paintComponent(graphics);
        if (board.imageMode) {
            graphics.drawImage(board.image,0,0,board.width*board.pixelSize,board.height*board.pixelSize,this);
        }
        graphics.setFont(new Font("Arial", Font.BOLD, 14));
        // Now draw all cells
        Color tempColor;
        int cellCount = 0;
        double totalStrength = 0;
        double totalMaxHP = 0;
        double totalCurrentHP = 0;
        double totalSpeed = 0;
        double[] minMaxStrength = {Double.POSITIVE_INFINITY, 0};
        double[] minMaxMaxHP = {Double.POSITIVE_INFINITY, 0};
        double[] minMaxCurrentHP = {Double.POSITIVE_INFINITY, 0};
        double[] minMaxSpeed = {Double.POSITIVE_INFINITY, 0};
        float[] modifiers = new float[4];
        minMaxStrength[0] = Double.POSITIVE_INFINITY;
        minMaxMaxHP[0] = Double.POSITIVE_INFINITY;
        minMaxCurrentHP[0] = Double.POSITIVE_INFINITY;
        minMaxSpeed[0] = Double.POSITIVE_INFINITY;
        minMaxStrength[1] = 0;
        minMaxMaxHP[1] = 0;
        minMaxCurrentHP[1] = 0;
        minMaxSpeed[1] = 0;
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                if (!cell.dead) {
                    cellCount++;
                    totalStrength += cell.strength;
                    totalMaxHP += cell.maxHP;
                    totalCurrentHP += cell.currentHP;
                    totalSpeed += cell.speed;
                    if (cell.strength < minMaxStrength[0]) {
                        minMaxStrength[0] = cell.strength;
                    }
                    if (cell.strength > minMaxStrength[1]) {
                        minMaxStrength[1] = cell.strength;
                    }
                    if (cell.currentHP < minMaxCurrentHP[0]) {
                        minMaxCurrentHP[0] = cell.currentHP;
                    }
                    if (cell.currentHP > minMaxCurrentHP[1]) {
                        minMaxCurrentHP[1] = cell.currentHP;
                    }
                    if (cell.maxHP < minMaxMaxHP[0]) {
                        minMaxMaxHP[0] = cell.maxHP;
                    }
                    if (cell.maxHP > minMaxMaxHP[1]) {
                        minMaxMaxHP[1] = cell.maxHP;
                    }
                    if (cell.speed < minMaxSpeed[0]) {
                        minMaxSpeed[0] = cell.speed;
                    }
                    if (cell.speed > minMaxSpeed[1]) {
                        minMaxSpeed[1] = cell.speed;
                    }
                    modifiers[0] = (float) ((cell.strength-minMaxStrength[0])/(minMaxStrength[1]-minMaxStrength[0]));
                    modifiers[1] = (float) ((cell.speed-minMaxSpeed[0])/(minMaxSpeed[1]-minMaxSpeed[0]));
                    modifiers[2] = (float) ((cell.maxHP-minMaxMaxHP[0])/(minMaxMaxHP[1]-minMaxMaxHP[0]));
                    modifiers[3] = (float) ((cell.currentHP-minMaxCurrentHP[0])/(minMaxCurrentHP[1]-minMaxCurrentHP[0]));
                    switch (colorModes[colorMode]) {
                        case "empire":
                            tempColor = cell.color;
                            break;
                        case "strength":
                            tempColor = new Color(modifiers[0],0,0);
                            break;
                        case "speed":
                            tempColor = new Color(0,modifiers[1],0);
                            break;
                        case "maxHP":
                            tempColor = new Color(0,0,modifiers[2]);
                            break;
                        case "currentHP":
                            tempColor = new Color(0,modifiers[3],modifiers[3]);
                            break;
                        case "stats":
                            tempColor = new Color(modifiers[0],modifiers[3],modifiers[2]);
                            break;
                        default:
                            tempColor = Color.MAGENTA;
                            break;
                    }
                    graphics.setColor(tempColor);
                    graphics.fillRect(cell.x * board.pixelSize, cell.y * board.pixelSize, board.pixelSize, board.pixelSize);
                }
            }
        }
        switch (guiModes[guiMode]) {
            case "basic":
                graphics.setColor(new Color(0,0,0,127));
                graphics.fillRect(0,0,300,110);
                graphics.setColor(Color.WHITE);
                graphics.drawString("cellCount = " + cellCount, 10,20);
                graphics.drawString("avg strength = " + (totalStrength / cellCount), 10,40);
                graphics.drawString("avg maxHP = " + (totalMaxHP / cellCount), 10,60);
                graphics.drawString("avg currentHP = " + (totalCurrentHP / cellCount), 10,80);
                graphics.drawString("avg speed = " + (totalSpeed / cellCount), 10,100);
                break;
        }
    }

    public void createWindow(String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(board.width*board.pixelSize,board.height*board.pixelSize));
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        KeyListener listener = new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        colorMode = Math.floorMod(colorMode-1, colorModes.length);
                        break;
                    case KeyEvent.VK_RIGHT:
                        colorMode = Math.floorMod(colorMode+1, colorModes.length);
                        break;
                    case KeyEvent.VK_TAB:
                        guiMode = Math.floorMod(guiMode+1,guiModes.length);
                        break;
                }
            }
        };
        frame.addKeyListener(listener);
        frame.setFocusTraversalKeysEnabled(false);
    }
}
