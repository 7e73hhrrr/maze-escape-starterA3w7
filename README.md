# Assignment 3. Maze Escape

Student ID: 252985
Name: Farida Byerik
Group: SE-2532
Maze size (rows x columns): 9 x 10

This file is the report. Fill it in as you go. How to start: `START_HERE.md`. The task: `docs/Assignment3.pdf`.

## Program output

The 13 lines your program prints on your data (`java -cp out Main`):

    Broken:89
    Kept:72
    Last broken wall:132
    MST cost:28282
    Heaviest broken wall:884
    Path length:19
    DFS visited:61
    Dead ends:24
    Zones:9
    Largest zone:19
    Is DAG:0
    Flow stages:7
    Fastest route cost:19

My maze:

![maze](output/maze.png)

## Questions

Answer in your own words, two or three sentences each.

Mission 0. What do find and union do, and what makes them fast in your code?
find returns the representative of a cell’s group. union joins two different groups and returns false when both cells already belong to the same group. Union by size keeps the trees shallow, and path compression makes future searches faster.

Mission 1. Why is Broken always R*C - 1, and Kept always (R-1)*(C-1)?
Initially, there are R*C separate groups. Every successful union removes one wall and reduces the number of groups by one, so connecting everything requires R*C - 1 broken walls.

The grid has R*(C-1) + C*(R-1) inner walls. Subtracting the broken walls leaves (R-1)*(C-1) walls.

For my maze:
Cells = 9 × 10 = 90
Broken = 90 - 1 = 89
Kept = (9 - 1) × (10 - 1) = 72

Mission 2. My MST cost from Kruskal and from Prim, and why they are equal:
Both algorithms produce a minimum spanning tree, so both give total cost 28282. Kruskal selects cheap walls connecting different groups, while Prim selects cheap walls connecting the growing tree to new cells. All wall costs are different, so this graph has a unique MST.

Mission 3. Why is there exactly one path between two cells? How did you avoid a stack overflow on a huge maze?
The maze is connected and contains no cycles, so it is a tree. A tree has exactly one simple path between any two cells. My DFS uses an explicit array stack instead of recursive calls, so a long path does not overflow Java’s call stack.

Mission 4. Why does the second pass of Kosaraju go on the reversed graph? Why do the slides have no cycle exactly when Zones equals the number of cells?
The first pass records finishing order, and the second pass processes cells in reverse finishing order. On the reversed graph, each search can collect its entire strongly connected component without entering another unassigned component.
The input forbids self-loops, so any directed cycle contains at least two cells that belong to the same zone. Therefore, the graph has no cycles exactly when every zone contains one cell.

Mission 5. Why is it enough to go through the zones in the order of their numbers? What does this pass cost?
Kosaraju assigns zone numbers in topological order, so every edge between different zones goes from a smaller number to a larger number. When a zone is processed, all possible predecessor zones have already been processed.

With K zones and E distinct zone edges, the dynamic-programming pass takes O(K + E) time. Building the zone graph takes expected O(n + m) time using hash maps.


## AI use

I used ChatGPT to inspect the starter project, generate implementations
for DisjointSet, Maze, Escape, and Zones, and add DSU checks. ChatGPT
also tested the proposed code against the supplied examples and helped
explain the algorithms, complexity, and setup steps.

## Timing (Mission 7)

Milliseconds, 5 runs each, from `java -cp out Bench`.

| Maze | Kruskal (ms) | Prim (ms) |
|---|---:|---:|
| 100 x 100 | 11 | 8 |
| 300 x 300 | 67 | 69 |
| 1000 x 1000 | 807 | 1597 |

What do the numbers say about how the time grows? Two or three sentences.
Both algorithms take longer as the maze grows because there are more cells and walls to process. Their theoretical running time on these grids is O(n log n), where n is the number of cells. In my measurements, Prim was slightly faster on the smallest maze, but Kruskal was about twice as fast on the largest maze.

## Complexity

n is the number of cells, m the number of walls (or slides). Fill in every algorithm you wrote.

| Algorithm | Time | Extra space | Assumptions |
|---|---|---|---|
| DSU `find` and `union` | `O(α(n))` amortized per operation | `O(n)` for DSU; `O(1)` per operation | Union by size and path compression |
| File-order maze construction | `O(n + m α(n))` | `O(n)` | All walls are processed |
| Kruskal | `O(n + m log m)` | `O(n + m)` | Sort wall indexes, then use DSU |
| Prim | `O(n + m log m)` | `O(n + m)` | This implementation queues edges |
| DFS escape | `O(n + m)` | `O(n + m)` | Grid degree is at most four; the maze is a tree |
| Kosaraju | `O(n + m)` | `O(n + m)` | Two iterative DFS passes |
| Zone graph construction and routes | Expected `O(n + m)` | `O(n + m)` upper bound | Hash-map operations take expected constant time; DAG pass is `O(K + E)` |

## Before you submit

- data/walls.txt, data/slides.txt and output/maze.png are in the repository.
- `java tools/Check.java` shows all examples right.
- This README is filled in, the program output and the section AI use included.
- The work is pushed to main (at least one commit per mission) and the repository is public.
- The link is attached in Moodle.
