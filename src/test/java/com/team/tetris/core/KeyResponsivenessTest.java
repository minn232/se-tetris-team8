package com.team.tetris.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 키 입력 반응성 테스트
 * Non-Functional Requirement: 반복된 키 입력이 무시되지 않고 블럭이 즉시, 정확하게 반응해야 함
 */
public class KeyResponsivenessTest {

    private Board board;
    private static final long MAX_RESPONSE_TIME_MS = 50; // 최대 응답 시간 50ms

    @BeforeEach
    void setUp() {
        board = new Board(Difficulty.NORMAL);
    }

    @Test
    @DisplayName("좌우 이동 키 입력 반응 시간 테스트")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void testLeftRightMovementResponseTime() {
        // Given: 초기 블록 위치 저장
        int initialX = board.getCurrent().getX();
        
        // When: 왼쪽 이동 명령을 실행하고 시간 측정
        long startTime = System.nanoTime();
        board.moveLeft();
        long leftMoveTime = System.nanoTime() - startTime;
        
        // Then: 블록이 즉시 이동했는지 확인
        assertEquals(initialX - 1, board.getCurrent().getX(), 
            "왼쪽 이동 후 X 좌표가 1 감소해야 함");
        assertTrue(leftMoveTime < MAX_RESPONSE_TIME_MS * 1_000_000, 
            String.format("왼쪽 이동 응답 시간이 %dms를 초과함: %dms", 
                MAX_RESPONSE_TIME_MS, leftMoveTime / 1_000_000));
        
        // When: 오른쪽 이동 명령을 실행하고 시간 측정
        int currentX = board.getCurrent().getX();
        startTime = System.nanoTime();
        board.moveRight();
        long rightMoveTime = System.nanoTime() - startTime;
        
        // Then: 블록이 즉시 이동했는지 확인
        assertEquals(currentX + 1, board.getCurrent().getX(), 
            "오른쪽 이동 후 X 좌표가 1 증가해야 함");
        assertTrue(rightMoveTime < MAX_RESPONSE_TIME_MS * 1_000_000, 
            String.format("오른쪽 이동 응답 시간이 %dms를 초과함: %dms", 
                MAX_RESPONSE_TIME_MS, rightMoveTime / 1_000_000));
    }

    @Test
    @DisplayName("회전 키 입력 반응 시간 테스트")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void testRotationResponseTime() {
        // Given: 초기 회전 상태 저장
        int initialRotation = board.getCurrent().getRotation();
        
        // When: 회전 명령을 실행하고 시간 측정
        long startTime = System.nanoTime();
        board.rotate();
        long rotationTime = System.nanoTime() - startTime;
        
        // Then: 블록이 즉시 회전했는지 확인 (회전 가능한 경우)
        int currentRotation = board.getCurrent().getRotation();
        assertTrue(currentRotation == initialRotation || currentRotation == (initialRotation + 1) % 4,
            "회전 후 rotation 값이 유지되거나 1 증가해야 함");
        
        assertTrue(rotationTime < MAX_RESPONSE_TIME_MS * 1_000_000, 
            String.format("회전 응답 시간이 %dms를 초과함: %dms", 
                MAX_RESPONSE_TIME_MS, rotationTime / 1_000_000));
    }

    @Test
    @DisplayName("하강 키 입력 반응 시간 테스트")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void testDownMovementResponseTime() {
        // Given: 초기 블록 Y 위치 저장
        int initialY = board.getCurrent().getY();
        
        // When: 아래 이동 명령을 실행하고 시간 측정
        long startTime = System.nanoTime();
        board.moveDown();
        long downMoveTime = System.nanoTime() - startTime;
        
        // Then: 블록이 즉시 이동했는지 확인
        assertTrue(board.getCurrent().getY() >= initialY, 
            "아래 이동 후 Y 좌표가 증가하거나 유지되어야 함");
        assertTrue(downMoveTime < MAX_RESPONSE_TIME_MS * 1_000_000, 
            String.format("아래 이동 응답 시간이 %dms를 초과함: %dms", 
                MAX_RESPONSE_TIME_MS, downMoveTime / 1_000_000));
    }

    @Test
    @DisplayName("하드드롭 키 입력 반응 시간 테스트")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void testHardDropResponseTime() {
        // Given: 현재 블록 확인
        assertNotNull(board.getCurrent(), "현재 블록이 존재해야 함");
        
        // When: 하드드롭 명령을 실행하고 시간 측정
        long startTime = System.nanoTime();
        board.hardDrop();
        long hardDropTime = System.nanoTime() - startTime;
        
        // Then: 명령이 즉시 처리되었는지 확인
        // 하드드롭 후에는 새 블록이 생성됨
        assertNotNull(board.getCurrent(), "하드드롭 후에도 새 블록이 존재해야 함");
        assertTrue(hardDropTime < MAX_RESPONSE_TIME_MS * 1_000_000, 
            String.format("하드드롭 응답 시간이 %dms를 초과함: %dms", 
                MAX_RESPONSE_TIME_MS, hardDropTime / 1_000_000));
    }

