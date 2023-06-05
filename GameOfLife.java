
/**
 * Write a description of class GameOfLife here.
 *
 * @author Rune Nicholson
 * @version 6/06/2023 - automatic mode fully works, worked on making cells be able to wrap around the sides
 */
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
public class GameOfLife
{
    Scanner kb = new Scanner(System.in);
    int boardSize = 40;
    int genSize = 1;
    final String ALIVE = "o";
    final String DEAD = "_";
    final int RESETSPEED = 25;
    boolean[][][] cells = new boolean[boardSize][boardSize][2];
    String[] toggleString = new String[2];
    boolean isStart = false;
    boolean isCommand;
    boolean isSteady;
    boolean isWrap = false;
    int speed = 500;
    public GameOfLife() 
    {
        title(true);
    }

    void readFile(String fileName, boolean isAnimated) {
        File myFile = new File(fileName);
        try {
            Scanner fileReader = new Scanner(myFile);
            boardSize = fileReader.nextInt();
            boolean[][][] tempBoard = new boolean[boardSize][boardSize][2];
            cells = tempBoard;
            String[] fileLine = new String[boardSize];
            fileLine = fileReader.nextLine().split(" ");
            if(isAnimated) {
                for(int i=0; i < boardSize; i++) cells[i][0][1] = true;
                update();
                for(int i=0; i < boardSize-1; i++) {
                    try {
                        Thread.sleep(RESETSPEED);
                    } catch(Exception e) {
                        System.out.println("Looks like something went wrong");
                    }
                    for(int j=0; j < boardSize; j++) cells[j][i+1][1] = true;
                    update();
                    try {
                        Thread.sleep(RESETSPEED);
                    } catch(Exception e) {
                        System.out.println("Looks like something went wrong");
                    }
                    fileLine = fileReader.nextLine().split(" ");
                    for(int j=0; j < boardSize; j++) {
                        if(fileLine[j].equals(ALIVE)) cells[j][i][1] = true;
                        else cells[j][i][1] = false;
                    }
                    update();
                }
                try {
                    Thread.sleep(100);
                } catch(Exception e) {
                    System.out.println("Looks like something went wrong");
                }
                fileLine = fileReader.nextLine().split(" ");
                for(int i=0; i < boardSize; i++) {
                    if(fileLine[i].equals(ALIVE)) cells[i][boardSize-1][1] = true;
                    else cells[i][boardSize-1][1] = false;
                }
            } else {
                for(int i=0; i < boardSize; i++) {
                    fileLine = fileReader.nextLine().split(" ");
                    for(int j=0; j < boardSize; j++) {
                        if(fileLine[j].equals(ALIVE)) cells[j][i][1] = true;
                        else cells[j][i][1] = false;
                    }
                }
            }
        } catch(IOException e) {
            System.out.println("Oopsies");
        }
    }

    void reset() { // sets all cells to false (dead) then updates the screen
        for(int i=0; i < boardSize; i++) cells[i][0][1] = true;
        update();
        for(int i=0; i < boardSize-1; i++) {
            try {
                Thread.sleep(RESETSPEED);
            } catch(Exception e) {
                System.out.println("Looks like something went wrong");
            }
            for(int j=0; j < boardSize; j++) cells[j][i+1][1] = true;
            update();
            try {
                Thread.sleep(RESETSPEED);
            } catch(Exception e) {
                System.out.println("Looks like something went wrong");
            }
            for(int j=0; j < boardSize; j++) cells[j][i][1] = false;
            update();
        }
        try {
            Thread.sleep(100);
        } catch(Exception e) {
            System.out.println("Looks like something went wrong");
        }
        for(int i=0; i < boardSize; i++) cells[i][boardSize-1][1] = false;
        update();
    }

