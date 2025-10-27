/*
    랭킹 데이터를 관리하는 클래스.
    게임을 켤 때마다 instance를 이용하여 loadRankings로 랭킹 데이터를 파일에서 불러온다.
    새로운 랭킹 데이터가 추가되면 addEntry로 리스트에 추가 후 정렬, saveRankings로 파일에 저장한다.
    인덱스 0~9까지 최대 10개 저장가능하며, 현재 점수가 랭킹에 들어갈 수 있는지 shouldInputName으로 확인한다.
 */





package com.team.tetris.ranking;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

//랭킹 데이터 관리 클래스
public class RankingManager {
    private static RankingManager instance; //랭킹에 정보가 처음 불러와질 때 사용할 인스턴스
    private List<RankingEntry> rankings;    //rankingEntry 리스트를 담을 변수 선언
    private static final int MAX_RANKINGS = 10; //랭킹에 저장되는 개수
    private static final String SAVE_FILE = "tetris_rankings.dat";  //파일이름

    //생성자를 private로 선언하여 무분별한 사용을 막음. (파일생성, 불러오기 등)
    //생성자를 호출할 때 (첫 호출), 파일에서 랭킹 데이터를 불러옴.
    private RankingManager() {
        rankings = loadRankings();
    }

    //첫번째 호출 시에만 인스턴스를 생성.
    public static RankingManager getInstance() {
        if (instance == null) {
            instance = new RankingManager();
        }
        //두번째부터는 무조건 여기로
        return instance;
    }

    //파일에서 랭킹 데이터를 불러오는 메소드
    //ois.readObject()로 객체를 다 불러오고, 읽어온 객체를 list<RankingEntry>로 형 변환.
    //추가로 예외 처리도 포함.
    private ArrayList<RankingEntry> loadRankings() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (ArrayList<RankingEntry>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                null,
                "랭킹 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        }
    }

    //랭킹 데이터를 파일에 저장하는 메소드
    //oos.writeObject(rankings)로 리스트 전체를 저장.
    //추가로 예외처리 포함.
    private void saveRankings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(new ArrayList<>(rankings));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "랭킹 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    //새로 랭킹데이터를 추가하는 메소드
    //랭킹 리스트에 추가 후 정렬(rankingEntry의 compareTo 메소드 사용). 
    //subList(0, MAX_RANKINGS)로 인덱스 0부터 9까지만 남김.
    public void addEntry(RankingEntry entry) {
        rankings.add(entry);
        Collections.sort(rankings);
        if (rankings.size() > MAX_RANKINGS) {
            rankings = new ArrayList<>(rankings.subList(0, MAX_RANKINGS));
        }
        saveRankings();
    }

    //현재 점수가 랭킹에 들어갈 수 있는지 확인하는 메소드.
    public boolean shouldInputName(int score) {
        if (rankings.size() < MAX_RANKINGS) {
            return true;
        }
        int lowestScore = rankings.get(rankings.size() - 1).getScore();
        return score > lowestScore;  // 현재 점수가 최하위 점수보다 클 때만 true 반환
    }

    //랭킹 리스트를 외부에서 사용하도록 반환하는 메소드.
    public List<RankingEntry> getRankings() {
        return new ArrayList<>(rankings);
    }

    //테스트용: 랭킹 초기화 메소드
    public void clearRankings() {
        rankings.clear();
        saveRankings();  // 파일도 초기화
    }

}