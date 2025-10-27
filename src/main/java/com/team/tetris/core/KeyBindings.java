package com.team.tetris.core;

import java.awt.event.KeyEvent;

public class KeyBindings {
    private int left   = KeyEvent.VK_LEFT;
    private int right  = KeyEvent.VK_RIGHT;
    private int down   = KeyEvent.VK_DOWN;
    private int rotate = KeyEvent.VK_UP;
    private int hardDrop = KeyEvent.VK_SPACE;

    private int pause  = KeyEvent.VK_P;
    private int quit   = KeyEvent.VK_ESCAPE;

    public int getLeft()     { return left; }
    public int getRight()    { return right; }
    public int getDown()     { return down; }
    public int getRotate()   { return rotate; }
    public int getHardDrop() { return hardDrop; }
    public int getPause()    { return pause; }
    public int getQuit()     { return quit; }

    // 추후 설정 메뉴에서 호출해 변경 가능
    public void setLeft(int code)     { left = code; }
    public void setRight(int code)    { right = code; }
    public void setDown(int code)     { down = code; }
    public void setRotate(int code)   { rotate = code; }
    public void setHardDrop(int code) { hardDrop = code; }
    public void setPause(int code)    { pause = code; }
    public void setQuit(int code)     { quit = code; }
}
