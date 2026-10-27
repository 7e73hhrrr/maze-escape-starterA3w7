/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
import java.util.Arrays;

public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int cols = w.cols();

        int[][] graph = Graphs.open(w, broken);

        // Count dead ends across the entire maze.
        int deadEnds = 0;

        for (int[] neighbours : graph) {
            if (neighbours.length == 1) {
                deadEnds++;
            }
        }

        boolean[] seen = new boolean[n];

        // Explicit DFS stack: no recursive method calls.
        int[] stack = new int[n];
        int[] direction = new int[n];

        int top = 0;
        int visited = 1;

        stack[0] = 0;
        seen[0] = true;

        while (top >= 0) {
            int u = stack[top];

            if (u == n - 1) {
                return new Result(
                        Arrays.copyOf(stack, top + 1),
                        visited,
                        deadEnds
                );
            }

            // All four directions have been checked: backtrack.
            if (direction[top] == 4) {
                top--;
                continue;
            }

            int dir = direction[top]++;

            int v = switch (dir) {
                case 0 -> u >= cols
                        ? u - cols
                        : -1;

                case 1 -> u % cols + 1 < cols
                        ? u + 1
                        : -1;

                case 2 -> u + cols < n
                        ? u + cols
                        : -1;

                default -> u % cols > 0
                        ? u - 1
                        : -1;
            };

            if (v == -1 || seen[v]) {
                continue;
            }

            // The neighbouring cell is reachable only if its wall is open.
            boolean open = false;

            for (int neighbour : graph[u]) {
                if (neighbour == v) {
                    open = true;
                    break;
                }
            }

            if (!open) {
                continue;
            }

            seen[v] = true;
            visited++;

            stack[++top] = v;
            direction[top] = 0;
        }

        return new Result(new int[0], 0, 0);
    }
}
