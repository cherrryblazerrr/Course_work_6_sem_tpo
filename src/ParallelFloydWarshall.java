import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelFloydWarshall {

    public static void solve(int[][] dist, int thread_counts, int threshold) {
        ForkJoinPool pool = new ForkJoinPool(thread_counts);
        try {
            int n = dist.length;
            for (int k = 0; k < n; k++) {
                pool.invoke(new FloydWarshallTask(dist, k, 0, n, threshold));
            }
        } finally {
            pool.shutdown();
        }
    }

    private static class FloydWarshallTask extends RecursiveAction {

        private final int[][] dist;
        private final int k;
        private final int startRow;
        private final int endRow;
        private final int threshold;

        FloydWarshallTask(int[][] dist, int k, int startRow, int endRow, int threshold) {
            this.dist = dist;
            this.k = k;
            this.startRow = startRow;
            this.endRow = endRow;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            if (endRow - startRow <= threshold) {
                int n = dist.length;
                int INF = GraphGenerator.INF;
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < n; j++) {
                        if (dist[i][k] < INF && dist[k][j] < INF
                                && dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            } else {
                int mid = (startRow + endRow) / 2;
                FloydWarshallTask left  = new FloydWarshallTask(dist, k, startRow, mid, threshold);
                FloydWarshallTask right = new FloydWarshallTask(dist, k, mid, endRow, threshold);
                left.fork();
                right.compute();
                left.join();
            }
        }
    }
}