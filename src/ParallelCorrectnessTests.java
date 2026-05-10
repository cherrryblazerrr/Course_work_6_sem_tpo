import java.util.Arrays;

public class ParallelCorrectnessTests {

    private static final int THREAD_COUNTS = 4;
    private static final int THRESHOLD = 64;
    private static final int INF = GraphGenerator.INF;

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("Тести паралельного алгоритму");
        System.out.println("Параметри: parallelism=" + THREAD_COUNTS
                + ", threshold=" + THRESHOLD);
        System.out.println();

        runTest("Тест 1: Багатокроковий шлях (n=4, з відомою відповіддю)",
                testHandcraftedExample());
        runTest("Тест 2: Орієнтований граф з додатними вагами (n=1000, density=0.5)",
                testParallelEqualsSequential(1000, 0.5, 1, 100, 42L));
        runTest("Тест 3: Повний граф з додатними вагами (n=500, density=1.0)",
                testParallelEqualsSequential(500, 1.0, 1, 100, 123L));
        runTest("Тест 4: Розріджений граф (n=1000, density≈5/n)",
                testParallelEqualsSequential(1000, 5.0 / 1000, 1, 100, 7L));
        runTest("Тест 5: Граф з від'ємними та додатними вагами (n=500, density = 0.5)",
                testDagWithNegativeWeights(500, 9L));
        runTest("Тест 6: Виявлення від'ємного циклу (n=300, density = 0.3)\n",
                testNegativeCycleDetection(300, 3L));
        System.out.println(passed + " пройдено, " + failed + " провалено");
    }

    private static boolean testHandcraftedExample() {
        int[][] graph = {
                {0,   1,   INF, INF},
                {INF, 0,   2,   INF},
                {INF, INF, 0,   3  },
                {INF, INF, INF, 0  }
        };
        int[][] expected = {
                {0,   1,   3,   6  },
                {INF, 0,   2,   5  },
                {INF, INF, 0,   3  },
                {INF, INF, INF, 0  }
        };

        int[][] result = GraphGenerator.copy(graph);
        ParallelFloydWarshall.solve(result, THREAD_COUNTS, THRESHOLD);

        return Arrays.deepEquals(result, expected);
    }

    private static boolean testParallelEqualsSequential(
            int n, double density, int minW, int maxW, long seed) {
        int[][] graph = GraphGenerator.generate(n, density, minW, maxW, seed);
        return parallelEqualsSequential(graph);
    }

    private static boolean testDagWithNegativeWeights(int n, long seed) {
        int[][] graph = GraphGenerator.generate(n, 0.5, -10, 100, seed);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                graph[i][j] = GraphGenerator.INF;
            }
        }
        return parallelEqualsSequential(graph);
    }

    private static boolean testNegativeCycleDetection(int n, long seed) {
        int[][] graph = GraphGenerator.generate(n, 0.3, 1, 100, seed);
        GraphGenerator.injectNegativeCycle(graph, seed);

        int[][] seq = GraphGenerator.copy(graph);
        int[][] par = GraphGenerator.copy(graph);
        SequentialFloydWarshall.solve(seq);
        ParallelFloydWarshall.solve(par, THREAD_COUNTS, THRESHOLD);

        return SequentialFloydWarshall.hasNegativeCycle(seq)
                && SequentialFloydWarshall.hasNegativeCycle(par);
    }

    private static boolean parallelEqualsSequential(int[][] graph) {
        int[][] seq = GraphGenerator.copy(graph);
        int[][] par = GraphGenerator.copy(graph);
        SequentialFloydWarshall.solve(seq);
        ParallelFloydWarshall.solve(par, THREAD_COUNTS, THRESHOLD);
        return Arrays.deepEquals(seq, par);
    }

    private static void runTest(String name, boolean result) {
        System.out.println((result ? "Passed " : "Failed ") + name);
        if (result) passed++; else failed++;
    }
}