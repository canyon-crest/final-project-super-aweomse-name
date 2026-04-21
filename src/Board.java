import java.util.*;

public class Board {
    public static final int SIZE = 4;
    private int[][] grid = new int[SIZE][SIZE];
    private int score = 0;
    private Random rand = new Random();

    public Board() {
        addRandomTile();
        addRandomTile();
    }

    public int[][] getGrid() { return grid; }
    public int getScore() { return score; }

    public void addRandomTile() {
        List<int[]> empty = new ArrayList<>();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0) empty.add(new int[]{r, c});
        if (empty.isEmpty()) return;
        int[] cell = empty.get(rand.nextInt(empty.size()));
        grid[cell[0]][cell[1]] = 2;
    }

    public boolean move(String dir) {
        int[][] before = copyGrid();
        switch (dir) {
            case "UP":    slideUp();    break;
            case "DOWN":  slideDown();  break;
            case "LEFT":  slideLeft();  break;
            case "RIGHT": slideRight(); break;
        }
        boolean changed = !gridsEqual(before, grid);
        if (changed) addRandomTile();
        return changed;
    }

    private int[] compress(int[] arr) {
        int[] result = new int[SIZE];
        int pos = 0;
        for (int v : arr) if (v != 0) result[pos++] = v;
        return result;
    }

    private int[] merge(int[] arr) {
        for (int i = 0; i < SIZE - 1; i++) {
            if (arr[i] != 0 && arr[i] == arr[i + 1]) {
                FruitType next = FruitType.fromValue(arr[i]).next();
                arr[i] = next.value;
                arr[i + 1] = 0;
                score += arr[i];
            }
        }
        return arr;
    }

    private int[] reverse(int[] arr) {
        int[] r = new int[arr.length];
        for (int i = 0; i < arr.length; i++) r[i] = arr[arr.length - 1 - i];
        return r;
    }

    private int[] getCol(int c) {
        int[] col = new int[SIZE];
        for (int r = 0; r < SIZE; r++) col[r] = grid[r][c];
        return col;
    }

    private void setCol(int c, int[] col) {
        for (int r = 0; r < SIZE; r++) grid[r][c] = col[r];
    }

    private void slideLeft() {
        for (int r = 0; r < SIZE; r++) {
            int[] row = compress(grid[r]);
            row = merge(row);
            grid[r] = compress(row);
        }
    }

    private void slideRight() {
        for (int r = 0; r < SIZE; r++) {
            int[] row = reverse(compress(reverse(grid[r])));
            row = merge(row);
            row = reverse(compress(reverse(row)));
            grid[r] = row;
        }
    }

    private void slideUp() {
        for (int c = 0; c < SIZE; c++) {
            int[] col = compress(getCol(c));
            col = merge(col);
            setCol(c, compress(col));
        }
    }

    private void slideDown() {
        for (int c = 0; c < SIZE; c++) {
            int[] col = reverse(compress(reverse(getCol(c))));
            col = merge(col);
            setCol(c, reverse(compress(reverse(col))));
        }
    }

    private int[][] copyGrid() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) copy[r] = grid[r].clone();
        return copy;
    }

    private boolean gridsEqual(int[][] a, int[][] b) {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (a[r][c] != b[r][c]) return false;
        return true;
    }

    public boolean isGameOver() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0) return false;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                if (c + 1 < SIZE && grid[r][c] == grid[r][c+1]) return false;
                if (r + 1 < SIZE && grid[r][c] == grid[r+1][c]) return false;
            }
        return true;
    }

    public boolean hasWon() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 2048) return true;
        return false;
    }
}