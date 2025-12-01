package screens;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.ShapeType;
import core.Tetromino;

class EnemyBoardPanelTest {
    
    private EnemyBoardPanel panel;
    
    @BeforeEach
    void setUp() {
        if (!GraphicsEnvironment.isHeadless()) {
            panel = new EnemyBoardPanel();
        }
    }
    
    @Test
    void testConstructor() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        assertNotNull(panel);
        assertEquals(300, panel.getPreferredSize().width);
        assertEquals(600, panel.getPreferredSize().height);
    }
    
    @Test
    void testUpdateBoard() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        grid[19][0] = ShapeType.I;
        grid[19][1] = ShapeType.O;
        
        Tetromino tetromino = new Tetromino(ShapeType.T, 5, 0);
        
        assertDoesNotThrow(() -> panel.updateBoard(grid, tetromino));
        
        try {
            Field boardDataField = EnemyBoardPanel.class.getDeclaredField("boardData");
            boardDataField.setAccessible(true);
            ShapeType[][] boardData = (ShapeType[][]) boardDataField.get(panel);
            
            assertEquals(ShapeType.I, boardData[19][0]);
            assertEquals(ShapeType.O, boardData[19][1]);
            
            Field enemyCurrentField = EnemyBoardPanel.class.getDeclaredField("enemyCurrent");
            enemyCurrentField.setAccessible(true);
            Tetromino enemyCurrent = (Tetromino) enemyCurrentField.get(panel);
            
            assertNotNull(enemyCurrent);
            assertEquals(ShapeType.T, enemyCurrent.getShape());
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testUpdateBoardWithNull() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        
        assertDoesNotThrow(() -> panel.updateBoard(grid, null));
        
        try {
            Field enemyCurrentField = EnemyBoardPanel.class.getDeclaredField("enemyCurrent");
            enemyCurrentField.setAccessible(true);
            Tetromino enemyCurrent = (Tetromino) enemyCurrentField.get(panel);
            
            assertNull(enemyCurrent);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testPaintComponentWithEmptyBoard() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        
        assertDoesNotThrow(() -> panel.paintComponent(g2));
        
        g2.dispose();
    }
    
    @Test
    void testPaintComponentWithGrid() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        grid[19][0] = ShapeType.I;
        grid[18][1] = ShapeType.O;
        grid[17][2] = ShapeType.T;
        grid[16][3] = ShapeType.S;
        grid[15][4] = ShapeType.Z;
        grid[14][5] = ShapeType.J;
        grid[13][6] = ShapeType.L;
        
        panel.updateBoard(grid, null);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        
        assertDoesNotThrow(() -> panel.paintComponent(g2));
        
        g2.dispose();
    }
    
    @Test
    void testPaintComponentWithTetromino() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        Tetromino tetromino = new Tetromino(ShapeType.I, 5, 0);
        
        panel.updateBoard(grid, tetromino);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        
        assertDoesNotThrow(() -> panel.paintComponent(g2));
        
        g2.dispose();
    }
    
    @Test
    void testPaintComponentWithGridAndTetromino() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        grid[19][0] = ShapeType.I;
        grid[19][1] = ShapeType.I;
        grid[19][2] = ShapeType.I;
        
        Tetromino tetromino = new Tetromino(ShapeType.T, 5, 10);
        
        panel.updateBoard(grid, tetromino);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        
        assertDoesNotThrow(() -> panel.paintComponent(g2));
        
        g2.dispose();
    }
    
    @Test
    void testPaintComponentWithTetrominoOutOfBounds() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        Tetromino tetromino = new Tetromino(ShapeType.I, -1, -1);
        
        panel.updateBoard(grid, tetromino);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        
        assertDoesNotThrow(() -> panel.paintComponent(g2));
        
        g2.dispose();
    }
    
    @Test
    void testPaintComponentWithTetrominoAtEdge() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        Tetromino tetromino = new Tetromino(ShapeType.O, 8, 18);
        
        panel.updateBoard(grid, tetromino);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        
        assertDoesNotThrow(() -> panel.paintComponent(g2));
        
        g2.dispose();
    }
    
    @Test
    void testPaintComponentMultipleTimes() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        grid[19][5] = ShapeType.L;
        
        panel.updateBoard(grid, null);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        
        assertDoesNotThrow(() -> {
            panel.paintComponent(g2);
            panel.paintComponent(g2);
            panel.paintComponent(g2);
        });
        
        g2.dispose();
    }
    
    @Test
    void testCellConstant() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field cellField = EnemyBoardPanel.class.getDeclaredField("CELL");
            cellField.setAccessible(true);
            int cell = (int) cellField.get(panel);
            
            assertEquals(30, cell);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testBoardDataInitial() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field boardDataField = EnemyBoardPanel.class.getDeclaredField("boardData");
            boardDataField.setAccessible(true);
            ShapeType[][] boardData = (ShapeType[][]) boardDataField.get(panel);
            
            assertNotNull(boardData);
            assertEquals(20, boardData.length);
            assertEquals(10, boardData[0].length);
            
            for (int y = 0; y < 20; y++) {
                for (int x = 0; x < 10; x++) {
                    assertNull(boardData[y][x]);
                }
            }
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testEnemyCurrentInitial() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field enemyCurrentField = EnemyBoardPanel.class.getDeclaredField("enemyCurrent");
            enemyCurrentField.setAccessible(true);
            Tetromino enemyCurrent = (Tetromino) enemyCurrentField.get(panel);
            
            assertNull(enemyCurrent);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testPaintComponentWithAllShapeTypes() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        ShapeType[][] grid = new ShapeType[20][10];
        grid[19][0] = ShapeType.I;
        grid[19][1] = ShapeType.O;
        grid[19][2] = ShapeType.T;
        grid[19][3] = ShapeType.S;
        grid[19][4] = ShapeType.Z;
        grid[19][5] = ShapeType.J;
        grid[19][6] = ShapeType.L;
        
        for (ShapeType shape : ShapeType.values()) {
            Tetromino tetromino = new Tetromino(shape, 3, 5);
            panel.updateBoard(grid, tetromino);
            
            BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> panel.paintComponent(g2));
            
            g2.dispose();
        }
    }
    
    @Test
    void testRepaint() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        assertDoesNotThrow(() -> panel.repaint());
    }
}
