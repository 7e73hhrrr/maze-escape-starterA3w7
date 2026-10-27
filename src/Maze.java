import java.util.*;
/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());

        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int count = 0;

        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count++] = k;
            }
        }
        return new int[0];
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];

        for (int k = 0; k < order.length; k++) {
            order[k] = k;
        }

        // Sort wall indexes without modifying the original input.
        Arrays.sort(
                order,
                Comparator.comparingInt(k -> w.cost()[k])
        );

        DisjointSet dsu = new DisjointSet(w.cells());

        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int count = 0;

        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count++] = k;

                if (count == w.cells() - 1) {
                    break;
                }
            }
        }

        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();

        if (n == 0) {
            return 0;
        }

        // Build lists of wall indexes touching each cell.
        int[] degree = new int[n];

        for (int k = 0; k < w.count(); k++) {
            degree[w.a()[k]]++;
            degree[w.b()[k]]++;
        }

        int[][] incident = new int[n][];

        for (int u = 0; u < n; u++) {
            incident[u] = new int[degree[u]];
        }

        int[] filled = new int[n];

        for (int k = 0; k < w.count(); k++) {
            incident[w.a()[k]][filled[w.a()[k]]++] = k;
            incident[w.b()[k]][filled[w.b()[k]]++] = k;
        }

        PriorityQueue<Integer> pq = new PriorityQueue<>(
                Comparator.comparingInt(k -> w.cost()[k])
        );

        boolean[] used = new boolean[n];

        used[0] = true;
        int reached = 1;
        long total = 0;

        for (int k : incident[0]) {
            pq.add(k);
        }

        while (!pq.isEmpty() && reached < n) {
            int k = pq.remove();

            int a = w.a()[k];
            int b = w.b()[k];

            // Both cells are already inside the tree.
            if (used[a] && used[b]) {
                continue;
            }

            int next = used[a] ? b : a;

            used[next] = true;
            reached++;
            total += w.cost()[k];

            // Add walls leading from this cell to unused cells.
            for (int edge : incident[next]) {
                int other = w.a()[edge] == next
                        ? w.b()[edge]
                        : w.a()[edge];

                if (!used[other]) {
                    pq.add(edge);
                }
            }
        }

        if (reached != n) {
            throw new IllegalStateException("Disconnected maze");
        }

        return total;
    }
}

