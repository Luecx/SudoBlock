package visual;

import board.Board;
import board.Piece;
import core.vector.Vector2d;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    private Panel panel;

    public Frame(Board b) {
        super();
        this.panel = new Panel();
        this.panel.setBoard(b);
        this.setSize(1000,1000);
        this.setLocation(800,400);
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    public void highlightPiece(Piece piece, Vector2d vector2d){
        this.panel.setNextPiece(vector2d);
        this.panel.setPiece(piece);
    }

    public Panel getPanel() {
        return panel;
    }

    public void setPanel(Panel panel) {
        this.panel = panel;
    }
}
