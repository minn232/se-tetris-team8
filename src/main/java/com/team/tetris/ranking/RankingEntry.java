package com.team.tetris.ranking;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.team.tetris.core.Difficulty;

/**
 * 랭킹 정보를 저장하는 엔트리 클래스
 * 점수 기준 내림차순으로 정렬
 */
public class RankingEntry implements Comparable<RankingEntry>, Serializable {

    private static final long serialVersionUID = 2L; // 필드 추가로 버전 변경
    private final String playerName;
    private final int score;
    private final LocalDateTime timestamp;
    private final Difficulty difficulty;

    // 기존 생성자 (하위 호환성을 위해 NORMAL 기본값 사용)
    public RankingEntry(String playerName, int score, LocalDateTime timestamp) {
        this(playerName, score, timestamp, Difficulty.NORMAL);
    }

    // 난이도를 포함하는 새 생성자
    public RankingEntry(String playerName, int score, LocalDateTime timestamp, Difficulty difficulty) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = timestamp;
        this.difficulty = difficulty;
    }

    public String getPlayerName() { 
        return playerName; 
    }
    
    public int getScore() { 
        return score; 
    }
    
    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public int compareTo(RankingEntry other) {
        return other.score - this.score;
    }
}