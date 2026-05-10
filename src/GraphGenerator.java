import java.util.Random;

public class GraphGenerator {

    public static final int INF = Integer.MAX_VALUE / 2;

    public static int[][] generate(int n, double density, int minWeight, int maxWeight, long seed) {
        if (minWeight > maxWeight) {
            throw new IllegalArgumentException("minWeight не може перевищувати maxWeight");
        }
        Random random = new Random(seed);
        int[][] graph = new int[n][n];
        int weightRange = maxWeight - minWeight + 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    graph[i][j] = 0;
                } else if (random.nextDouble() < density) {
                    graph[i][j] = minWeight + random.nextInt(weightRange);
                } else {
                    graph[i][j] = INF;
                }
            }
        }
        return graph;
    }

    public static void injectNegativeCycle(int[][] graph, long seed) {
        int n = graph.length;
        if (n < 3) {
            throw new IllegalArgumentException("Для від'ємного циклу потрібно щонайменше 3 вершини");
        }
        Random random = new Random(seed);
        int a = random.nextInt(n);
        int b;
        do { b = random.nextInt(n); } while (b == a);
        int c;
        do { c = random.nextInt(n); } while (c == a || c == b);
        graph[a][b] = -10;
        graph[b][c] = -10;
        graph[c][a] = -10;
    }

    public static int[][] copy(int[][] graph) {
        int n = graph.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(graph[i], 0, result[i], 0, n);
        }
        return result;
    }
}