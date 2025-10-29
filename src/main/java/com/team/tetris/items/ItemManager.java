package com.team.tetris.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 아이템 블록들을 관리하는 매니저 클래스
 */
public class ItemManager {
    private static final int LINES_FOR_ITEM = 2; // 10줄마다 아이템 생성
    
    private final List<ItemBlock> availableItems;
    private final Random random;
    private int totalLinesCleared;
    private final boolean itemMode;
    
    public ItemManager(boolean itemMode) {
        this.itemMode = itemMode;
        this.availableItems = new ArrayList<>();
        this.random = new Random();
        this.totalLinesCleared = 0;
        
        if (itemMode) {
            initializeItems();
        }
    }
    
    /**
     * 사용 가능한 아이템 블록들을 초기화
     */
    private void initializeItems() {
        availableItems.add(new LineBlock());
        availableItems.add(new SlowBlock());
        availableItems.add(new BombBlock());
        availableItems.add(new WeightBlock());
    }
    
    /**
     * 줄이 삭제될 때 호출되어 아이템 생성 여부를 결정
     * @param linesCleared 이번에 삭제된 줄 수
     * @return 아이템 블록 (생성되지 않으면 null)
     */
    public void onLinesCleared(int linesCleared) {
        if (!itemMode || availableItems.isEmpty()) {
            return;
        }
        
        totalLinesCleared += linesCleared;
    }
    
    public boolean shouldCreateItem() {
        return totalLinesCleared >= LINES_FOR_ITEM;
    }
    
    public ItemBlock generateItem() {
        if (shouldCreateItem()) {
            totalLinesCleared = totalLinesCleared % LINES_FOR_ITEM;
            return getRandomItem();
        }
        return null;
    }
    
    /**
     * 랜덤한 아이템 블록을 반환
     * @return 랜덤 아이템 블록
     */
    private ItemBlock getRandomItem() {
        if (availableItems.isEmpty()) {
            return null;
        }
        return availableItems.get(random.nextInt(availableItems.size()));
    }
    
    /**
     * 특정 아이템 블록을 이름으로 검색
     * @param name 아이템 이름
     * @return 아이템 블록 (없으면 null)
     */
    public ItemBlock getItemByName(String name) {
        return availableItems.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 사용 가능한 모든 아이템 블록 목록을 반환
     * @return 아이템 블록 리스트
     */
    public List<ItemBlock> getAvailableItems() {
        return new ArrayList<>(availableItems);
    }
    
    /**
     * 아이템 모드 여부를 반환
     * @return 아이템 모드 활성화 여부
     */
    public boolean isItemMode() {
        return itemMode;
    }
    
    /**
     * 현재까지 삭제된 총 줄 수를 반환
     * @return 총 삭제 줄 수
     */
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }
    
    /**
     * 아이템 매니저 상태를 초기화 (게임 재시작용)
     */
    public void reset() {
        totalLinesCleared = 0;
    }
}