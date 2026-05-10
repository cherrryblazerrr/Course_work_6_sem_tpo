import java.util.function.Consumer;

public class Benchmark {

    private static final int RUNS = 20;
    private static final int WARMUP = 5;

    private static final int[] SIZES = {500, 1000, 1500, 2000, 2500, 3000};
    private static final int FIXED_PARALLELISM = 8;
    private static final int FIXED_THRESHOLD = 256;

    private static final int FIXED_N = 2000;
    private static final int[] PARALLELISM_VALUES = {1, 2, 4, 6, 8, 10, 12, 16};
    private static final int[] THRESHOLDS = {64, 128, 256, 512, 1024};

    public static void main(String[] args) {
        warmupJVM();
        runSequentialAnalysis();
        runSeries1();
        runSeries2a();
        runSeries2b();
    }

    private static void warmupJVM() {
        System.out.println("Розігрів JVM...");
        int[][] graph = GraphGenerator.generate(1000, 0.5, 1, 100, 42L);
        for (int i = 0; i < 3; i++) {
            SequentialFloydWarshall.solve(GraphGenerator.copy(graph));
            ParallelFloydWarshall.solve(GraphGenerator.copy(graph), 4, 100);
        }
        System.out.println("Готово.\n");
    }

    public static void runSequentialAnalysis() {
        System.out.println("Аналіз швидкодії послідовного алгоритму");
        System.out.printf("| %5s | %12s |%n", "n", "T_seq, мс");
        System.out.println("|-------|--------------|");

        for (int n : SIZES) {
            int[][] graph = GraphGenerator.generate(n, 0.5, 1, 100, 42L);
            long tSeq = measureTime(graph, SequentialFloydWarshall::solve);
            System.out.printf("| %5d | %12.2f |%n", n, tSeq / 1e6);
        }
        System.out.println();
    }

    public static void runSeries1() {
        System.out.println("Серія 1: розмір даних (parallelism="
                + FIXED_PARALLELISM + ", threshold=" + FIXED_THRESHOLD + ")");
        System.out.printf("%-6s | %10s | %10s | %8s%n", "n", "T_seq, мс", "T_par, мс", "Speedup");
        System.out.println("-------|------------|------------|---------");

        for (int n : SIZES) {
            int[][] graph = GraphGenerator.generate(n, 0.5, 1, 100, 42L);
            long tSeq = measureTime(graph, SequentialFloydWarshall::solve);
            long tPar = measureTime(graph,
                    g -> ParallelFloydWarshall.solve(g, FIXED_PARALLELISM, FIXED_THRESHOLD));
            double speedup = (double) tSeq / tPar;
            System.out.printf("%-6d | %10.2f | %10.2f | %8.2f%n",
                    n, tSeq / 1e6, tPar / 1e6, speedup);
        }
        System.out.println();
    }

    public static void runSeries2a() {
        System.out.println("Серія 2a: кількість потоків (n="
                + FIXED_N + ", threshold=" + FIXED_THRESHOLD + ")");

        int[][] graph = GraphGenerator.generate(FIXED_N, 0.5, 1, 100, 42L);
        long tSeq = measureTime(graph, SequentialFloydWarshall::solve);
        System.out.printf("T_seq = %.2f мс%n", tSeq / 1e6);

        System.out.printf("%-8s | %10s | %8s%n", "threads", "T_par, мс", "Speedup");
        System.out.println("---------|------------|---------");

        for (int parallelism : PARALLELISM_VALUES) {
            long tPar = measureTime(graph,
                    g -> ParallelFloydWarshall.solve(g, parallelism, FIXED_THRESHOLD));
            double speedup = (double) tSeq / tPar;
            System.out.printf("%-8d | %10.2f | %8.2f%n",
                    parallelism, tPar / 1e6, speedup);
        }
        System.out.println();
    }

    public static void runSeries2b() {
        System.out.println("Серія 2b: поріг розбиття (n="
                + FIXED_N + ", parallelism=" + FIXED_PARALLELISM + ")");

        int[][] graph = GraphGenerator.generate(FIXED_N, 0.5, 1, 100, 42L);
        long tSeq = measureTime(graph, SequentialFloydWarshall::solve);
        System.out.printf("T_seq = %.2f мс%n", tSeq / 1e6);

        System.out.printf("%-10s | %10s | %8s%n", "threshold", "T_par, мс", "Speedup");
        System.out.println("-----------|------------|---------");

        for (int threshold : THRESHOLDS) {
            long tPar = measureTime(graph,
                    g -> ParallelFloydWarshall.solve(g, FIXED_PARALLELISM, threshold));
            double speedup = (double) tSeq / tPar;
            System.out.printf("%-10d | %10.2f | %8.2f%n",
                    threshold, tPar / 1e6, speedup);
        }
        System.out.println();
    }

    private static long measureTime(int[][] graph, Consumer<int[][]> algorithm) {
        for (int i = 0; i < WARMUP; i++) {
            algorithm.accept(GraphGenerator.copy(graph));
        }

        long total = 0;
        int measured = RUNS - WARMUP;
        for (int i = 0; i < measured; i++) {
            int[][] copy = GraphGenerator.copy(graph);
            long start = System.nanoTime();
            algorithm.accept(copy);
            total += System.nanoTime() - start;
        }
        return total / measured;
    }
}