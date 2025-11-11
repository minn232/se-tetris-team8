package ranking;

import java.io.File;
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

/**
 * 랭킹 데이터 관리 클래스
 * - 일반 모드와 아이템 모드의 랭킹을 분리하여 관리
 * - 최대 10개의 랭킹 데이터를 파일에 저장/로드
 */
public class RankingManager {
    private static final int MAX_RANKINGS = 10;
    private static final String DATA_DIR = "src/main/data/";
    private static final String NORMAL_RANKING_FILE = "normal_rankings.dat";
    private static final String ITEM_RANKING_FILE = "item_rankings.dat";
    
    private static RankingManager normalInstance;
    private static RankingManager itemInstance;
    
    private ArrayList<RankingEntry> rankings;
    private final String saveFilePath;

    private RankingManager(String fileName) {
        ensureDataDirectoryExists();
        this.saveFilePath = DATA_DIR + fileName;
        this.rankings = loadRankings();
    }

    public static RankingManager getInstance(String fileName) {
        if (ITEM_RANKING_FILE.equals(fileName)) {
            if (itemInstance == null) {
                itemInstance = new RankingManager(fileName);
            }
            return itemInstance;
        }
        
        if (normalInstance == null) {
            normalInstance = new RankingManager(NORMAL_RANKING_FILE);
        }
        return normalInstance;
    }

    public static RankingManager getInstance() {
        return getInstance(NORMAL_RANKING_FILE);
    }
    
    private void ensureDataDirectoryExists() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<RankingEntry> loadRankings() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFilePath))) {
            return (ArrayList<RankingEntry>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                null,
                "An error occurred while loading ranking data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        }
    }

    private void saveRankings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFilePath))) {
            oos.writeObject(new ArrayList<>(rankings));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "An error occurred while saving ranking data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void addEntry(RankingEntry entry) {
        rankings.add(entry);
        Collections.sort(rankings);
        if (rankings.size() > MAX_RANKINGS) {
            rankings.subList(MAX_RANKINGS, rankings.size()).clear();
        }
        saveRankings();
    }

    public boolean shouldInputName(int score) {
        if (rankings.size() < MAX_RANKINGS) {
            return true;
        }
        return score > rankings.get(MAX_RANKINGS - 1).getScore();
    }

    public List<RankingEntry> getRankings() {
        return new ArrayList<>(rankings);
    }

    public void clearRankings() {
        rankings.clear();
        saveRankings();
    }
}