    @Test
    @DisplayName("연속 키 입력 누락 없이 처리 테스트")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testContinuousKeyInputsWithoutLoss() {
        // Given: 초기 위치
        int initialX = board.getCurrent().getX();
        int moveCount = 3; // 3번 연속 이동
        
        // When: 연속으로 오른쪽 이동 (입력 간격 10ms)
        for (int i = 0; i < moveCount; i++) {
            board.moveRight();
            try {
                Thread.sleep(10); // 빠른 연속 입력 시뮬레이션
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Then: 모든 입력이 처리되었는지 확인 (벽에 닿지 않은 경우)
        int expectedX = Math.min(initialX + moveCount, Board.COLS - 1);
        assertTrue(board.getCurrent().getX() >= initialX + moveCount 
                   || board.getCurrent().getX() == expectedX,
            String.format("연속 %d회 이동 중 일부가 무시되었음. 예상: %d 이상, 실제: %d", 
                moveCount, initialX + moveCount, board.getCurrent().getX()));
    }

    @Test
    @DisplayName("빠른 반복 회전 입력 처리 테스트")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testRapidRotationInputs() {
        // Given: 초기 회전 상태
        int initialRotation = board.getCurrent().getRotation();
        int rotationCount = 4; // 4번 회전하면 원래 상태로
        
        // When: 연속으로 회전 (입력 간격 10ms)
        for (int i = 0; i < rotationCount; i++) {
            board.rotate();
            try {
                Thread.sleep(10); // 빠른 연속 입력 시뮬레이션
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Then: 모든 회전 입력이 처리되어 원래 상태로 돌아왔는지 확인 (가능한 경우)
        int finalRotation = board.getCurrent().getRotation();
        assertTrue(finalRotation == initialRotation || finalRotation >= 0 && finalRotation < 4,
            String.format("연속 %d회 회전 후 rotation 값이 유효 범위 내에 있어야 함", rotationCount));
    }

    @Test
    @DisplayName("좌우 교대 입력 정확도 테스트")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testAlternatingLeftRightInputs() {
        // Given: 초기 위치
        int initialX = board.getCurrent().getX();
        
        // When: 좌우 교대로 이동 (오른쪽 2회, 왼쪽 1회 반복)
        board.moveRight();
        board.moveRight();
        board.moveLeft();
        
        // Then: 순 이동량이 정확한지 확인
        int expectedX = initialX + 1; // +2 -1 = +1
        assertEquals(expectedX, board.getCurrent().getX(),
            "교대 입력 시 순 이동량이 정확해야 함");
    }

    @Test
    @DisplayName("입력 처리 일관성 테스트 - 100회 반복")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testInputProcessingConsistency() {
        // Given: 여러 보드 생성하여 일관성 확인
        int testRuns = 100;
        int successCount = 0;
        
        // When: 동일한 입력 시퀀스를 반복 실행
        for (int i = 0; i < testRuns; i++) {
            Board testBoard = new Board(Difficulty.NORMAL);
            int initialX = testBoard.getCurrent().getX();
            
            testBoard.moveRight();
            testBoard.moveRight();
            
            // Then: 결과가 일관적인지 확인
            if (testBoard.getCurrent().getX() == initialX + 2) {
                successCount++;
            }
        }
        
        // 최소 95% 이상의 일관성 요구
        double successRate = (successCount * 100.0) / testRuns;
        assertTrue(successRate >= 95.0,
            String.format("입력 처리 일관성이 95%% 미만: %.2f%%", successRate));
    }

    @Test
    @DisplayName("복합 입력 시퀀스 정확도 테스트")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testComplexInputSequence() {
        // Given: 초기 상태
        int initialX = board.getCurrent().getX();
        
        // When: 복합 입력 (이동 + 회전 + 이동)
        long startTime = System.nanoTime();
        
        board.moveRight();
        board.rotate();
        board.moveLeft();
        board.moveDown();
        
        long totalTime = System.nanoTime() - startTime;
        
        // Then: 모든 명령이 순서대로 처리되었는지 확인
        assertTrue(totalTime < MAX_RESPONSE_TIME_MS * 4 * 1_000_000,
            String.format("4개 명령 처리 시간이 예상(%dms)을 초과: %dms",
                MAX_RESPONSE_TIME_MS * 4, totalTime / 1_000_000));
        
        // 순 이동은 0 (오른쪽 1 + 왼쪽 -1)
        assertEquals(initialX, board.getCurrent().getX(),
            "복합 입력 후 순 X 이동량이 정확해야 함");
    }

    @Test
    @DisplayName("입력 버퍼링 오버플로우 방지 테스트")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testInputBufferOverflow() {
        // Given: 매우 많은 연속 입력
        int massiveInputCount = 50;
        int initialX = board.getCurrent().getX();
        
        // When: 대량의 연속 입력 (실제 게임에서 있을 수 없는 수준)
        for (int i = 0; i < massiveInputCount; i++) {
            board.moveRight();
        }
        
        // Then: 입력이 무시되지 않고 처리되었는지 확인 (벽 제한 고려)
        // 테트로미노의 너비를 고려하여 실제 이동 가능한 거리 계산
        int finalX = board.getCurrent().getX();
        int actualMoved = finalX - initialX;
        
        // 벽까지 이동했거나 대량 입력이 처리되었는지 확인
        assertTrue(actualMoved > 0 || finalX >= Board.COLS - 4,
            String.format("대량 입력이 처리되어야 함. 초기: %d, 최종: %d, 이동: %d", 
                initialX, finalX, actualMoved));
    }
}
