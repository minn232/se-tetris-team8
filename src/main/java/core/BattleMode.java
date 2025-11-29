package core;

/**
 * 대전 모드 인터페이스
 * Board가 상대방에게 줄을 보내기 위한 콜백
 */
public interface BattleMode {
    /**
     * 상대방에게 줄을 보냄
     * @param lines 보낼 줄의 개수 (2줄 이상 클리어 시 클리어 줄 수 - 1)
     */
    void sendLinesToOpponent(int lines);
}
