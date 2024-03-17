package lk.ijse.dep.service;

import java.util.Arrays;

public class BoardImpl implements Board{
    private final Piece [][] pieces = new Piece[NUM_OF_COLS][NUM_OF_ROWS];
    private final BoardUI boardUI;

    public BoardImpl(BoardUI boardUI){
        for(int i = 0; i < 6; ++i) {
            for(int j = 0; j < 5; ++j) {
                this.pieces[i][j] = Piece.EMPTY;
            }
        }

        this.boardUI = boardUI;
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
        return this.findNextAvailableSpot(col) != -1;
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
    public void updateMove(int col, int row, Piece move) {
        this.pieces[col][row]=move;
    }

    @Override
    public Piece[][] getPiece() {
        return this.pieces;
    }

    @Override
    public Winner findWinner() {
        for (int row = 0; row < NUM_OF_ROWS; row++) {
            for (int col = 0; col < NUM_OF_COLS - 3; col++) {
                Piece piece = pieces[col][row];
                if (piece != Piece.EMPTY &&
                        piece == pieces[col + 1][row] &&
                        piece == pieces[col + 2][row] &&
                        piece == pieces[col + 3][row]) {
                    return new Winner(piece, col, row, col + 3, row);
                }
            }
        }

        for (int col = 0; col < NUM_OF_COLS; col++) {
            for (int row = 0; row < NUM_OF_ROWS - 3; row++) {
                Piece piece = pieces[col][row];
                if (piece != Piece.EMPTY &&
                        piece == pieces[col][row + 1] &&
                        piece == pieces[col][row + 2] &&
                        piece == pieces[col][row + 3]) {
                    return new Winner(piece, col, row, col, row + 3);
                }
            }
        }

        return new Winner(Piece.EMPTY);
    }

}
