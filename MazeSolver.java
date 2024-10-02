import java.util.*;
import java.io.*;

public class MazeSolver {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\lukeb\\Downloads\\Maze2.txt");
        int lives = 200;
        int posX = 0;
        int posY = 0;
        String[] input = new String[20];

        try {
            Scanner scan = new Scanner(file);
            for (int i = 0; i < 20; i++) {
                input[i] = scan.nextLine(); // read the maze
            }
            posX = Integer.parseInt(scan.nextLine()); // this is where you appear
            posY = Integer.parseInt(scan.nextLine()); // scans in two numbers with the maze that are your coordinates
            scan.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        boolean[][] maze = new boolean[20][20];  // loop through maze 
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                if (input[i].charAt(j) == 'X') {
                    maze[i][j] = false; // theres a wall, mark as not passable
                } else {
                    maze[i][j] = true; // theres a space, a space that can be traversed
                }
            }
        }

        Brain myBrain = new Brain(20, 20); // Pass maze size to brain class
        while (lives > 0) {
            System.out.println("Current position: " + posX + " " + posY);
            for (int i = 0; i < 20; i++) { // print out the map
                for (int j = 0; j < 20; j++) {
                    if (posX == i && posY == j) {
                        System.out.print("o");
                    } else if (maze[i][j] == true) {
                        System.out.print(" "); // there is a space
                    } else {
                        System.out.print("X"); // there is a wall
                    }
                }
                System.out.println();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // handle interrupted exception (if necessary)
                e.printStackTrace();
            }

            // get move decision from Brain
            String move = myBrain.getMove(maze, posX, posY);

            if (move.equals("north") && maze[posX - 1][posY]) {
                posX--; // if the brain wants to move north if its possible
            } else if (move.equals("south") && maze[posX + 1][posY]) {
                posX++; // if the brain wants to move south if its possible
            } else if (move.equals("east") && maze[posX][posY + 1]) {
                posY++;
            } else if (move.equals("west") && maze[posX][posY - 1]) {
                posY--;
            }

            lives--;

            if (posY % 19 == 0 || posX % 19 == 0) { // found a way out
                System.out.println("You found the exit at: " + posX + "," + posY); // print out coordinates of the exit
                System.exit(0);
            }
        }
        System.out.println("You died in the maze!");
    }
}
//Brain class that uses stacks to decide what path to take and to keep track where youve visited and be able to backtrack
class Brain {
    private int[][] visited; // keep track of your visited position
    private int rows, cols;
    private Stack<String> moveStack; 

    public Brain(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        visited = new int[rows][cols];
        moveStack = new Stack<>();
    }

    // Depth First Search, based decision making to explore deeper paths first
    public String getMove(boolean[][] maze, int posX, int posY) {
        visited[posX][posY] = 1; // mark the current position as visited

        // Possible movements: North, South, East, West
        boolean north = posX > 0 && maze[posX - 1][posY] && visited[posX - 1][posY] == 0;
        boolean south = posX < rows - 1 && maze[posX + 1][posY] && visited[posX + 1][posY] == 0;
        boolean east = posY < cols - 1 && maze[posX][posY + 1] && visited[posX][posY + 1] == 0;
        boolean west = posY > 0 && maze[posX][posY - 1] && visited[posX][posY - 1] == 0;

        // prioritize directions in Depth First Search style, try unvisited directions first
        if (east) {
            moveStack.push("east");
            return "east";
        } else if (south) {
            moveStack.push("south");
            return "south";
        } else if (west) {
            moveStack.push("west");
            return "west";
        } else if (north) {
            moveStack.push("north");
            return "north";
        }

        // if all directions are visited or blocked, backtrack
        if (!moveStack.isEmpty()) {
            String lastMove = moveStack.pop(); // get the last move from the stack
            // backtrack in the opposite direction
            switch (lastMove) {
                case "north":
                    return "south";
                case "south":
                    return "north";
                case "east":
                    return "west";
                case "west":
                    return "east";
            }
        }

        return "north"; // if no other option, go north
    }
}
