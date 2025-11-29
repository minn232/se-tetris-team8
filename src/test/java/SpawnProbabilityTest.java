

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.Board;
import core.Difficulty;
import core.ShapeType;

/**
 * 확률 기반 테트리스 블록 스폰 로직에 대한 단위 테스트.
 */
class SpawnProbabilityTest {

    /** 표본 개수 */
    private static final int SAMPLES = 100_000;

    /** 허용 오차(절대 퍼센트 포인트) */
    private static final double TOL = 0.05;

    @Test
    @DisplayName("EASY: I block has +20% weight")
    void testA_EasyDistribution() throws Exception {
        Board board = new Board(Difficulty.EASY, false);
        Map<ShapeType, Integer> counts = sampleCounts(board, SAMPLES);
        printDistribution("EASY", counts);

        double total = 6 * 1.0 + 1.2;
        double expectedI = 1.2 / total;
        double expectedOthers = 1.0 / total;

        for (ShapeType st : ShapeType.values()) {
            if (st == ShapeType.GRAY) continue; // GRAY는 weight 0이므로 테스트에서 제외
            double p = counts.get(st) / (double) SAMPLES;
            double exp = (st == ShapeType.I) ? expectedI : expectedOthers;
            assertWithinTol(p, exp, TOL, "EASY " + st.name());
        }
    }

    @Test
    @DisplayName("NORMAL: all blocks equal probability (~1/7)")
    void testB_NormalDistribution() throws Exception {

        Board board = new Board(Difficulty.NORMAL, false);
        Map<ShapeType, Integer> counts = sampleCounts(board, SAMPLES);
        printDistribution("NORMAL", counts);

        // GRAY를 제외한 7개 블록의 확률
        int validBlocks = (int) java.util.Arrays.stream(ShapeType.values())
            .filter(st -> st != ShapeType.GRAY)
            .count();
        double expectedEach = 1.0 / validBlocks;
        
        for (ShapeType st : ShapeType.values()) {
            if (st == ShapeType.GRAY) continue; // GRAY는 weight 0이므로 테스트에서 제외
            double p = counts.get(st) / (double) SAMPLES;
            assertWithinTol(p, expectedEach, TOL, "NORMAL " + st.name());
        }
    }

    @Test
    @DisplayName("HARD: I block has -20% weight")
    void testC_HardDistribution() throws Exception {

        Board board = new Board(Difficulty.HARD, false);
        Map<ShapeType, Integer> counts = sampleCounts(board, SAMPLES);
        printDistribution("HARD", counts);

        double total = 6 * 1.0 + 0.8;
        double expectedI = 0.8 / total;
        double expectedOthers = 1.0 / total;

        for (ShapeType st : ShapeType.values()) {
            if (st == ShapeType.GRAY) continue; // GRAY는 weight 0이므로 테스트에서 제외
            double p = counts.get(st) / (double) SAMPLES;
            double exp = (st == ShapeType.I) ? expectedI : expectedOthers;
            assertWithinTol(p, exp, TOL, "HARD " + st.name());
        }
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    private Map<ShapeType, Integer> sampleCounts(Board board, int samples) throws Exception {
        Method m = Board.class.getDeclaredMethod("pickByRoulette");
        m.setAccessible(true);

        Map<ShapeType, Integer> counts = new EnumMap<>(ShapeType.class);
        for (ShapeType st : ShapeType.values()) counts.put(st, 0);

        for (int i = 0; i < samples; i++) {
            ShapeType s = (ShapeType) m.invoke(board);
            counts.put(s, counts.get(s) + 1);
        }
        return counts;
    }

    private void printDistribution(String label, Map<ShapeType, Integer> counts) {
        System.out.println("===== " + label + " Mode Distribution =====");
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        for (ShapeType st : ShapeType.values()) {
            double percent = (counts.get(st) * 100.0) / total;
            System.out.printf("%-8s: %6d (%.2f%%)%n", st.name(), counts.get(st), percent);
        }
        System.out.println();
    }

    private void assertWithinTol(double actual, double expected, double tol, String label) {
        double diff = Math.abs(actual - expected);
        assertTrue(
            diff <= tol,
            () -> String.format(
                "%s: p=%.4f, expected=%.4f, diff=%.4f > tol=%.4f",
                label, actual, expected, diff, tol
            )
        );
    }
}
