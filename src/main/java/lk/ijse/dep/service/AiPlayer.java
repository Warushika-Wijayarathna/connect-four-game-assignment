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

    // Represents a node created in MctsAlgorithm
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
    //Implements the MCTS Algorithm
    private static class MctsAlgorithm{
        Node root;
        ArrayList<Node> maxNodesOfUcb;
        boolean lastMove = false;

        MctsAlgorithm(){}
        private int bestMove() {
            /*
            ++ Create a new root node
             ++ Find the best moves According to the highest nodes of ucb value*/
            root = new Node();
            for (int i = 0; i < 6; i++) {
                selection(root);
            }
            if (maxNodesOfUcb == null || maxNodesOfUcb.size() == 0) bestMove();
            int bestMove;
            do {
                bestMove = (int) (Math.random() * maxNodesOfUcb.size());
            }while (bestMove == maxNodesOfUcb.size());
            return maxNodesOfUcb.get(bestMove).col;
        }

        private void selection(Node parentNode){
            ArrayList<Node> childList = parentNode.children;// Get tall the child nodes of parent node.
            if (childList.size() > 0) {// Check the availability of child nodes.
                for (Node child : childList) {
                    if (child.visits == 0) {
                        simulate(child);//If unvisited nodes are met, they will be simulated.
                        return;
                    }
                }
                Node tempMaxUcbNode = childList.get(0);
                //Check the node with the highest ucb by iterating
                for (Node child : childList) {
                    if (child.ucb > tempMaxUcbNode.ucb) {
                        tempMaxUcbNode = child;
                    }
                }
                // Check the nullability children of selected node and expand them.
                if (tempMaxUcbNode.children.size() == 0) expand(tempMaxUcbNode);
                else selection(tempMaxUcbNode);// Apply recursion and calls itself for selected node.

            } else expand(parentNode);// If no child nodes, expands.
        }

        // Expand the search tree
        private void expand(Node parentNode) {
            for (int i = 0; i < 6; i++) {
                parentNode.children.add(new Node(i, parentNode));
            }
            simulate(parentNode.children.get(0));
        }

        private void simulate(Node nonVisitedNode) {
            Board logicalBoard = AiPlayer.getState(new BoardImpl(board.getBoardUI()));

            int wins = 0;
            int moves = 0;
            Piece currentPlayer = Piece.BLUE;
            Piece winningPiece;

            boolean firstTime = true;
            //Simulates until there is a winner or no legal moves are left.
            while ((winningPiece = logicalBoard.findWinner().getWinningPiece()) == Piece.EMPTY  && logicalBoard.existLegalMoves()) {
                currentPlayer = (currentPlayer == Piece.GREEN) ? Piece.BLUE : Piece.GREEN;
                int randomMove;
                do{
                    // Decide whether it is first time or not, if it is not give a random move.
                    if (firstTime) {
                        randomMove = nonVisitedNode.col;
                        firstTime = false;
                    } else {
                        randomMove = (int) (Math.random() * 6);
                    }
                } while(randomMove == 6 || !logicalBoard.isLegalMove(randomMove));

                logicalBoard.updateMove(randomMove, currentPlayer);
                if (currentPlayer == Piece.GREEN) moves++;//If the player is green moves will be incremented.

            }
            // Determine whether the player wins or not.
            if (winningPiece == Piece.GREEN) wins = 2;
            else if (winningPiece == Piece.BLUE) {
                wins = 0;
                moves = 0;
            }
            else wins = 1;

            if (winningPiece == Piece.GREEN && moves == 1) this.lastMove = true;// Flag the last move.
            backPropagation(nonVisitedNode, wins, moves);// Send simulation result to back propagate.
        }

        /*
        ++ Below method performs the backpropagation step of the Monte Carlo Tree Search (MCTS) algorithm.
        ++ It updates the statistics of nodes in the tree based on the outcome of the simulation.
        ++ Starting from the simulatedNode, it traverses up the tree to the root, updating wins, moves, and visits counts of each node.
        ++ Finally, it resets the maxNodesOfUcb list and updates the UCB values of nodes starting from the root.*/
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
            updateNodeOfMaxUcb(root);
        }
        /*
        ++ This method updates the Upper Confidence Bound (UCB) values of nodes in the tree recursively, starting from the given node.
        ++ It also updates the maxNodesOfUcb list to contain nodes with the maximum UCB values.*/
        private void updateNodeOfMaxUcb(Node node) {
            if (node == root && node.children.size() == 0) return;// If the node is the root and has no children, return.
            //Check if the child node has been visited and update the UCB value of the child node.
            for (Node child : node.children) {
                if (child.visits > 0) {
                    child.ucb = child.wins/(child.visits * 1.0) + Math.sqrt(2) * Math.sqrt(Math.log(child.parent.visits) /child.visits);

                    //Update the maxNodesOfUcb list if the child node's UCB value is higher or equal to the current maximum.
                    if (board.isLegalMove(child.col) && child.ucb > 0) {
                        if (maxNodesOfUcb == null || child.ucb > maxNodesOfUcb.get(0).ucb || (child.ucb == maxNodesOfUcb.get(0).ucb && child.moves < maxNodesOfUcb.get(0).moves)) {
                            maxNodesOfUcb = new ArrayList<>();
                            maxNodesOfUcb.add(child);
                        }
                        else if (child.ucb == maxNodesOfUcb.get(0).ucb&& child.moves == maxNodesOfUcb.get(0).moves) {
                            maxNodesOfUcb.add(child);
                        }
                    }
                    //Recursively call the method for the child node if it has children.
                    if (child.children.size() != 0) {
                        updateNodeOfMaxUcb(child);
                    }
                }
            }
        }

    }

    /*

    **** Before adding Monte Carlo Tree Search Algorithm

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
    }*/


    // Move the pieces for human player.
    @Override
    public void movePiece(int col) {
        Board State = getState(new BoardImpl(board.getBoardUI()));
        MctsAlgorithm mctsAlgorithm = new MctsAlgorithm();

        int win = mctsAlgorithm.bestMove();
        int defend = defenceMove(State);

        //Choose the column according to MctsAlgorithm.
        if (mctsAlgorithm.lastMove == true) {
            col = win;
        } else if (defend==-1) {
            col=win;
        } else {
            col=defend;
        }

        board.updateMove(col, Piece.GREEN);
        board.getBoardUI().update(col, false);

        //Checks winner after move and notify the UI.
        Winner winner = board.findWinner();
        if (winner.getWinningPiece()!=Piece.EMPTY){
            board.getBoardUI().notifyWinner(winner);
        } else if (!board.existLegalMoves()){
            board.getBoardUI().notifyWinner(new Winner(Piece.EMPTY));
        }
    }

    // Find defensive move to prevent opponent from winning by checking 3 similar pieces.
    private int defenceMove(Board state) {
        // Check vertically
        for (int i = 0; i < Board.NUM_OF_COLS; i++) {
            int row= board.findNextAvailableSpot(i);
            if (row<3||row==-1) continue;
            int blueCount=0;
            int j = row -1;
            while (j<row-4&&state.getPiece()[i][j]==Piece.BLUE){
                blueCount++;
                j--;
            }
            if (blueCount==3) return i;
        }

        // Check horizontally
        for (int i = 0; i < Board.NUM_OF_COLS; i++) {
            int row = board.findNextAvailableSpot(i);
            if (row==-1) continue;
            for (int j = i-3; j <=i ; j++) {
                if (j<0) continue;
                int blueCount=0;
                for (int k =j; k<j+4; k++){
                    if (k==i||k>=Board.NUM_OF_COLS) continue;
                    if(state.getPiece()[k][row]==Piece.BLUE) blueCount++;
                    else break;
                }
                if (blueCount==3) return i;
            }
        }
        return -1;
    }

    // Update a given logical board to match the current state of the game board.
    public static BoardImpl getState(BoardImpl logicalBoard){
        Piece[][] state=getStatePiece();
        for (int i = 0; i < Board.NUM_OF_ROWS; i++) {
            for (int j = 0; j < Board.NUM_OF_COLS; j++) {
                logicalBoard.updateMove(j,i,state[j][i]);
            }
        }
        return logicalBoard;
    }

    // Retrieve the current state of the game board as a 2D array of Piece.
    public static Piece[][] getStatePiece() {
        Piece[][] statePiece = new Piece[Board.NUM_OF_COLS][Board.NUM_OF_ROWS];
        Piece[][] piece = board.getPiece();

        for (int i = 0; i < Board.NUM_OF_COLS; i++) {
            for (int j = 0; j < Board.NUM_OF_ROWS; j++) {
                statePiece[i][j]=piece[i][j];
            }

        }
        return statePiece;
    }

}