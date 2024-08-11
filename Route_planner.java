import java.util.*;
class Graph {
    private final Map<String, Integer> vertexIndexMap = new HashMap<>();
    private final List<String> vertices = new ArrayList<>();
    private final int[][] adjacencyMatrix;

    public Graph(String[] vertices, int[][] adjacencyMatrix) {
        int size = vertices.length;
        this.adjacencyMatrix = adjacencyMatrix;

        // Initializing vertex index map
        for (int i = 0; i < size; i++) {
            vertexIndexMap.put(vertices[i], i);
            this.vertices.add(vertices[i]);
        }
    }
    public int getDistance(String from, String to) {
        int fromIndex = vertexIndexMap.get(from);
        int toIndex = vertexIndexMap.get(to);
        return adjacencyMatrix[fromIndex][toIndex];
    }
    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public int getVertexIndex(String vertex) {
        return vertexIndexMap.get(vertex);
    }

    public List<String> getVertices() {
        return vertices;
    }
}

// Main class to test the route planner
public class RoutePlanner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input vertices
        System.out.println("Enter vertices separated by spaces:");
        String[] vertices = scanner.nextLine().split(" ");

        // Input edges as weight matrix
        int[][] weightMatrix = new int[vertices.length][vertices.length];
        System.out.println("Enter edges in matrix format (weight matrix):");
        for (int i = 0; i < vertices.length; i++) {
            for (int j = 0; j < vertices.length; j++) {
                weightMatrix[i][j] = scanner.nextInt();
            }
        }
        Graph graph = new Graph(vertices, weightMatrix);

        System.out.println("Enter the starting point:");
        String start = scanner.next();
        // Finding the shortest delivery route using TSP approach (Dynamic Programming)
        List<String> shortestRoute = findShortestRoute(graph, start);

        System.out.println("Delivery route:");
        for (String location : shortestRoute) {
            System.out.print(location + " -> ");
        }
        System.out.println(start);  // Returning to start point
    }

    public static List<String> findShortestRoute(Graph graph, String start) {
        List<String> vertices = graph.getVertices();
        int n = vertices.size();

        // If there is only one vertex return the start point
        if (n == 1) {
            return Collections.singletonList(start);
        }

        // Initializing DP table
        int[][] dp = new int[n][1 << n];
        int[][] parent = new int[n][1 << n];
        final int INF = 1000000000;

        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }
        dp[graph.getVertexIndex(start)][1 << graph.getVertexIndex(start)] = 0;

        // dp table
        for (int mask = 1; mask < (1 << n); mask += 2) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) != 0) {
                    for (int next = 0; next < n; next++) {
                        if ((mask & (1 << next)) == 0) {
                            int newMask = mask | (1 << next);
                            if (dp[last][mask] + graph.getDistance(vertices.get(last), vertices.get(next)) < dp[next][newMask]) {
                                dp[next][newMask] = dp[last][mask] + graph.getDistance(vertices.get(last), vertices.get(next));
                                parent[next][newMask] = last;
                            }
                        }
                    }
                }
            }
        }
        int minCost = INF;
        int startIdx = graph.getVertexIndex(start);
        int finalMask = (1 << n) - 1;
        int endNode = -1;
        //finding shortest distance from dp table
        for (int i = 0; i < n; i++) {
            if (dp[i][finalMask] + graph.getDistance(vertices.get(i), start) < minCost) {
                minCost = dp[i][finalMask] + graph.getDistance(vertices.get(i), start);
                endNode = i;
            }
        }

        List<String> path = new ArrayList<>();
        int mask = finalMask;
        int currNode = endNode;

        while (mask != (1 << startIdx)) {
            path.add(vertices.get(currNode));
            int nextNode = parent[currNode][mask];
            mask ^= (1 << currNode);
            currNode = nextNode;
        }
        path.add(vertices.get(currNode));
        path.add(start);

        Collections.reverse(path);
        return path;
    }
}