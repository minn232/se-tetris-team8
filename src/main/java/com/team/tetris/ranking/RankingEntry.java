/*
    랭킹 정보를 파일로 저장하는 클래스.
    playerName(String), score(int), timestamp(LocalDateTime) 정보를 저장.
    comparableTo의 compareTo 메소드를 오버라이드하여 점수 기준으로 내림차순으로 정렬하도록 한다.
 */





package com.team.tetris.ranking;

import java.io.Serializable;
import java.time.LocalDateTime;

//ranking entry 객체끼리 점수를 비교하고, 파일에 저장하도록 한다.
public class RankingEntry implements Comparable<RankingEntry>, Serializable {

    private static final long serialVersionUID = 1L;    //파일을 불러오는 ID
    private final String playerName;
    private final int score;
    private final LocalDateTime timestamp;

    //생성자. 이름과 점수, 시간을 받는다.
    public RankingEntry(String playerName, int score, LocalDateTime timestamp) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = timestamp;
    }

    //받은 이름/점수/시간을 외부에서 사용할 수 있도록 함
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public LocalDateTime getTimestamp() { return timestamp; }

    //comperTo 메소드에 추가로 점수를 비교하도록 한다. 점수 기준으로 내림차순.
    @Override
    public int compareTo(RankingEntry other) {
        return other.score - this.score;
    }
}