import java.util.Arrays;

public class SequentialCorrectnessTests {

    private static final int INF = GraphGenerator.INF;
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("Тести послідовного алгоритму\n");
        runTest("Тест 1: Багатокроковий шлях ", testSimpleChain());
        runTest("Тест 2: Обхідний шлях коротший за прямий", testTriangleWithShortcut());
        runTest("Тест 3: Збереження INF для недосяжних пар вершин", testDisconnected());
        runTest("Тест 4: Одна вершина", testSingleVertex());
        runTest("Тест 5: Від'ємні ваги", testNegativeWeightsDAG());
        runTest("Тест 6: Від'ємний цикл", testNegativeCycleDetection());
        runTest("Тест 7: Великий щільний граф (n=1000, density = 0.9)", testLargeDenseGraph());
        runTest("Тест 8: Великий розріджений граф (n=1000, density = 0.1)\n", testLargeSparseGraph());
        System.out.println(passed + " пройдено, " + failed + " провалено");
    }

    private static boolean testSimpleChain() {
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
        SequentialFloydWarshall.solve(graph);
        return Arrays.deepEquals(graph, expected);
    }

    private static boolean testTriangleWithShortcut() {
        int[][] graph = {
                {0,   5,   INF, 10 },
                {INF, 0,   3,   INF},
                {INF, INF, 0,   1  },
                {INF, INF, INF, 0  }
        };
        int[][] expected = {
                {0,   5,   8,   9  },
                {INF, 0,   3,   4  },
                {INF, INF, 0,   1  },
                {INF, INF, INF, 0  }
        };
        SequentialFloydWarshall.solve(graph);
        return Arrays.deepEquals(graph, expected);
    }

    private static boolean testDisconnected() {
        int[][] graph = {
                {0,   INF, INF},
                {INF, 0,   INF},
                {INF, INF, 0  }
        };
        int[][] expected = GraphGenerator.copy(graph);
        SequentialFloydWarshall.solve(graph);
        return Arrays.deepEquals(graph, expected);
    }

    private static boolean testSingleVertex() {
        int[][] graph = {{0}};
        int[][] expected = {{0}};
        SequentialFloydWarshall.solve(graph);
        return Arrays.deepEquals(graph, expected);
    }

    private static boolean testNegativeWeightsDAG() {
        int[][] graph = {
                {0,   3,   5,   INF},
                {INF, 0,   INF, -2 },
                {INF, INF, 0,   1  },
                {INF, INF, INF, 0  }
        };
        int[][] expected = {
                {0,   3,   5,   1  },
                {INF, 0,   INF, -2 },
                {INF, INF, 0,   1  },
                {INF, INF, INF, 0  }
        };
        SequentialFloydWarshall.solve(graph);
        return Arrays.deepEquals(graph, expected);
    }

    private static boolean testNegativeCycleDetection() {
        int[][] graph = GraphGenerator.generate(50, 0.3, 1, 100, 42L);
        GraphGenerator.injectNegativeCycle(graph, 42L);
        SequentialFloydWarshall.solve(graph);
        return SequentialFloydWarshall.hasNegativeCycle(graph);
    }

    private static boolean testLargeSparseGraph() {
        int n = 1000;
        int[][] graph = GraphGenerator.generate(n, 0.01, 1000, 2000, 42L);
        for (int i = 0; i < n - 1; i++) {
            graph[i][i + 1] = 1;
        }
        SequentialFloydWarshall.solve(graph);
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (graph[i][j] != j - i) return false;
            }
        }
        return true;
    }

    private static boolean testLargeDenseGraph() {
        int n = 1000;
        int[][] graph = GraphGenerator.generate(n, 0.9, 1000, 2000, 7L);
        for (int i = 0; i < n - 1; i++) {
            graph[i][i + 1] = 1;
        }
        SequentialFloydWarshall.solve(graph);
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (graph[i][j] != j - i) return false;
            }
        }
        return true;
    }

    private static void runTest(String name, boolean result) {
        System.out.println((result ? "Passed " : "Failed ") + name);
        if (result) passed++; else failed++;
    }
}