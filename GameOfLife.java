
/**
 * Write a description of class GameOfLife here.
 *
 * @author Rune Nicholson
 * @version 4/05/2023 - toggles cell life, menu started on
 */
import java.util.Scanner;
public class GameOfLife
{
    Scanner kb = new Scanner(System.in);
    final int BOARDSIZE = 20;
    final char ALIVE = 'o';
    final char DEAD = '_';
    boolean[][] cells = new boolean[BOARDSIZE][BOARDSIZE];
    String[] toggleString = new String[2];
    boolean isStart = false;
    public GameOfLife()
    {
        reset();
        menu();
        
    }

    void reset() { // sets all cells to false (dead) then updates the screen
        for(int i=0; i < BOARDSIZE; i++) {
            for(int j=0; j < BOARDSIZE; j++) {
                cells[j][i] = false;
            }
        }
        update();
    }

    void menu() { // tells you the commands and lets you input them
        while(!isStart) {
            System.out.println("Press t to toggle a cell's life, s to start");
            switch(kb.nextLine().toLowerCase()) {
                case "t": toggle();
                break;
                case "s": isStart = true;
                break;
            }
        }
        System.out.println("How many turns do you want to advance?");
    }

    void toggle() { // toggles player-selected cells between alive and dead then updates the screen
        System.out.println("To toggle a cell, enter its x and y coordinates (separated by a comma)");
        toggleString = kb.nextLine().split(",");
        if(cells[Integer.parseInt(toggleString[0])-1][Integer.parseInt(toggleString[1])-1]) cells[Integer.parseInt(toggleString[0])-1][Integer.parseInt(toggleString[1])-1] = false;
        else cells[Integer.parseInt(toggleString[0])-1][Integer.parseInt(toggleString[1])-1] = true;
        update();
    }

    void update() { // clears the screen and prints the board
        System.out.print("\f");
        for(int i=0; i < BOARDSIZE; i++) { // prints the board
            for(int j=0; j < BOARDSIZE; j++) {
                if(cells[j][i]) System.out.print(ALIVE + " ");
                else System.out.print(DEAD + " ");
            }
            System.out.println();
        }
    }
}
