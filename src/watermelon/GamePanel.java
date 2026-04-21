package watermelon;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements KeyListener, MouseListener {

    private static final long serialVersionUID = 1L;

    // Screens
    private static final int SCREEN_START = 0;
    private static final int SCREEN_GAME  = 1;
    private static final int SCREEN_END   = 2;

    private int screen = SCREEN_START;

    // Layout
    private static final int TILE   = 100;
    private static final int GAP    = 12;
    private static final int PAD    = 20;
    private static final int BOARD  = PAD * 2 + Board.SIZE * TILE + (Board.SIZE - 1) * GAP;
    private static final int WIDTH  = BOARD;
    private static final int HEIGHT = BOARD + 80;

    // Colors
    private static final Color BG         = new Color(250, 248, 239);
    private static final Color BOARD_BG   = new Color(187, 173, 160);
    private static final Color EMPTY_TILE = new Color(205, 193, 180);
    private static final Color TILE_BG    = new Color(237, 224, 200);
    private static final Color TEXT_DARK  = new Color(119, 110, 101);
    private static final Color BUTTON_BG  = new Color(143, 122, 102);
    private static final Color BUTTON_FG  = Color.WHITE;

    private Board board;
    private Map<Integer, BufferedImage> images = new HashMap<>();

    // Button bounds (for click detection)
    private Rectangle startBtn;
    private Rectangle playAgainBtn;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(BG);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        loadImages();
    }

    private void loadImages() {
        for (FruitType f : FruitType.values()) {
            if (f == FruitType.EMPTY) continue;
            try {
                BufferedImage img = ImageIO.read(new File("images/" + f.name().toLowerCase() + ".png"));
                images.put(f.value, img);
            } catch (Exception e) {
                // No image found — will draw fruit name as text fallback
            }
        }
    }

    // ── PAINTING ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (screen) {
            case SCREEN_START: drawStart(g2); break;
            case SCREEN_GAME:  drawGame(g2);  break;
            case SCREEN_END:   drawGame(g2); drawEnd(g2); break;
        }
    }

    private void drawStart(Graphics2D g) {
        // Title
        g.setColor(TEXT_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        drawCentered(g, "Suika 2048", WIDTH / 2, 160);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        drawCentered(g, "Merge fruits to reach", WIDTH / 2, 210);
        drawCentered(g, "the Watermelon!", WIDTH / 2, 232);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        drawCentered(g, "Use arrow keys to move tiles.", WIDTH / 2, 270);

        // Start button
        startBtn = drawButton(g, "Start Game", WIDTH / 2, 340);
    }

    private void drawGame(Graphics2D g) {
        // Score header
        g.setColor(TEXT_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("Suika 2048", PAD, 38);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("SCORE: " + board.getScore(), PAD, 60);

        // Board background
        int boardX = PAD - GAP;
        int boardY = 75 - GAP;
        int boardW = BOARD - PAD + GAP;
        int boardH = BOARD - PAD + GAP;
        g.setColor(BOARD_BG);
        g.fillRoundRect(boardX, boardY, boardW, boardH, 12, 12);

        // Tiles
        int[][] grid = board.getGrid();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                int x = PAD + c * (TILE + GAP);
                int y = 75 + r * (TILE + GAP);
                drawTile(g, grid[r][c], x, y);
            }
        }
    }

    private void drawTile(Graphics2D g, int value, int x, int y) {
        g.setColor(value == 0 ? EMPTY_TILE : TILE_BG);
        g.fillRoundRect(x, y, TILE, TILE, 8, 8);

        if (value == 0) return;

        BufferedImage img = images.get(value);
        if (img != null) {
            int pad = 8;
            g.drawImage(img, x + pad, y + pad, TILE - pad * 2, TILE - pad * 2, null);
        } else {
            // Fallback text
            String name = FruitType.fromValue(value).name();
            g.setColor(TEXT_DARK);
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(name, x + (TILE - fm.stringWidth(name)) / 2,
                    y + TILE / 2 + fm.getAscent() / 2 - 2);
        }
    }

    private void drawEnd(Graphics2D g) {
        // Dim overlay
        g.setColor(new Color(238, 228, 218, 200));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(TEXT_DARK);

        String title = board.hasWon() ? "You Win!" : "Game Over!";
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        drawCentered(g, title, WIDTH / 2, HEIGHT / 2 - 60);

        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        drawCentered(g, "Score: " + board.getScore(), WIDTH / 2, HEIGHT / 2 - 20);

        playAgainBtn = drawButton(g, "Play Again", WIDTH / 2, HEIGHT / 2 + 60);
    }

    // Draws a rounded button centered at (cx, cy), returns its bounds
    private Rectangle drawButton(Graphics2D g, String label, int cx, int cy) {
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int bw = fm.stringWidth(label) + 40;
        int bh = 44;
        int bx = cx - bw / 2;
        int by = cy - bh / 2;

        g.setColor(BUTTON_BG);
        g.fillRoundRect(bx, by, bw, bh, 10, 10);
        g.setColor(BUTTON_FG);
        g.drawString(label, bx + 20, by + bh / 2 + fm.getAscent() / 2 - 2);

        return new Rectangle(bx, by, bw, bh);
    }

    private void drawCentered(Graphics2D g, String text, int cx, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, cx - fm.stringWidth(text) / 2, y);
    }

    // ── INPUT ─────────────────────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        if (screen != SCREEN_GAME) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    board.move("UP");    break;
            case KeyEvent.VK_DOWN:  board.move("DOWN");  break;
            case KeyEvent.VK_LEFT:  board.move("LEFT");  break;
            case KeyEvent.VK_RIGHT: board.move("RIGHT"); break;
            default: return;
        }
        if (board.hasWon() || board.isGameOver()) screen = SCREEN_END;
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (screen == SCREEN_START && startBtn != null && startBtn.contains(p)) {
            board = new Board();
            screen = SCREEN_GAME;
            requestFocusInWindow();
            repaint();
        } else if (screen == SCREEN_END && playAgainBtn != null && playAgainBtn.contains(p)) {
            board = new Board();
            screen = SCREEN_START;
            repaint();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}