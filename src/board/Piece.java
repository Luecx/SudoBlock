package board;

public enum Piece {

//    L(new boolean[][]{{true,true,true},{false,false,true}}),
//    L_90(L, true),
//    L_180(L_90, true),
//    L_270(L_90, true),
//
//    T(new boolean[][]{{true,true,true},{false,true,false}}),
//    T_90(T, true),
//    T_180(T_90, true),
//    T_270(T_180, true),

    BLOCK(new boolean[][]{{true,true},{true,true}}),

    ARC(new boolean[][]{{true,true},{true, false}}),
    ARC_90(ARC, true),
    ARC_180(ARC_90, true),
    ARC_270(ARC_180, true),

    I3(new boolean[][]{{true},{true}, {true}}),
    I3_90(I3, true),

    I2(new boolean[][]{{true},{true}}),
    I2_90(I2, true),
    I2_180(I2_90, true),
    I2_270(I2_180, true),

    DOT(new boolean[][]{{true}});

    public final int width;
    public final int height;
    public final int price;

    public boolean[][] filter;

    Piece(boolean[][] filter) {
        this.filter = filter;
        this.width = filter.length;
        this.height = filter[0].length;

        int total = 0;
        for(int i = 0; i < width; i++){
            for(int n = 0; n < height; n++){
                if(filter[i][n]) total++;
            }
        }
        this.price = total;
    }

    Piece(Piece other, boolean rotated){
        this.filter = new boolean[other.height][other.width];

        for(int i = 0; i < other.width; i++){
            for(int n = 0; n < other.height; n++){
                this.filter[n][i] = other.filter[i][other.height - 1 - n];
            }
        }

        this.width = filter.length;
        this.height = filter[0].length;
        this.price = other.price;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPrice() {
        return price;
    }

    public boolean[][] getFilter() {
        return filter;
    }

}
