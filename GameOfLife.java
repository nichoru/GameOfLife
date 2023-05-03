
/**
 * Write a description of class GameOfLife here.
 *
 * @author Rune Nicholson
 * @version 2/05/2023
 */
import java.util.Scanner;
public class GameOfLife
{
    Scanner kb = new Scanner(System.in);
    final int BOARDSIZE = 20;
    final char ALIVE = 'o';
    final char DEAD = 'x';
    boolean[][] cells = new boolean[BOARDSIZE][BOARDSIZE];
    int toggleX = 0;
    int toggleY = 0;
    public GameOfLife()
    {
        reset();
        for(int i=0; i < BOARDSIZE; i++) { // prints the board
            for(int j=0; j < BOARDSIZE; j++) {
                if(cells[j][i]) System.out.print(ALIVE + " ");
                else System.out.print(DEAD + " ");
            }
            System.out.println();
        }

        System.out.println("To toggle a cell, enter its x and y coordinates (separated by a comma)");
        
    }

    void reset() { // sets all cells to false (dead)
        for(int i=0; i < BOARDSIZE; i++) {
            for(int j=0; j < BOARDSIZE; j++) {
                cells[j][i] = false;
            }
        }
    }
}
