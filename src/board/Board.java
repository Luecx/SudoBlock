package board;

import core.vector.Vector2d;
import ki.KI;
import visual.Frame;

import java.util.ArrayList;
import java.util.Random;

public class Board {

    private boolean[][] stones;
    private int width;
    private int height;
    private int subd_w;
    private int subd_h;


    public Board() {
        this(9, 9, 3, 3);
    }

    public Board(int width, int height, int subd_w, int subd_h) {
        this.subd_w = subd_w;
        this.subd_h = subd_h;
        this.width = width;
        this.height = height;
        this.stones = new boolean[width][height];

        this.random = new Random((int)(Math.random() * 10000));
        this.nextPiece = this.nextPiece();
        this.score = 0;
    }

    public Board(Board other) {
        this.width = other.width;
        this.height = other.height;
        this.subd_w = other.subd_w;
        this.subd_h = other.subd_h;
        this.stones = copyArray(other.stones);

        this.random = new Random((int)(Math.random() * 10000));
        this.nextPiece = other.nextPiece;
        this.score = other.score;
    }


    private boolean inRange(Piece piece, int x_offset, int y_offset) {
        return piece.width + x_offset <= width && piece.height + y_offset <= height;
    }

    /**
     * assumed to be inRange. Otherwise exception will be thrown
     *
     * @param piece
     * @param x_offset
     * @param y_offset
     * @return
     */
    private boolean canPlace(Piece piece, int x_offset, int y_offset) {
        for (int i = x_offset; i < x_offset + piece.width; i++) {
            for (int n = y_offset; n < y_offset + piece.height; n++) {
                if (piece.filter[i - x_offset][n - y_offset] && stones[i][n]) return false;
            }
        }
        return true;
    }

    private int place(Piece piece, int x_offset, int y_offset) {
        if (!inRange(piece, x_offset, y_offset) || !canPlace(piece, x_offset, y_offset)) return 0;
        for (int i = x_offset; i < x_offset + piece.width; i++) {
            for (int n = y_offset; n < y_offset + piece.height; n++) {
                if(piece.filter[i-x_offset][n-y_offset])
                    stones[i][n] = piece.filter[i - x_offset][n - y_offset];
            }
        }
        return piece.price + reduce() * 2;
    }

    private int place(Piece piece, Vector2d vector2d) {
        return this.place(piece, (int)vector2d.getX(), (int)vector2d.getY());
    }

    private int reduce() {
        boolean[][] new_stones = copyArray(this.stones);
        for (int i = 0; i < this.getWidth(); i++) {
            boolean c = true;
            for (int n = 0; n < this.getHeight(); n++) {
                if (stones[i][n] == false) c = false;
            }
            if (c) {
                for (int n = 0; n < this.getHeight(); n++) {
                    new_stones[i][n] = false;
                }
            }
        }
        for (int i = 0; i < this.getHeight(); i++) {
            boolean c = true;
            for (int n = 0; n < this.getWidth(); n++) {
                if (stones[n][i] == false) c = false;
            }
            if (c) {
                for (int n = 0; n < this.getWidth(); n++) {
                    new_stones[n][i] = false;
                }
            }
        }
        for (int b_x = 0; b_x < (this.getWidth() / this.subd_w + 1); b_x++) {
            for (int b_y = 0; b_y < (this.getHeight() / this.subd_h + 1); b_y++) {
                boolean full = true;
//
//                System.out.println(subd_w * b_x + "  " +  Math.min(this.width, subd_w * (b_x + 1)));
//                System.out.println(subd_h * b_y + "  " +  Math.min(this.height,subd_h * (b_y + 1)));

                for (int x = subd_w * b_x; x < Math.min(this.width, subd_w * (b_x + 1)); x++) {
                    for (int y = subd_h * b_y; y <  Math.min(this.height,subd_h * (b_y + 1)); y++) {
                        if (stones[x][y] == false) full = false;
                    }
                }
                if (full) {
                    for (int x = subd_w * b_x; x < Math.min(this.width, subd_w * (b_x + 1)); x++) {
                        for (int y = subd_h * b_y; y <  Math.min(this.height,subd_h * (b_y + 1)); y++) {
                            new_stones[x][y] = false;
                        }
                    }
                }
            }
        }
        int counter = 0;
        for (int n = 0; n < this.height; n++) {
            for (int i = 0; i < this.width; i++) {
                if(new_stones[i][n] != stones[i][n]) counter++;
            }
        }
        this.stones = new_stones;
        return counter;
    }

