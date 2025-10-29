package com.team.tetris.ranking;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 랭킹 정보를 저장하는 엔트리 클래스
 * 점수 기준 내림차순으로 정렬
 */
public class RankingEntry implements Comparable<RankingEntry>, Serializable {

    private static final long serialVersionUID = 1L;
    private final String playerName;
    private final int score;
    private final LocalDateTime timestamp;

    public RankingEntry(String playerName, int score, LocalDateTime timestamp) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = timestamp;
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

    @Override
    public int compareTo(RankingEntry other) {
        return other.score - this.score;
    }
}