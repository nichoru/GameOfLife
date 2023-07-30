
/**
 * Write a description of class GameOfLife here.
 *
 * @author Rune Nicholson
 * @version 31/07/2023 - worked on comments (for all methods, nested statements)
 */
import java.util.Scanner; // giving me access to user input and files
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
public class GameOfLife
{
    Scanner kb = new Scanner(System.in); // initialising a bunch of variables and the keyboard scanner
    int boardSize = 40;
    int genSize = 1;
    final String ALIVE = "⬛";
    final String DEAD = "⬜";
    final String NUMBEREDALIVE = "■";
    final String NUMBEREDDEAD = "□";
    final int RESETSPEED = 25;
    boolean[][][] cells = new boolean[boardSize][boardSize][2];
    String[] toggleString = new String[2];
    boolean isStart = false;
    boolean isCommand;
    boolean isSteady;
    boolean isWrap = false;
    boolean isEpilepsyMode = true;
    boolean isValid;
    int speed = 500;
    public GameOfLife() {
        title(true); // starts up the game
    }

    public void main(String[] args) {
        title(true); // starts up the game
    }

    void readFile(String fileName, boolean isAnimated) { // reads a given file and prints it on the board, either immediately or animated depending on what is given to it
        File myFile = new File(fileName);
        try {
            Scanner fileReader = new Scanner(myFile);
            boardSize = fileReader.nextInt();
            boolean[][][] tempBoard = new boolean[boardSize][boardSize][2];
            cells = tempBoard;
            // ^reading the board size in the given file and making the board that size
            String[] fileLine = new String[boardSize];
            fileLine = fileReader.nextLine().split(" ");
            if(isAnimated && !isEpilepsyMode) { // animates the transition to the new board image
                for(int i=0; i < boardSize; i++) cells[i][0][1] = true;
                update(false);
                for(int i=0; i < boardSize-1; i++) {
                    try {
                        Thread.sleep(RESETSPEED);
                    } catch(Exception e) {
                        System.out.println("Looks like something went wrong");
                    }
                    for(int j=0; j < boardSize; j++) cells[j][i+1][1] = true;
                    update(false);
                    try {
                        Thread.sleep(RESETSPEED);
                    } catch(Exception e) {
                        System.out.println("Looks like something went wrong");
                    }
                    fileLine = fileReader.nextLine().split(" ");
                    for(int j=0; j < boardSize; j++) {
                        cells[j][i][1] = fileLine[j].equals("o"); // Note: files are written using "o"s to represent live cells
                    }
                    update(false);
                }
                try {
                    Thread.sleep(100);
                } catch(Exception e) {
                    System.out.println("Looks like something went wrong");
                }
                fileLine = fileReader.nextLine().split(" ");
                for(int i=0; i < boardSize; i++) {
                    cells[i][boardSize-1][1] = fileLine[i].equals("o");
                }
            } else { // just prints the new board if animation is unwanted/epilepsy mode is on
                for(int i=0; i < boardSize; i++) {
                    fileLine = fileReader.nextLine().split(" ");
                    for(int j=0; j < boardSize; j++) {
                        cells[j][i][1] = fileLine[j].equals("o");
                    }
                }
            }
        } catch(IOException e) {
            invalidInput();
        }
    }

    void reset() { // sets all cells to false (dead) then updates the screen
        if(isEpilepsyMode) { // if in epilepsy mode, there's no animation for resetting the board
            for(int i=0; i < boardSize; i++) {
                for(int j=0; j < boardSize; j++) {
                    cells[j][i][1] = false;
                }
            }
            update(false);
        } else {
            for(int i=0; i < boardSize; i++) cells[i][0][1] = true;
            update(false);
            for(int i=0; i < boardSize-1; i++) {
                try {
                    Thread.sleep(RESETSPEED);
                } catch(Exception e) {
                    System.out.println("Looks like something went wrong");
                }
                for(int j=0; j < boardSize; j++) cells[j][i+1][1] = true;
                update(false);
                try {
                    Thread.sleep(RESETSPEED);
                } catch(Exception e) {
                    System.out.println("Looks like something went wrong");
                }
                for(int j=0; j < boardSize; j++) cells[j][i][1] = false;
                update(false);
            }
            try {
                Thread.sleep(100);
            } catch(Exception e) {
                System.out.println("Looks like something went wrong");
            }
            for(int i=0; i < boardSize; i++) cells[i][boardSize-1][1] = false;
            update(false);
        }
    }

