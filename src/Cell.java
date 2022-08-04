import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Cell {

    // Efficient Fisher-Yates shuffle array function
    private static void shuffleArray(int[] array)
    {
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            if (index != i)
            {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }

    private static int randBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt((max-min)+1)+min;
        //return ThreadLocalRandom.current().nextInt(min,max+1);
    }

    private static double randUniform(double min, double max) {
        return min + Math.random() * (max - min);
    }

    public double limit(double val, double min, double max) {
        return Math.max(min, Math.min(val, max));
    }

    Board board;
    Color color;

    int x,y;
    boolean dead;
    double speed, strength, maxHP, currentHP, reproduction;
    int[] swimDirection = {0,0};

    ArrayList<Cell> neighbours = new ArrayList<>();
    ArrayList<Cell> choices = new ArrayList<>();
    Cell target;

    public Cell(int _x, int _y, Board board) {
        this.x = _x;
        this.y = _y;
        this.board = board;
        this.resetDead();
    }

    public void setStrength(double strength) {
        this.strength = strength;
        if (strength > Ref.singleton.getMaxStrength()) {
            Ref.singleton.setMaxStrength(strength);
        } else if (strength < Ref.singleton.getMinStrength()) {
            Ref.singleton.setMinStrength(strength);
        }
    }

    public void setCurrentHP(double currentHP) {
        this.currentHP = currentHP;
        if (currentHP > Ref.singleton.getMaxCurrentHP()) {
            Ref.singleton.setMaxCurrentHP(currentHP);
        } else if (currentHP < Ref.singleton.getMinCurrentHP()) {
            Ref.singleton.setMinCurrentHP(currentHP);
        }
    }

    public void setMaxHP(double maxHP) {
        this.maxHP = maxHP;
        if (maxHP > Ref.singleton.getMaxMaxHP()) {
            Ref.singleton.setMaxMaxHP(maxHP);
        } else if (maxHP < Ref.singleton.getMinMaxHP()) {
            Ref.singleton.setMinMaxHP(maxHP);
        }
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        if (speed > Ref.singleton.getMaxSpeed()) {
            Ref.singleton.setMaxSpeed(speed);
        } else if (speed < Ref.singleton.getMinSpeed()) {
            Ref.singleton.setMinSpeed(speed);
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setReproduction(double reproduction) {
        this.reproduction = reproduction;
    }

    public void setSwimDirection(int dx, int dy) {
        swimDirection[0] = dx;
        swimDirection[1] = dy;
    }

    public void resetNew(Color _color) {
        dead = false;
        if (_color == null) {
            // Color randomised to give a bright rainbow color.
            int[] tempColor = {255, randBetween(0,255),0};
            shuffleArray(tempColor);
            color = new Color(tempColor[0],tempColor[1],tempColor[2]);
        } else {
            color = _color;
        }
        setSpeed(randUniform(0,100));
        setStrength(0.5);
        setMaxHP(50);
        setCurrentHP(maxHP);
        setReproduction(board.maxReproduction);
        setSwimDirection(0,0);
    }
    public void resetChild(Color _color, double _speed, double _strength, double _maxHP) {
        dead = false;
        this.color = _color;
        speed = limit(getNewStat(_speed),0,100);
        strength = limit(getNewStat(_strength),0,board.strengthCap);
        maxHP = limit(getNewStat(_maxHP),0,board.maxHPCap);
        currentHP = maxHP;
        reproduction = board.maxReproduction;
        swimDirection[0] = 0;
        swimDirection[1] = 0;
    }
    public void resetCopy(Color _color, double _speed,
                         double _strength, double _maxHP,
                         double _currentHP, double _reproduction,
                          int[] _swimDirection) {
        dead = false;
        color = _color;
        speed = _speed;
        strength = _strength;
        maxHP = _maxHP;
        currentHP = _currentHP;
        reproduction = _reproduction;
        swimDirection[0] = _swimDirection[0];
        swimDirection[1] = _swimDirection[1];
    }
    public void resetDead() {
        if ((!board.imageMode || isSwimming()) && !dead && color != null) {
            board.boats--;
        }
        dead = true;
        color = null;
        speed = 0;
        strength = 0;
        maxHP = 0;
        currentHP = 0;
        reproduction = Double.POSITIVE_INFINITY;
        swimDirection[0] = 0;
        swimDirection[1] = 0;
    }

    public double getNewStat(double inputStat) {
        if (randBetween(0,100) > 0) {
            if (randBetween(0,1) == 0) {
                return inputStat*0.98;
            } else {
                return inputStat*1.02;
            }
        } else {
            if (randBetween(0,1) == 0) {
                return inputStat*0.8;
            } else {
                return inputStat*1.2;
            }
        }
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isSwimming() {
        return board.imageColors[x][y].equals(board.oceanColor);
    }

    public void newColony() {
        int tempX,tempY;
        for (int i=0; i<(board.startingCells-1); i++) {
            tempX = Math.floorMod(x+randBetween(-board.colonyRange, board.colonyRange),board.width);
            tempY = Math.floorMod(y+randBetween(-board.colonyRange, board.colonyRange),board.height);
            if ((!board.imageMode || !board.cells[tempX][tempY].isSwimming()) && (tempX != x || tempY != y) && board.cells[tempX][tempY].dead) {
                board.cells[tempX][tempY].resetChild(color,speed,strength,maxHP);
            }
        }
    }

    public void getNeighbours() {
        //int[][] nextList = {{1,0},{0,1},{-1,0},{0,-1}}; //Orthogonal
        int[][] nextList = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}}; //Diagonals + Orthogonal
        //int[][] nextList = {{1,1},{1,-1},{-1,1},{-1,-1}}; //Diagonals only
        //int[][] nextList = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}}; //Knight
        //int[][] nextList = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1},{1,0},{0,1},{-1,0},{0,-1}}; //Knight + Orthogonal
        //int[][] nextList = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-2},{1,1},{1,-1},{-1,1},{-1,-1}}; //Diagonals + Knight
        //int[][] nextList = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1},{2,0},{-2,0},{0,2},{0,-2}}; //Radius 2 Diamond
        //int[][] nextList = {{2,0},{-2,0},{0,2},{0,-2}}; //2-Step Orthogonal
        //int[][] nextList = {{1,3},{1,-3},{-1,3},{-1,-3},{3,1},{3,-1},{-3,1},{-3,-1}}; //2-Knight
        this.neighbours.clear();
        int nX, nY;
        for (int[] coordinates : nextList) {
            nX = Math.floorMod(x+coordinates[0],board.width);
            nY = Math.floorMod(y+coordinates[1],board.height);
            // If it's not image mode OR the neighbour isn't swimming OR there's a boat available OR you're swimming:
            if (!board.imageMode ||
                    !board.cells[nX][nY].isSwimming() ||
                    board.boats < board.maxBoats ||
                    isSwimming()) {
                this.neighbours.add(board.cells[nX][nY]);
            }
        }
    }

    public void action() {
        currentHP = Math.min(currentHP+0.1*strength*maxHP,maxHP);
        if (currentHP <= 0){
            resetDead();
        }
        if (dead == false) {
            choices.clear();
            for (Cell neighbour : neighbours) {
                if (neighbour.color != color) {
                    choices.add(neighbour);
                }
            }

            if (choices.size() != 0) {
                // If it's an image AND you can move in the swim direction AND you're swimming
                if (board.imageMode && choices.contains(board.cells[Math.floorMod(x + swimDirection[0], board.width)][Math.floorMod(y + swimDirection[1], board.height)]) && isSwimming()) {
                    target = board.cells[Math.floorMod(x + swimDirection[0], board.width)][Math.floorMod(y + swimDirection[1], board.height)];
                } else {
                    target = choices.get(randBetween(0, choices.size() - 1));
                }
            } else {
                target = null;
            }

            if (target != null) {
                if (board.imageMode && !target.isSwimming()) {
                    swimDirection[0] = 0;
                    swimDirection[1] = 0;
                }
                if (target.dead) {
                    boolean allyCheck = false;
                    for (Cell neighbour : neighbours) {
                        if (neighbour.color != null && neighbour.color.equals(color)) {
                            allyCheck = true;
                        }
                    }

                    /*
                    CONDITIONS FOR CHILD:
                    - someone is next to you
                    - you can reproduce
                    - either:
                     - it's not image mode
                     - you're not swimming and your child won't be in the water
                     */
                    if (allyCheck && reproduction <= 0 && (!board.imageMode || (!isSwimming() && !target.isSwimming()))) {
                        target.resetChild(color, speed, strength, maxHP);
                        swimDirection[0] = 0;
                        swimDirection[1] = 0;
                        reproduction = board.maxReproduction;
                    } else {
                        swimDirection[0] = target.x - x;
                        swimDirection[1] = target.y - y;
                        target.resetCopy(color, speed, strength, maxHP, currentHP, reproduction - currentHP, swimDirection);
                        if (board.imageMode && target.isSwimming() && !isSwimming()) {
                            board.boats++;
                        } else if (board.imageMode && !target.isSwimming() && isSwimming()) {
                            board.boats--;
                        }
                        dead = true;
                        color = null;
                    }
                } else if (target.color != color) {
                    target.currentHP -= strength * maxHP;
                    currentHP -= 0.5 * target.strength * maxHP;
                    if (target.currentHP <= 0) {
                        if (!board.imageMode || !target.isSwimming()) {
                            target.resetChild(color, speed, strength, maxHP);
                        } else {
                            target.resetDead();
                        }
                    }
                    if (currentHP <= 0) {
                        if (!board.imageMode || !isSwimming()) {
                            resetChild(color, speed, strength, maxHP);
                        } else {
                            resetDead();
                        }
                    }
                }
            } else {
                if (board.imageMode && isSwimming()) {
                    resetDead();
                }
            }
        }
    }
}
