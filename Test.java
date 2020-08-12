import java.util.*;
import java.math.*;


class Test {

    public static void main(String[] args) {

        String[][] grid = {
            {"T",".",".",".",".",".",},
            {".","H","H","H",".","H",},
            {".",".",".","H",".","H",},
            {".","H","H","H",".","S",},
            {".",".",".","H",".","H",},
            {".","H",".",".",".",".",},
        };

        int xa = 5;
        int ya = 3;

        int xb = 0;
        int yb = 0;

        PathFinder.unvalaibleCells.add("H");
        PathFinder.xConnex = 4;

        ArrayList<int[]> path = PathFinder.abPath(xa, xb, ya, yb, grid);

        for(int[] step: path) {
            System.out.println(step[0] + ";" + step[1]);
        }
        
    }
}

/**
 * Represent a grid position with costs to move on.
 * Node type is manipulating inside open ans closed stacks
 */
class Node {
    public int x;
    public int y;
    public int cost;
    public int heuristic;
    public Node parent;

    // Overide to compare Node type objects in stacks
    public boolean equals(Object o) {
        // sanityze before type cast
        if (o == this) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }

        // here type cast should be safe
        Node c = (Node) o;

        // compare int x & int y
        return (this.x == c.x && this.y == c.y);
    }

    public String toString() {
        return String.format("{x: %s,\ny: %s\ncost: %s\nheuristic: %s}",
        this.x, this.y, this.cost, this.heuristic);
    }
}

/**
 * MAIN CLASS implementing A* algorithm
 */
class PathFinder {

    // String(s) in grid representing cell on wich we can't move
    public static ArrayList<String> unvalaibleCells = new ArrayList<>();
    // represent movements possibilities (4 or 8 cells arround)
    // 4 for up;down;left;right 6 for diagonals only 8 for 4 and diagonals
    public static int xConnex = 4;
    // set the output of logs
    public static boolean verbose = true;

    private static void log(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    private static void printGrid(String[][] grid) {
        if (verbose) { 
            for (String[] line: grid) {
                for(String cell: line) {
                   log(cell);
                }
                log("");
            }
        }
    }

    /*
    return the euclidian distance beetween to points A(xy) & B(xy)
    */
    private static int abDistance(Node nodeA, Node nodeB) {

        return (int) Math.sqrt(
            Math.pow((nodeB.x - nodeA.x), 2) + Math.pow((nodeB.y - nodeA.y), 2)
            );
    }

    /**
     * Return all cells wich can be reached (after excluding unavailableCells)
     */
    private static ArrayList<Node> targetableCells(String[][] grid, Node node) {
        ArrayList<Node> availableCells = new ArrayList<>();

        // x axis
        if (xConnex == 8 || xConnex == 4) {
            if (node.x + 1 < grid[0].length) {
                if (unvalaibleCells.indexOf(grid[node.y][node.x + 1]) == -1 ) {
                    Node n = new Node();
                    n.x = node.x + 1;
                    n.y = node.y;
                    availableCells.add(n);
                }
            }
            if (node.x - 1 >= 0) {
                if (unvalaibleCells.indexOf(grid[node.y][node.x - 1]) == -1 ) {
                    Node n = new Node();
                    n.x = node.x - 1;
                    n.y = node.y;
                    availableCells.add(n);
                }
            }
            // y axis
            if (node.y - 1 >= 0) {
                if (unvalaibleCells.indexOf(grid[node.y - 1][node.x]) == -1 ) {
                    Node n = new Node();
                    n.x = node.x;
                    n.y = node.y - 1;
                    availableCells.add(n);
                }
            }
            if (node.y + 1 < grid.length) {
                if (unvalaibleCells.indexOf(grid[node.y + 1][node.x]) == -1 ) {
                    Node n = new Node();
                    n.x = node.x;
                    n.y = node.y + 1;
                    availableCells.add(n);
                }
            }
        }
        if (xConnex == 8 || xConnex == 6) {
            // NE
            if (node.x + 1 < grid[0].length && node.y - 1 >= 0) {
                if (unvalaibleCells.indexOf(grid[node.y - 1][node.x + 1]) == -1) {
                    Node n = new Node();
                    n.x = node.x + 1;
                    n.y = node.y - 1;
                    availableCells.add(n);
                }
            }
            // SE
            if (node.x + 1 < grid[0].length && node.y + 1 < grid.length) {
                if (unvalaibleCells.indexOf(grid[node.y + 1][node.x + 1]) == -1) {
                    Node n = new Node();
                    n.x = node.x + 1;
                    n.y = node.y + 1;
                    availableCells.add(n);
                }
            }
            // SW
            if (node.x - 1 >= 0 && node.y + 1 < grid.length) {
                if (unvalaibleCells.indexOf(grid[node.y + 1][node.x - 1]) == -1) {
                    Node n = new Node();
                    n.x = node.x - 1;
                    n.y = node.y + 1;
                    availableCells.add(n);
                }
            }
            // NW
            if (node.x - 1 >= 0 && node.y - 1 >= 0) {
                if (unvalaibleCells.indexOf(grid[node.y - 1][node.x - 1]) == -1) {
                    Node n = new Node();
                    n.x = node.x + 1;
                    n.y = node.y - 1;
                    availableCells.add(n);
                }
            }
        }
        

        return availableCells;
    }

    /**
     * A* inplementation
     */
    public static ArrayList<int[]> abPath(int xa, int xb, int ya, int yb, String[][] grid) {
        Stack<Node> openStack = new Stack<>();
        Stack<Node> closedStack = new Stack<>();

        // init start node
        Node nodeA = new Node();
        nodeA.x = xa;
        nodeA.y = ya;

        Node nodeB = new Node();
        nodeB.x = xb;
        nodeB.y = yb;

        openStack.push(nodeA);
        
        while(openStack.size() > 0) {

            Node node = openStack.pop(); 

            // evaluating node has same coordinates than target node, recreate path from parents nodes
            if (node.equals(nodeB)) {
                ArrayList<int[]> path = new ArrayList<>();
                Node nodeFinal = node;
                
                int[] targetXY = {nodeB.x, nodeB.y};
                path.add(targetXY);

                while (nodeFinal.parent != null) {
                    nodeFinal = nodeFinal.parent;
                    int[] step = {nodeFinal.x, nodeFinal.y};
                    path.add(step);
                }

                Collections.reverse(path);
                return path;
            }

            // evaluating all accessible cells
            for(Node nextNode: targetableCells(grid, node)) {
                nextNode.parent = node;
                nextNode.cost = node.cost + 1;
                nextNode.heuristic = nextNode.cost + abDistance(nextNode, nodeB);

                // test if node allready exist in tehe solution with inferior cost
                if (closedStack.search(nextNode) == -1) {
                    if (openStack.search(nextNode) != -1) {
                        Node openNode = openStack.get(openStack.search(nextNode) - 1);
                        if (openNode.cost < nextNode.cost) {
                            break;
                        }
                    }
                    
                    openStack.push(nextNode);
                }
            }

            closedStack.push(node);
        }        

        return null;
    }
}
