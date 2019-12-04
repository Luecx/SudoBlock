package ki;

import board.Board;
import core.vector.Vector2d;

public abstract class KI {

    public KI(){

    }

    public int evaluate(Board board) {
        while(board.gameOver() == false){
            board.move(evaluateNextMove(board));
        }
        return board.getScore();
    }

    public abstract Vector2d evaluateNextMove(Board b);



}
