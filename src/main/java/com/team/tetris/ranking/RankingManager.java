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
    private static RankingManager normalInstance;
    private static RankingManager itemInstance;
    private ArrayList<RankingEntry> rankings;
    private static final int MAX_RANKINGS = 10;
    private String saveFile;

    private RankingManager(String saveFile) {
        this.saveFile = saveFile;
        rankings = loadRankings();
    }

    public static RankingManager getInstance(String saveFile) {
        if ("item_rankings.dat".equals(saveFile)) {
            if (itemInstance == null) {
                itemInstance = new RankingManager(saveFile);
            }
            return itemInstance;
        }
        
        if (normalInstance == null) {
            normalInstance = new RankingManager("normal_rankings.dat");
        }
        return normalInstance;
    }

    public static RankingManager getInstance() {
        return getInstance("normal_rankings.dat");
    }

    //파일에서 랭킹 데이터를 불러오는 메소드
    //ois.readObject()로 객체를 다 불러오고, 읽어온 객체를 list<RankingEntry>로 형 변환.
    //추가로 예외 처리도 포함.
    private ArrayList<RankingEntry> loadRankings() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
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