    void menu() { // tells you the commands and lets you input them
        isStart = false;
        while(!isStart) {
            System.out.println("Press t to toggle (change) cell lives, s to start, q to quick advance one turn, r to reset board, x to quit, i to see instructions on how to play\nPress l to load a save file, c to save the current state\nPress a to change advancement speed (currently "+speed+" milliseconds per turn), b to change board size (currently "+boardSize+" by "+boardSize+")");
            if(isWrap) System.out.println("Press d to make cells that go off screen not come out the other side");
            else System.out.println("Press d to make cells that go off screen come out the other side");
            isCommand = false;
            while(!isCommand) {
                isCommand = true;
                switch (kb.nextLine().toLowerCase()) { // executes the command represented by the letter the user chose
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
                    case "q": advanceOne(false, true);
                        break;
                    case "r": reset();
                        break;
                    case "s": isStart = true;
                        break;
                    case "t": toggle();
                        break;
                    case "x": title(false);
                        break;
                    default: isCommand = false; // if the user inputs something unexpected, the game asks for them to input something else
                        invalidInput();
                }
            }
        }
        update(false); // this block is executed when "s" is pressed - it asks for the number of turns to advance
        System.out.println("How many turns do you want to advance?\nType a negative number to turn on automatic mode. It will stop advancing once it goes between that number of states or less, or hits the turn limit.\n(eg. -1 means it will stop once its stable, -2 means it will stop once it goes between 2 (or less) states etc.)\nTurn limit: "+turnLimit());
        isValid = false;
        while(!isValid) {
            isValid = true;
            try {
                advance(Integer.parseInt(kb.nextLine()));
            } catch(Exception e) {
                invalidInput();
            }
        }
    }

    void invalidInput() { // called when an input doesn't make sense. Used in conjunction with a while loop, it asks for the user to try again
        System.out.println("Invalid input. Try again.");
        isValid = false;
    }

    void title(boolean isStart) { // called when the game begins or when the user quits (differentiated by the variable isStart), makes the title screen then goes to menu
        readFile("TitleScreen.txt", !isStart);
        update(false);
        System.out.println("Press enter to start");
        kb.nextLine();
        if(isStart) { // epilepsy warning - asks if epilepsy mode is wanted, which disables animations and sets a limit on how fast the board updates
            System.out.println("WARNING - this game contains flashing lights which could trigger seizures in people with photosensitive epilepsy. Would you like to disable these? (y/n)");
            String answer = kb.nextLine();
            if(answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) isEpilepsyMode = false;
        }
        reset();
        help();
        if(isStart) menu();
    }

    void help() { // displays instructions
        update(false);
        System.out.println("Though 'game' is in the title, this is not a game - more of an automaton. My advice? Experiment! You can also load other people's previous games (press l) - there might be something cool there.");
        System.out.println("Every cell is either alive ("+ALIVE+") or dead ("+DEAD+"). You can switch these states in the menu.\nOnce you start playing, the cells will follow some rules, one turn at a time:");
        System.out.println("Rule 1 - any live cell with fewer than two live neighbours dies, as if by underpopulation\nRule 2 - any live cell with two or three live neighbours lives on to the next generation\nRule 3 - any live cell with more than three live neighbours dies, as if by overpopulation\nRule 4 - any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction\n");
    }

    void wrap() { // toggles cells being able to wrap around the screen
        isWrap = !isWrap;
        update(false);
    }

    void toggle() { // toggles player-selected cells between alive and dead then updates the screen
        update(true);
        toggleString[0] = ".";
        while(!toggleString[0].equalsIgnoreCase("m") || toggleString.length!=1) {
            System.out.println("To toggle a cell, enter its x and y coordinates (separated by a comma). To go back to the menu, type m");
            isValid = false;
            while(!isValid) {
                isValid = true;
                try {
                    toggleString = kb.nextLine().split(",");
                    if(!(toggleString[0].equalsIgnoreCase("m") && toggleString.length==1)) {
                        if(toggleString.length!=2) {
                            invalidInput();
                        } else {
                            cells[Integer.parseInt(toggleString[0])-1][Integer.parseInt(toggleString[1])-1][1] = !cells[Integer.parseInt(toggleString[0]) - 1][Integer.parseInt(toggleString[1]) - 1][1];
                            update(true);
                        }
                    }
                } catch(Exception e) {
                    invalidInput();
                }
            }
        }
        update(false);
    }