    void menu() { // tells you the commands and lets you input them
        isStart = false;
        while(!isStart) {
            System.out.println("Press t to toggle cell lives, s to start, q to advance one turn, r to reset board, x to quit, i to see instructions on how to play\nPress l to load a save file, c to save the current state\nPress a to change advancement speed (currently "+speed+" milliseconds per turn), b to change board size (currently "+boardSize+" by "+boardSize+")");
            if(isWrap) System.out.println("Press d to make cells that go off screen not come out the other side");
            else System.out.println("Press d to make cells that go off screen come out the other side");
            isCommand = false;
            while(!isCommand) {
                isCommand = true;
                switch(kb.nextLine().toLowerCase()) {
                    case "a": speed();
                        break;
                    case "b": size();
                        break;
                    case "c": save();
                        break;
                    case "d": wrap();
                        break;
                    case "i": help();
                        break;
                    case "l": load();
                        break;
                    case "q": advanceOne(false,true);
                        break;
                    case "r": reset();
                        break;
                    case "s": isStart = true;
                        break;
                    case "t": toggle();
                        break;
                    case "x": title(false);
                        break;
                    default: isCommand = false;
                }
            }
        }
        update();
        System.out.println("How many turns do you want to advance?\nType a negative number to turn on automatic mode. It will stop advancing once it goes between that number of states or less.\n(eg. -1 means it will stop once its stable, -2 means it will stop once it goes between 2 (or less) states etc.)");
        advance(kb.nextInt());
    }

    void title(boolean isStart) {
        if(isStart) readFile("TitleScreen.txt", false);
        else readFile("TitleScreen.txt", true);
        update();
        System.out.println("Press enter to start");
        kb.nextLine();
        reset();
        if(isStart) menu();
    }

    void help() {
        update();
        System.out.println("Every cell is either alive ("+ALIVE+") or dead ("+DEAD+"). You can switch these states in the menu.\nOnce you start playing, the cells will follow some rules, one turn at a time:");
        System.out.println("Rule 1 - any live cell with fewer than two live neighbours dies, as if by underpopulation\nRule 2 - any live cell with two or three live neighbours lives on to the next generation\nRule 3 - any live cell with more than three live neighbours dies, as if by overpopulation\nRule 4 - any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction\n");
    }

    void wrap() {
        if(isWrap) isWrap = false;
        else isWrap = true;
        update();
    }

    void toggle() { // toggles player-selected cells between alive and dead then updates the screen
        update();
        System.out.println("To toggle a cell, enter its x and y coordinates (separated by a comma). To go back to the menu, type m");
        toggleString = kb.nextLine().split(",");
        while(!toggleString[0].toLowerCase().equals("m")) {
            if(cells[Integer.parseInt(toggleString[0])-1][Integer.parseInt(toggleString[1])-1][1]) {
                cells[Integer.parseInt(toggleString[0])-1][Integer.parseInt(toggleString[1])-1][1] = false;
            } else {
                cells[Integer.parseInt(toggleString[0])-1][Integer.parseInt(toggleString[1])-1][1] = true;
            }
            update();
            System.out.println("To toggle a cell, enter its x and y coordinates (separated by a comma). To go back to the menu, type m");
            toggleString = kb.nextLine().split(",");
        }
        update();
    }

    void speed() {
        System.out.println("How often do you want the board to update (in milliseconds)?");
        speed = kb.nextInt();
        update();
    }

    void size() {
        System.out.println("How wide/tall do you want the board to be?");
        boardSize = kb.nextInt();
        boolean[][][] temp = new boolean[boardSize][boardSize][2];
        for(int i=0; i<cells.length && i<boardSize; i++) {
            for(int j=0; j<cells.length && j<boardSize; j++) {
                temp[j][i][1] = cells[j][i][1];
            }
        }
        cells = temp;
        update();
    }

    void save() {
        System.out.println("Which save file do you want to save to (1,2 or 3 - this will overwrite previous saves)?");
        try {
            FileWriter saveWriter = new FileWriter("SavedGame"+kb.nextInt()+".txt");
            saveWriter.write(boardSize+"\n");
            for(int i=0; i<boardSize; i++) {
                for(int j=0; j<boardSize; j++) {
                    if(cells[j][i][0]) saveWriter.write(ALIVE+" ");
                    else saveWriter.write(DEAD+" ");
                }
                saveWriter.write("\n");
            }
            saveWriter.flush();
            saveWriter.close();
        } catch(IOException e) {
            System.out.println("Oh no");
        }
        update();
    }

    void load() {
        System.out.println("Which save file do you want to load (1,2 or 3)?");
        readFile("SavedGame"+kb.nextInt()+".txt", true);
        update();
    }

