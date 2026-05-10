public class SequentialFloydWarshall {

    public static void solve(int[][] dist) {
        int n = dist.length;
        int INF = GraphGenerator.INF;
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < INF && dist[k][j] < INF
                            && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
    }

    public static boolean hasNegativeCycle(int[][] dist) {
        for (int i = 0; i < dist.length; i++) {
            if (dist[i][i] < 0) return true;
        }
        return false;
    }
}