    public ArrayList<Vector2d> getPossiblePositions(Piece piece) {
        ArrayList<Vector2d> vector2ds = new ArrayList<>();
        for(int i = 0; i <= this.getWidth()-piece.width; i++){
            for(int n = 0; n <= this.getHeight()-piece.height; n++){
                if(this.canPlace(piece, i, n)){
                    vector2ds.add(new Vector2d(i,n));
                }
            }
        }
        return vector2ds;
    }


    private Random random;
    private Piece nextPiece;
    private int score;
    private boolean testing= false;
    private boolean[][] testingBackup;

    private Piece nextPiece() {
        return Piece.values()[(int)(random.nextDouble() * Piece.values().length)];
        //return Piece.BLOCK;
    }

    public Piece getNextPiece() {
        return nextPiece;
    }

    public boolean gameOver() {
        return this.getPossiblePositions(nextPiece).size() == 0;
    }

    public int move(int x, int y) {
        if(gameOver() || testing) return -1;
        int score = this.place(nextPiece, x, y);
        this.nextPiece = nextPiece();
        this.score += score;
        return score;
    }

    public int move(Vector2d vec) {
        if(gameOver() || testing) return -1;
        int score = this.place(nextPiece, vec);
        this.nextPiece = nextPiece();
        this.score += score;
        return score;
    }

    public void test(Piece piece, Vector2d vec) {
        if(!testing){
            testingBackup = copyArray(this.stones);
            testing = true;
        }

        this.place(piece, vec);
    }

    public void untest() {
        testing = false;
        this.stones = testingBackup;
    }


    public Board copy() {
        return new Board(this);
    }

    public boolean[][] getStones() {
        return stones;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSubd_w() {
        return subd_w;
    }

    public int getSubd_h() {
        return subd_h;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int n = 0; n < this.height; n++) {
            for (int i = 0; i < this.width; i++) {
                builder.append(stones[i][n] ? "#" : ".");
            }
            builder.append("\n");
        }
        return builder.toString();
    }


    public static void main(String[] args) throws InterruptedException {
        Board b = new Board(6,6,2,2);
        b.getStones()[1][0] = true;
        b.getStones()[2][0] = true;
        b.getStones()[4][0] = true;

        b.getStones()[0][1] = true;
        b.getStones()[3][1] = true;

        b.getStones()[3][2] = true;

        b.getStones()[0][3] = true;
        b.getStones()[2][3] = true;

        b.getStones()[2][4] = true;
        b.getStones()[3][4] = true;
        b.getStones()[4][4] = true;

        b.getStones()[0][5] = true;
        b.getStones()[2][5] = true;
        b.getStones()[5][5] = true;
        System.out.println(b.getPossiblePositions(Piece.ARC_90));

        Frame f = new Frame(b);

        for(Vector2d vec:b.getPossiblePositions(Piece.ARC_90)){
            Thread.sleep(1500);
            f.highlightPiece(Piece.ARC_90, vec);
            f.repaint();
        }


    }


    public static boolean[][] copyArray(boolean[][] other) {
        boolean[][] stones = new boolean[other.length][other[0].length];
        for (int i = 0; i < other.length; i++) {
            for (int n = 0; n < other[0].length; n++) {
                stones[i][n] = other[i][n];
            }
        }
        return stones;
    }
}
