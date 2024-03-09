package lk.ijse.dep.service;

public class AiPlayer extends Player{
    public AiPlayer(Board board) {
        super(board);
    }
    @Override
    public void movePiece(int col){
        if (col != -1) {
            return;
        }

        int randCol;
        do{
           randCol=(int) (Math.random()*Board.NUM_OF_COLS);
        }while (!board.isLegalMove(randCol));

        board.updateMove(randCol, Piece.GREEN);
        board.getBoardUI().update(randCol,false);

        Winner winner = board.findWinner();
        if (winner.getWinningPiece() != Piece.EMPTY) {
            board.getBoardUI().notifyWinner(winner);
        } else if (!board.existLegalMoves()) {
            board.getBoardUI().notifyWinner(new Winner(Piece.EMPTY));
        }
    }
}
