import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class Board {

    int width, height, pixelSize, speciesCount, startingCells, colonyRange, maxBoats;
    int boats = 0;
    boolean imageMode;
    double strengthCap, maxHPCap, maxReproduction;
    Cell[][] cells;
    ArrayList<Color> colors;
    Color[][] imageColors;
    ArrayList<int[]> startingLocations = new ArrayList<>();
    ArrayList<Cell> speedList = new ArrayList<>();
    BufferedImage image;
    Color oceanColor,landColor;

    private static int randBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min,max+1);
    }

    private static ArrayList<Color> getColorArray(int count) {
        ArrayList<Color> colors = new ArrayList<>();
        float colorAdd = 1530 / count;
        float colorValue;
        int r, g, b;

        for (int i=0; i<count; i++) {
            colorValue = i * colorAdd;
            r = Math.min(Math.max((int) (Math.abs(765 - colorValue) - 255), 0), 255);
            g = Math.min(Math.max((int) (510 - Math.abs(510 - colorValue)), 0), 255);
            b = Math.min(Math.max((int) (510 - Math.abs(1020 - colorValue)), 0), 255);
            Color tempCol = new Color(r,g,b);
            colors.add(tempCol);
        }
        return colors;
    }

    public Board(String filePath, Color land, Color ocean,
                 int pSize, int _speciesCount, int _startingCells,
                 int _colonyRange, double _maxStrength, double _maxHP,
                 int _maxReproduction, int _maxBoats) {

        imageMode = true;
        image = null;
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
        }

        width = image.getWidth();
        height = image.getHeight();
        pixelSize = pSize;
        landColor = land;
        oceanColor = ocean;

        imageColors = new Color[width][height];
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                imageColors[i][j] = new Color(image.getRGB(i,j));
            }
        }

        speciesCount = _speciesCount;
        startingCells = _startingCells;
        colonyRange = _colonyRange;
        cells = new Cell[width][height];
        strengthCap = _maxStrength;
        maxHPCap = _maxHP;
        maxBoats = _maxBoats;
        maxReproduction = _maxReproduction;

        initialise();
    }

    public Board(int w, int h, int pSize, int _speciesCount, int _startingCells,
                 int _colonyRange, double _maxStrength, double _maxHP, double _maxReproduction) {
        imageMode = false;
        width = w;
        height = h;
        pixelSize = pSize;
        speciesCount = _speciesCount;
        startingCells = _startingCells;
        colonyRange = _colonyRange;
        cells = new Cell[width][height];
        strengthCap = _maxStrength;
        maxHPCap = _maxHP;
        maxBoats = 0;
        maxReproduction = _maxReproduction;

        initialise();
    }

    private void initialise() {
        for (int i=0; i<speciesCount; i++) {
            while (true) {
                int[] coordinates = new int[2];
                coordinates[0] = randBetween(0, width - 1);
                coordinates[1] = randBetween(0, height - 1);
                if (imageMode == false || !imageColors[coordinates[0]][coordinates[1]].equals(oceanColor)) {
                    startingLocations.add(coordinates);
                    break;
                }
            }
        }

        colors = getColorArray(startingLocations.size());

        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                cells[i][j] = new Cell(i,j,this);
            }
        }

        int colorIndex = 0;
        for (int[] coordinates : startingLocations) {
            cells[coordinates[0]][coordinates[1]].resetNew(colors.get(colorIndex));
            cells[coordinates[0]][coordinates[1]].newColony();
            colorIndex++;
        }

        for (int i=0; i<cells.length; i++) {
            for (Cell cell : cells[i]) {
                cell.getNeighbours();
                speedList.add(cell);
            }
        }

    }

    public void sortSpeed() {
        Collections.sort(speedList, Comparator.comparing(Cell::getSpeed));
    }
}
