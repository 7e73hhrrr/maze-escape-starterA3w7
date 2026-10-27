/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
import java.util.*;

public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();

        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);

        boolean[] seen = new boolean[n];

        int[] stack = new int[n];
        int[] next = new int[n];
        int[] finish = new int[n];

        int finished = 0;

        // First pass: DFS on the original graph.
        for (int start = 0; start < n; start++) {
            if (seen[start]) {
                continue;
            }

            int top = 0;
            stack[0] = start;
            seen[start] = true;

            while (top >= 0) {
                int u = stack[top];

                if (next[u] < out[u].length) {
                    int edge = out[u][next[u]++];
                    int v = s.to()[edge];

                    if (!seen[v]) {
                        seen[v] = true;
                        stack[++top] = v;
                    }
                } else {
                    // Record a cell only after all its outgoing
                    // slides have been processed.
                    finish[finished++] = u;
                    top--;
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);

        int zone = 0;

        // Second pass: reversed graph, decreasing finish order.
        for (int i = finished - 1; i >= 0; i--) {
            int start = finish[i];

            if (zoneOf[start] != -1) {
                continue;
            }

            int top = 0;
            stack[0] = start;
            zoneOf[start] = zone;

            while (top >= 0) {
                int u = stack[top--];

                for (int edge : in[u]) {
                    int v = s.from()[edge];

                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zone;
                        stack[++top] = v;
                    }
                }
            }

            zone++;
        }
        return new int[s.n()];
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(
            Input.Slides s,
            int[] zoneOf,
            int zoneCount
    ) {
        List<Map<Integer, Integer>> graph = new ArrayList<>();

        for (int z = 0; z < zoneCount; z++) {
            graph.add(new HashMap<>());
        }

        // Keep only the cheapest slide for each ordered zone pair.
        for (int i = 0; i < s.count(); i++) {
            int from = zoneOf[s.from()[i]];
            int to = zoneOf[s.to()[i]];

            if (from != to) {
                graph.get(from).merge(
                        to,
                        s.time()[i],
                        Integer::min
                );
            }
        }

        // Every zone alone is a chain containing one zone.
        int[] stages = new int[zoneCount];
        Arrays.fill(stages, 1);

        long[] distance = new long[zoneCount];
        Arrays.fill(distance, Long.MAX_VALUE);

        distance[zoneOf[0]] = 0;

        int longest = 0;

        // Zone numbers already form a topological order.
        for (int from = 0; from < zoneCount; from++) {
            longest = Math.max(longest, stages[from]);

            for (Map.Entry<Integer, Integer> edge
                    : graph.get(from).entrySet()) {

                int to = edge.getKey();

                stages[to] = Math.max(
                        stages[to],
                        stages[from] + 1
                );

                // Unreachable zones must not update shortest distances.
                if (distance[from] != Long.MAX_VALUE) {
                    distance[to] = Math.min(
                            distance[to],
                            distance[from] + edge.getValue()
                    );
                }
            }
        }

        return new long[]{
                longest,
                distance[zoneOf[s.n() - 1]]
        };
    }
}