    void speed() { // lets the player change the speed the board updates
        int speedTemp = 0;
        while(speedTemp<350) {
            System.out.println("How often do you want the board to update (in milliseconds)? Upper limit: 5000");
            isValid = false;
            while(!isValid) {
                isValid = true;
                try {
                    speedTemp = Integer.parseInt(kb.nextLine());
                    if(speedTemp<0) invalidInput();
                    if(speedTemp>5000) speedTemp=5000; // upper limit of once every 5 seconds, as this is VERY boring and slow
                } catch(Exception e) {
                    invalidInput();
                }
            }
            if(speedTemp>=350 || !isEpilepsyMode) { // epilepsy mode sets a lower limit on this at 350 milliseconds, so that it doesn't flash too fast (the screen has a tendency to flash if its told to do too much too fast)
                speed = speedTemp;
                speedTemp = 350; // stops the loop
                update(false);
            } else System.out.print("Choose a number that's at least 350. ");
        }
    }

    void size() { // lets the player change the size of the board
        System.out.println("How wide/tall do you want the board to be?");
        boolean[][][] temp = new boolean[boardSize][boardSize][2];;
        isValid = false;
        while(!isValid) {
            isValid = true;
            try {
                boardSize = Integer.parseInt(kb.nextLine());
                if(boardSize==0) invalidInput();
                if(boardSize>39) { // upper limit of 39 to keep the board and menu on screen at the same time
                    System.out.println("Board must be between 1 and 39 cells wide/tall");
                    isValid = false;
                }
                temp = new boolean[boardSize][boardSize][2];
            } catch(Exception e) {
                invalidInput();
            }
        }
        for(int i=0; i<cells.length && i<boardSize; i++) {
            for(int j=0; j<cells.length && j<boardSize; j++) {
                temp[j][i][1] = cells[j][i][1];
            }
        }
        cells = temp;
        update(false);
    }

    void save() { // overwrites a save file with the current board
        System.out.println("Which save file do you want to save to (1,2 or 3 - this will overwrite previous saves)? To go back to the menu, type m");
        isValid = false;
        while(!isValid) {
            isValid = true;
            try {
                String tempFileString = kb.nextLine();
                if(tempFileString.equalsIgnoreCase("m")) update(false); // lets the user go back instead of forcing them to overwrite previous progress if they accidentally press "c"
                else {
                    int tempFileNum = Integer.parseInt(tempFileString);
                    if(tempFileNum!=1 && tempFileNum!=2 && tempFileNum!=3) invalidInput();
                    FileWriter saveWriter = new FileWriter("SavedGame"+tempFileNum+".txt");
                    saveWriter.write(boardSize+"\n");
                    for(int i=0; i<boardSize; i++) {
                        for(int j=0; j<boardSize; j++) { // Note: "o"s represent live cells and "_"s represent dead cells in the save files
                            if(cells[j][i][0]) saveWriter.write("o ");
                            else saveWriter.write("_ ");
                        }
                        saveWriter.write("\n");
                    }
                    saveWriter.flush();
                    saveWriter.close();
                }
            } catch(IOException e) {
                invalidInput();
            } catch(Exception e) {
                invalidInput();
            }
        }
        update(false);
    }

    void load() { // the user chooses a previous game that's been saved and loads it onto the board
        System.out.println("Which save file do you want to load (1,2 or 3)? To go back to the menu, type m");
        isValid = false;
        while(!isValid) {
            isValid = true;
            try {
                String tempLoadString = kb.nextLine();
                if(tempLoadString.equalsIgnoreCase("m")) update(false); // lets the user go back instead of forcing them to overwrite their current progress if they accidentally press "l"
                else readFile("SavedGame"+Integer.parseInt(tempLoadString)+".txt", true);
            } catch(Exception e) {
                invalidInput();
            }
        }
        update(false);
    }

    void update(boolean isNumbered) { // clears the screen and prints the board
        for(int h=0; h < genSize; h++) { // moving it forward a generation
            for(int i=0; i < boardSize; i++) {
                for(int j=0; j < boardSize; j++) {
                    cells[j][i][h]=cells[j][i][h+1];
                }
            }
        }
        System.out.print("\f");
        if(isNumbered) { // prints the numbers at the top of the screen when in toggle mode
            System.out.print("   ");
            for(int i=0; i < boardSize; i++) {
                System.out.print(i+1+" ");
                if(i<9) System.out.print(" ");
            }
            System.out.println();
        }
        for(int i=0; i < boardSize; i++) { // prints the board
            if(isNumbered) { // prints the numbers along the side of the screen when in toggle mode
                System.out.print(i+1+" ");
                if(i<9) System.out.print(" ");
            }
            for(int j=0; j < boardSize; j++) { // prints the cells one row at a time
                if(isNumbered) {
                    if(cells[j][i][genSize]) System.out.print(NUMBEREDALIVE+"  ");
                    else System.out.print(NUMBEREDDEAD+"  ");
                } else {
                    if(cells[j][i][genSize]) System.out.print(ALIVE+" ");
                    else System.out.print(DEAD+" ");
                }
            }
            System.out.println();
        }
    }

