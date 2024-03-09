package lk.ijse.dep.service;

public class BoardImpl implements Board{
    private final Piece [][] pieces = new Piece[NUM_OF_COLS][NUM_OF_ROWS];
    private final BoardUI boardUI;

    public BoardImpl(BoardUI boardUI){
        this.boardUI=boardUI;
    }
    @Override
    public BoardUI getBoardUI() {
        return this.boardUI;
    }

    @Override
    public int findNextAvailableSpot(int col) {
        for (int j = 0; j < pieces[col].length; j++) {
            if (pieces[col][j] == Piece.EMPTY) {
                return j;
            }
        }
        return -1;
    }

    @Override
    public boolean isLegalMove(int col) {
        if (this.findNextAvailableSpot(col) != -1) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean existLegalMoves() {
        for (int i = 0; i < NUM_OF_COLS; i++) {
            if (this.isLegalMove(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateMove(int col, Piece move) {
        this.pieces[col][this.findNextAvailableSpot(col)]=move;
    }

    @Override
    public Winner findWinner() {
        for (int row = 0; row < pieces.length; row++) {
            for (int col = 0; col < pieces[0].length; col++) {
                Piece piece = pieces[row][col];
                if (piece!=Piece.EMPTY) {
                    int spot = findNextAvailableSpot(col);
                    if (col+3 < spot &&
                            piece==pieces[row][col+1] &&
                            piece==pieces[row][col+2] &&
                            piece==pieces[row][col+3]) {
                        return new Winner(piece);
                    }
                    if (row+3 < spot &&
                            piece==pieces[row+1][col] &&
                            piece==pieces[row+2][col] &&
                            piece==pieces[row+3][col]) {
                        return new Winner(piece);
                    }
                }
            }
        }
        return new Winner(Piece.EMPTY);
    }
}