    void update() { // clears the screen and prints the board
        for(int h=0; h < genSize; h++) {
            for(int i=0; i < boardSize; i++) {
                for(int j=0; j < boardSize; j++) {
                    cells[j][i][h]=cells[j][i][h+1];
                }
            }
        }
        System.out.print("\f");
        for(int i=0; i < boardSize; i++) { // prints the board
            for(int j=0; j < boardSize; j++) {
                if(cells[j][i][genSize]) System.out.print(ALIVE + " ");
                else System.out.print(DEAD + " ");
            }
            System.out.println();
        }
    }

    void advance(int turns) {
        if(turns > -1) {
            for(int k=0; k < turns; k++) {
                advanceOne(true, true);
            }
        } else {
            isSteady = false;
            changeGenSize(-turns);
            while(!isSteady) {
                advanceOne(true, false);
                for(int h=0; h < genSize; h++) {
                    isSteady = true;
                    for(int i=0; i < boardSize; i++) {
                        for(int j=0; j < boardSize; j++) {
                            if(cells[j][i][h] != cells[j][i][genSize]) {
                                isSteady = false;
                            }
                        }
                    }
                    if(isSteady) break;
                }
                update();
            }
            changeGenSize(1);
        }
        menu();
    }

    void changeGenSize(int newSize) {
        boolean[][][] temp = new boolean[boardSize][boardSize][newSize+1];
        for(int i=0; i<cells.length && i<boardSize; i++) {
            for(int j=0; j<cells.length && j<boardSize; j++) {
                for(int k=0; k<newSize+1; k++) {
                    temp[j][i][k] = cells[j][i][genSize];
                }
            }
        }
        cells = temp;
        genSize = newSize;
        update();
    }

    void advanceOne(boolean isWait, boolean isUpdate) {
        for(int i=0; i < boardSize; i++) {
            for(int j=0; j < boardSize; j++) {
                if(cells[j][i][genSize-1]) {
                    if(!(countAdjacent(j,i,genSize-1)==2 || countAdjacent(j,i,genSize-1)==3)) cells[j][i][genSize] = false;
                } else {
                    if(countAdjacent(j,i,genSize-1)==3) cells[j][i][genSize] = true;
                }
            }
        }
        if(isWait) {
            try {
                Thread.sleep(speed);
            } catch(Exception e) {
                System.out.println("Looks like something went wrong");
            }
        }
        if(isUpdate) update();
    }

    int countAdjacent(int x, int y, int gen) { // counts how many cells are alive surrounding the cell given to it
        int surroundingNum = 0;
        if(y>0) {
            if(x>0 && cells[x-1][y-1][gen]) surroundingNum++;
            if(cells[x][y-1][gen]) surroundingNum++;
            if(x<boardSize-1 && cells[x+1][y-1][gen]) surroundingNum++;
        } else if(isWrap) {
            if(x>0 && cells[x-1][boardSize-1][gen]) surroundingNum++;
            if(x==0 && cells[boardSize-1][boardSize-1][gen]) surroundingNum++;
            if(cells[x][boardSize-1][gen]) surroundingNum++;
            if(x<boardSize-1 && cells[x+1][boardSize-1][gen]) surroundingNum++;
            if(x==boardSize-1 && cells[0][boardSize-1][gen]) surroundingNum++;
        }
        if(x>0 && cells[x-1][y][gen]) surroundingNum++;
        if(x==0 && isWrap && cells[boardSize-1][y][gen]) surroundingNum++;
        if(x<boardSize-1 && cells[x+1][y][gen]) surroundingNum++;
        if(x==boardSize-1 && isWrap && cells[0][y][gen]) surroundingNum++;
        if(y<boardSize-1) {
            if(x>0 && cells[x-1][y+1][gen]) surroundingNum++;
            if(cells[x][y+1][gen]) surroundingNum++;
            if(x<boardSize-1 && cells[x+1][y+1][gen]) surroundingNum++;
        } else if(isWrap) {
            if(x>0 && cells[x-1][0][gen]) surroundingNum++;
            if(x==0 && cells[boardSize-1][0][gen]) surroundingNum++;
            if(cells[x][0][gen]) surroundingNum++;
            if(x<boardSize-1 && cells[x+1][0][gen]) surroundingNum++;
            if(x==boardSize-1 && cells[0][0][gen]) surroundingNum++;
        }
        return surroundingNum;
    }
}
