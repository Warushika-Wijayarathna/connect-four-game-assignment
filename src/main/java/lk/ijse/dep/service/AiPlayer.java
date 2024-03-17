package lk.ijse.dep.service;

import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;
import java.util.List;

public class AiPlayer extends Player{
    private static Board board;
    public AiPlayer(Board board){
        super(board);
        this.board = board;
    }

    private static class Node{
        private int col;
        private Node parent;
        private int visits=0;
        private int wins=0;
        private int moves=0;
        private double ucb = 0;
        private ArrayList<Node> children = new ArrayList<>();

        private Node(){}
        private Node(int col, Node parent){
            this.col=col;

            this.parent=parent;
        }
    }
    private static class MctsAlgorithm{
        Node root;
        ArrayList<Node> maxNodesOfUcb;
        boolean lastMove = false;

        MctsAlgorithm(){}

        private void simiulate(Node nonVisitedNode) {
            Board logicalboard = AiPlayer.getState(new BoardImpl(board.getBoardUI()));

            int wins = 0;
            int moves = 0;
            Piece currentPlayer = Piece.BLUE;
            Piece winngPiece;

            boolean firstTime = true;

            while (winngPiece=logicalboard.findWinner().getWinningPiece()==Piece.EMPTY&&logicalboard.existLegalMoves()){
                currentPlayer = (currentPlayer==Piece.GREEN)?Piece.BLUE:Piece.GREEN;
                int random = (firstTime) ? nonVisitedNode.col : (int) (Math.random()*6);

                while()
            }
        }
        private void backPropagation(Node simulatedNode, int win, int moves){
            Node traverseNode=simulatedNode;
            traverseNode.wins+=win;
            traverseNode.moves+=moves;
            traverseNode.visits++;

            while (traverseNode.parent!=null){
                Node parentTraverseNode = traverseNode.parent;
                parentTraverseNode.wins+=win;
                parentTraverseNode.moves+=moves;
                parentTraverseNode.visits++;
                traverseNode = traverseNode.parent;
            }
            maxNodesOfUcb=null;
            updateMaxUcbOfNode(root);
        }
        private double calculateUcb(Node node){
            double exploitation = (double) node.wins/node.visits;
            double exploration = Math.sqrt(2)*Math.sqrt(Math.log(node.parent.visits)/node.visits);
            return exploitation+exploration;
        }
        private void updateMaxUcbOfNode(Node node) {
            if(node==root&&node.children.isEmpty()) return;
            double maxUcbValue = Double.NEGATIVE_INFINITY;
            List<Node> maxNodesOfUcb = new ArrayList<>();
            
            for (Node child : node.children){
                if (child.visits>0 ==) {
                    double ucb = calculateUcb(child);

                    if (ucb>maxUcbValue&& board.isLegalMove(child.col)) {
                        maxNodesOfUcb.clear();
                        maxNodesOfUcb.add(child);
                        maxUcbValue=ucb;
                    } else if (ucb==maxUcbValue&&board.isLegalMove(child.col)) {
                        maxNodesOfUcb.add(child);
                    }
                }
            }
        }

        private void selection(Node parentNode){
            ArrayList<Node> childrenList = parentNode.children;
            if(childrenList.size()>0){
                for(Node child : childrenList){
                    if(child.visits==0){
                        simiulate(child);
                        return;
                    }
                }
                Node tempMax = childrenList.get(0);
                for (Node child: childrenList) {
                    if (child.ucb> tempMax.ucb) {
                        tempMax=child;
                    }
                }
            }else expand(parentNode);
        }
    }
    @Override
    public void movePiece(int col) {
        Board State = getState(new BoardImpl(board.getBoardUI()));
        MctsAlgorithm mctsAlgorithm = new MctsAlgorithm();

        int win = mctsAlgorithm.bestMove();
        int defend = defenceMove(State);

        if (mctsAlgorithm.lastMove == true) {
            col = win;
        } else if (defend==-1) {
            col=win;
        } else {
            col=defend;
        }

        board.updateMove(col, Piece.GREEN);
        board.getBoardUI().update(col, false);

        Winner winner = board.findWinner();
        if (winner.getWinningPiece()!=Piece.EMPTY){
            board.getBoardUI().notifyWinner(winner);
        } else if (!board.existLegalMoves()){
            board.getBoardUI().notifyWinner(new Winner(Piece.EMPTY));
        }

    }

    public static BoardImpl getState(BoardImpl logicalBoard){

    }
}