    void advance(int turns) { // advances the number of generations that the user inputs
        if(turns > -1) { // normal mode
            if(turns>turnLimit()) turns = turnLimit(); // stops after a minute
            update(false);
            for(int k=0; k < turns; k++) {
                System.out.print("Turn "+(k+1));
                advanceOne(true, true);
            }
        } else { // automatic mode
            isSteady = false;
            if(turns<-turnLimit()) turns = -turnLimit(); // stops after a minute
            changeGenSize(-turns); // makes the number of generations remembered equal the number the user wants
            for(int k=0; !isSteady && k<turnLimit(); k++) {
                System.out.print("Turn "+(k+1));
                advanceOne(true, false);
                for(int h=0; h < genSize && !isSteady; h++) { // checking if any of the generations recorded have repeated
                    isSteady = true;
                    for(int i=0; i < boardSize; i++) {
                        for(int j=0; j < boardSize; j++) {
                            if (cells[j][i][h] != cells[j][i][genSize]) isSteady = false;
                        }
                    }
                }
                update(false);
            }
            changeGenSize(1); // sets it back to recording only the old generation and the next generation
        }
        menu();
    }

    void changeGenSize(int newSize) { // changes the number of generations saved
        boolean[][][] tempCells = new boolean[boardSize][boardSize][newSize+1];
        for(int i=0; i<cells.length && i<boardSize; i++) { // makes every generation in tempCells[][][] equal to the current generation of cells[][][]
            for(int j=0; j<cells.length && j<boardSize; j++) {
                for(int k=0; k<newSize+1; k++) {
                    tempCells[j][i][k] = cells[j][i][genSize];
                }
            }
        }
        cells = tempCells; // this lets me change the dimensions of the cells[][][] array. If I manipulated tempCells[][][] in another section of my code, cells[][][] would also be manipulated (I don't use tempCells[][][] anywhere else though so it's fine)
        genSize = newSize;
        update(false);
    }

    void advanceOne(boolean isWait, boolean isUpdate) { // advances one generation (potentially waiting and updating the board based on the inputs)
        for(int i=0; i < boardSize; i++) { // applies the rules to the current generation
            for(int j=0; j < boardSize; j++) {
                if(cells[j][i][genSize-1]) {
                    if(!(countAdjacent(j,i,genSize-1)==2 || countAdjacent(j,i,genSize-1)==3)) cells[j][i][genSize] = false;
                } else {
                    if(countAdjacent(j,i,genSize-1)==3) cells[j][i][genSize] = true;
                }
            }
        }
        if(isWait) { // waits after the turn if needed
            try {
                Thread.sleep(speed);
            } catch(Exception e) {
                System.out.println("Looks like something went wrong");
            }
        }
        if(isUpdate) update(false);
    }

    int countAdjacent(int x, int y, int gen) { // counts how many cells are alive surrounding the cell given to it
        int surroundingNum = 0;
        if(y>0) { // if there are cells above the cell being checked, count them
            if(x>0 && cells[x-1][y-1][gen]) surroundingNum++;
            if(x==0 && cells[boardSize-1][y-1][gen]) surroundingNum++;
            if(cells[x][y-1][gen]) surroundingNum++;
            if(x<boardSize-1 && cells[x+1][y-1][gen]) surroundingNum++;
            if(x==boardSize-1 && cells[0][y-1][gen]) surroundingNum++;
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
            if(x==0 && cells[boardSize-1][y+1][gen]) surroundingNum++;
            if(cells[x][y+1][gen]) surroundingNum++;
            if(x<boardSize-1 && cells[x+1][y+1][gen]) surroundingNum++;
            if(x==boardSize-1 && cells[0][y+1][gen]) surroundingNum++;
        } else if(isWrap) {
            if(x>0 && cells[x-1][0][gen]) surroundingNum++;
            if(x==0 && cells[boardSize-1][0][gen]) surroundingNum++;
            if(cells[x][0][gen]) surroundingNum++;
            if(x<boardSize-1 && cells[x+1][0][gen]) surroundingNum++;
            if(x==boardSize-1 && cells[0][0][gen]) surroundingNum++;
        }
        return surroundingNum;
    }

    int turnLimit() {
        return 60120/(speed+1); // takes a minute at most at a time
    }
}

