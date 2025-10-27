package com.team.tetris.core;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 확률 기반 테트리스 블록 스폰 로직에 대한 단위 테스트.
 *
 * 핵심 아이디어
 * - 실제 게임에서 사용하는 Board의 내부 메서드 pickByRoulette()를 리플렉션으로 직접 호출하여,
 *   대량 샘플링(표본추출)을 통해 난이도별 분포가 의도한 확률과 일치하는지 검증한다.
 *
 * 테스트 정책
 * - 각 난이도(EASY, NORMAL, HARD)에 대해 SAMPLES(기본 100,000회)만큼 블록을 랜덤 선택.
 * - 각 ShapeType이 선택된 빈도를 집계하여 실제 비율(%)을 계산.
 * - 기대 확률 대비 허용 오차(±5%p) 내에 있는지 검증한다.
 *
 * 참고
 * - NORMAL: 모든 블럭 동일 가중치 → 각 1/7 ≈ 14.2857%
 * - EASY  : I 블럭 가중치 +20% (I=1.2, others=1.0)
 * - HARD  : I 블럭 가중치 -20% (I=0.8, others=1.0)
 *
 * 출력
 * - 콘솔에는 영어로 "===== <DIFFICULTY> Mode Distribution =====" 형식으로
 *   각 ShapeType의 선택 횟수와 비율(%)을 보기 좋게 출력한다.
 */
class SpawnProbabilityTest {

    /** 표본 개수(반복 횟수). 100,000회면 95% 신뢰구간 기준 약 ±0.2%p 수준으로 상당히 정밀함. */
    private static final int SAMPLES = 100_000;

    /** 허용 오차(절대 퍼센트 포인트, 예: 0.05 = ±5%p). 요구사항에 맞춰 ±5%p로 설정. */
    private static final double TOL = 0.05;

    /**
     * EASY 모드 검증
     * - I 블럭 가중치 1.2, 나머지는 1.0
     * - 기대확률: I = 1.2 / (6*1.0 + 1.2), others = 1.0 / (6*1.0 + 1.2)
     * - 출력 순서를 EASY → NORMAL → HARD 로 보기 위해 메서드명을 testA_*로 둔다.
     */
    @Test
    @DisplayName("EASY: I block has +20% weight")
    void testA_EasyDistribution() throws Exception {
        Board board = new Board(Difficulty.EASY);
        Map<ShapeType, Integer> counts = sampleCounts(board, SAMPLES);
        printDistribution("EASY", counts);

        double total = 6 * 1.0 + 1.2;
        double expectedI = 1.2 / total;
        double expectedOthers = 1.0 / total;

        for (ShapeType st : ShapeType.values()) {
            double p = counts.get(st) / (double) SAMPLES;
            double exp = (st == ShapeType.I) ? expectedI : expectedOthers;
            assertWithinTol(p, exp, TOL, "EASY " + st.name());
        }
    }

    /**
     * NORMAL 모드 검증
     * - 모든 블럭 동일 가중치(1.0) → 각 1/7 ≈ 0.142857
     * - 출력 순서를 EASY → NORMAL → HARD 로 보기 위해 메서드명을 testB_*로 둔다.
     */
    @Test
    @DisplayName("NORMAL: all blocks equal probability (~1/7)")
    void testB_NormalDistribution() throws Exception {
        Board board = new Board(Difficulty.NORMAL);
        Map<ShapeType, Integer> counts = sampleCounts(board, SAMPLES);
        printDistribution("NORMAL", counts);

        double expectedEach = 1.0 / ShapeType.values().length;
        for (ShapeType st : ShapeType.values()) {
            double p = counts.get(st) / (double) SAMPLES;
            assertWithinTol(p, expectedEach, TOL, "NORMAL " + st.name());
        }
    }

    /**
     * HARD 모드 검증
     * - I 블럭 가중치 0.8, 나머지는 1.0
     * - 기대확률: I = 0.8 / (6*1.0 + 0.8), others = 1.0 / (6*1.0 + 0.8)
     * - 출력 순서를 EASY → NORMAL → HARD 로 보기 위해 메서드명을 testC_*로 둔다.
     */
    @Test
    @DisplayName("HARD: I block has -20% weight")
    void testC_HardDistribution() throws Exception {
        Board board = new Board(Difficulty.HARD);
        Map<ShapeType, Integer> counts = sampleCounts(board, SAMPLES);
        printDistribution("HARD", counts);

        double total = 6 * 1.0 + 0.8;
        double expectedI = 0.8 / total;
        double expectedOthers = 1.0 / total;

        for (ShapeType st : ShapeType.values()) {
            double p = counts.get(st) / (double) SAMPLES;
            double exp = (st == ShapeType.I) ? expectedI : expectedOthers;
            assertWithinTol(p, exp, TOL, "HARD " + st.name());
        }
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    /**
     * Board.pickByRoulette() 메서드를 리플렉션으로 호출하여
     * 실제 게임 스폰 로직과 동일한 방식으로 무작위 블럭을 뽑는다.
     *
     * @param board   테스트 대상 Board
     * @param samples 반복 횟수(표본 개수)
     * @return ShapeType별 선택 횟수 맵
     */
    private Map<ShapeType, Integer> sampleCounts(Board board, int samples) throws Exception {
        // private 메서드 접근 허용
        Method m = Board.class.getDeclaredMethod("pickByRoulette");
        m.setAccessible(true);

        Map<ShapeType, Integer> counts = new EnumMap<>(ShapeType.class);
        for (ShapeType st : ShapeType.values()) counts.put(st, 0);

        // SAMPLES 회 반복 샘플링
        for (int i = 0; i < samples; i++) {
            ShapeType s = (ShapeType) m.invoke(board); // 실제 게임과 동일한 확률 로직 호출
            counts.put(s, counts.get(s) + 1);
        }
        return counts;
    }

    /**
     * 콘솔에 난이도 라벨과 함께 ShapeType별 선택 횟수/비율(%)을 출력한다.
     * - 출력은 영어로 남겨 윈도우 CP949 콘솔 등에서도 깨지지 않도록 함.
     * - 예: "===== EASY Mode Distribution ====="
     */
    private void printDistribution(String label, Map<ShapeType, Integer> counts) {
        System.out.println("===== " + label + " Mode Distribution =====");
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        for (ShapeType st : ShapeType.values()) {
            double percent = (counts.get(st) * 100.0) / total;
            System.out.printf("%-8s: %6d (%.2f%%)%n", st.name(), counts.get(st), percent);
        }
        System.out.println();
    }

    /**
     * 실제 비율(actual)이 기대 비율(expected)에서 tol(허용오차) 이내인지 검증한다.
     * - tol은 절대 퍼센트 포인트 기준(예: 0.05 = ±5%p).
     */
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
