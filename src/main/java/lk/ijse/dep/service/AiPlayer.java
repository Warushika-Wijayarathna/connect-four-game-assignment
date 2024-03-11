package lk.ijse.dep.service;

public class AiPlayer extends Player{
    public AiPlayer(Board board) {
        super(board);
    }
    @Override
    public void movePiece(int col){
        int randCol;
        do{
           randCol=(int)Math.floor(Math.random() * Board.NUM_OF_COLS);
        }while (!this.board.isLegalMove(randCol));

        this.board.updateMove(randCol, Piece.GREEN);
        this.board.getBoardUI().update(randCol,false);

        Winner winner = this.board.findWinner();
        if (winner.getWinningPiece() != Piece.EMPTY) {
            this.board.getBoardUI().notifyWinner(winner);
        } else if (!this.board.existLegalMoves()) {
            this.board.getBoardUI().notifyWinner(new Winner(Piece.EMPTY));
        }
    }
}
