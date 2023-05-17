
/**
 * Write a description of class GameOfLife here.
 *
 * @author Rune Nicholson
 * @version 18/05/2023 - Reads title screen file
 */
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
public class GameOfLife
{
    Scanner kb = new Scanner(System.in);
    int boardSize = 40;
    final char ALIVE = 'o';
    final char DEAD = '_';
    boolean[][][] cells = new boolean[boardSize][boardSize][2];
    String[] toggleString = new String[2];
    boolean isStart = false;
    boolean isCommand;
    int speed = 2000;
    public GameOfLife() 
    {
        reset();
        menu();
    }

    void readFile(String fileName) {
        File myFile = new File(fileName);
        try {
            Scanner fileReader = new Scanner(myFile);
            boardSize = fileReader.nextInt();
            String[] fileLine = new String[boardSize];
            fileLine = fileReader.nextLine().split(" ");
            for(int i=0; i < boardSize; i++) {
                fileLine = fileReader.nextLine().split(" ");
                for(int j=0; j < boardSize; j++) {
                    if(fileLine[j].equals("o")) cells[j][i][1] = true;
                    else cells[j][i][1] = false;
                }
            }
        } catch(IOException e) {
            System.out.println("Oopsies");
        }
    }

    void reset() { // sets all cells to false (dead) then updates the screen
        readFile("TitleScreen.txt");
        /*for(int i=0; i < boardSize; i++) {
            for(int j=0; j < boardSize; j++) {
                cells[j][i][0] = false;
                cells[j][i][1] = false;
            }
        }*/
        update();
    }

    void menu() { // tells you the commands and lets you input them
        isStart = false;
        while(!isStart) {
            System.out.println("Press t to toggle cell lives, s to start, q to advance one turn, r to reset board, a to change advancement speed (currently "+speed+" milliseconds per turn)");
            isCommand = false;
            while(!isCommand) {
                isCommand = true;
                switch(kb.nextLine().toLowerCase()) {
                    case "t": toggle();
                    break;
                    case "s": isStart = true;
                    break;
                    case "q": advance(1);
                    break;
                    case "a": speed();
                    break;
                    case "r": reset();
                    break;
                    default: isCommand = false;
                }
            }
        }
        update();
        System.out.println("How many turns do you want to advance?");
        advance(kb.nextInt());
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

    void update() { // clears the screen and prints the board
        for(int i=0; i < boardSize; i++) {
            for(int j=0; j < boardSize; j++) {
                cells[j][i][0]=cells[j][i][1];
            }
        }
        System.out.print("\f");
        for(int i=0; i < boardSize; i++) { // prints the board
            for(int j=0; j < boardSize; j++) {
                if(cells[j][i][1]) System.out.print(ALIVE + " ");
                else System.out.print(DEAD + " ");
            }
            System.out.println();
        }
    }

    void advance(int turns) {
        for(int k=0; k < turns; k++) {
            for(int i=0; i < boardSize; i++) {
                for(int j=0; j < boardSize; j++) {
                    if(cells[j][i][0]) {
                        if(!(countAdjacent(j,i)==2 || countAdjacent(j,i)==3)) cells[j][i][1] = false;
                    } else {
                        if(countAdjacent(j,i)==3) cells[j][i][1] = true;
                    }
                }
            }
            try {
                Thread.sleep(speed);
            } catch(Exception e) {
                System.out.println("Looks like something went wrong");
            }
            update();
        }
        menu();
    }

    int countAdjacent(int x, int y) { // counts how many cells are alive surrounding the cell given to it
        int surroundingNum = 0;
        if(y>0) {
            if(x>0) if(cells[x-1][y-1][0]) surroundingNum++;
            if(cells[x][y-1][0]) surroundingNum++;
            if(x<boardSize-1) if(cells[x+1][y-1][0]) surroundingNum++;
        }
        if(x>0) if(cells[x-1][y][0]) surroundingNum++;
        if(x<boardSize-1) if(cells[x+1][y][0]) surroundingNum++;
        if(y<boardSize-1) {
            if(x>0) if(cells[x-1][y+1][0]) surroundingNum++;
            if(cells[x][y+1][0]) surroundingNum++;
            if(x<boardSize-1) if(cells[x+1][y+1][0]) surroundingNum++;
        }
        return surroundingNum;
    }
}
