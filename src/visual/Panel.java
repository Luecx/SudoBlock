package visual;

import board.Board;
import board.Piece;
import core.vector.Vector2d;
import ki.GenAlgNNDefault;
import neuralnetwork.builder.Network;

import java.awt.*;

public class Panel extends visuals.Panel {


    private Board board;
    private Vector2d nextPiece = null;
    private Piece piece = null;

    public Panel() {
        super();
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0,0,this.getWidth(), this.getHeight());

        int w = board.getWidth();
        int h = board.getHeight();
        if(this.board != null){
            for(int i = 0; i < w; i++){
                for(int n = 0; n < h; n++){
                    if(board.getStones()[i][n]){
                        g.setColor(Color.blue);
                        g.fillRect(
                                (int)(this.getWidth() * (double)(i) / w),
                                (int)(this.getHeight() * (double)(n) / h),
                                (int)(this.getWidth() / w),
                                (int)(this.getHeight() / h)
                        );
                    }else{
                        g.setColor(Color.black);
                        g.drawRect(
                                (int)(this.getWidth() * (double)(i) / w),
                                (int)(this.getHeight() * (double)(n) / h),
                                (int)(this.getWidth() / w),
                                (int)(this.getHeight() / h)
                        );
                    }
                }
            }
        }

        if(this.nextPiece != null && this.piece != null){
            for(int i = (int)nextPiece.getX(); i < (int)nextPiece.getX() + this.piece.width; i++){
                for(int n = (int)nextPiece.getY(); n < (int)nextPiece.getY() + this.piece.height; n++){
                    if(piece.filter[i-(int)nextPiece.getX()][n-(int)nextPiece.getY()]){
                        g.setColor(Color.red);
                        g.fillRect(
                                (int)(this.getWidth() * (double)(i) / w),
                                (int)(this.getHeight() * (double)(n) / h),
                                (int)(this.getWidth() / w),
                                (int)(this.getHeight() / h)
                        );
                    }

                }
            }
        }

        //this.draw_grid((Graphics2D) g);
    }

    public Vector2d getNextPiece() {
        return nextPiece;
    }

    public void setNextPiece(Vector2d nextPiece) {
        this.nextPiece = nextPiece;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public static void main(String[] args) throws Exception {
        Board b = new Board(6,6,2,2);
        GenAlgNNDefault.Client client = new GenAlgNNDefault.Client(b);
        client.setNetwork(Network.load("networks/network5.net"));

//        for(int i = 0; i < 10; i++){
//            Vector2d nextPosition = client.getNextPosition(b);
//            Piece nextPiece = b.getNextPiece();
//            Frame f = new Frame(b.copy());
//            f.getPanel().nextPiece = nextPosition;
//            f.getPanel().piece = nextPiece;
//            b.move(nextPosition);
//        }

        
        Frame f = new Frame(b);


        int counter = 0;

        while (!b.gameOver()) {
            try {

                Vector2d nextPosition = client.getNextPosition(new Board(b));
                Piece nextPiece = b.getNextPiece();


                System.out.println(b.getScore() + "  " + b.getNextPiece());

                counter ++;

                f.repaint();
                Thread.sleep(Math.max(300-5*counter,10));
                f.getPanel().nextPiece = nextPosition;
                f.getPanel().piece = nextPiece;
                f.repaint();
                Thread.sleep(Math.max(300-5*counter,10));
                f.getPanel().nextPiece = null;
                f.getPanel().piece = null;
                f.repaint();
                b.move(nextPosition);
                f.repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
