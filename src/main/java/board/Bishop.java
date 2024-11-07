package board;

import static board.Board.isOccupied;
import static board.Board.isOutOfBounds;
import static board.Constants.BISHOP;

public class Bishop extends Piece {

    private final Board board;

    public Bishop(Board board) {
        this.board = board;
    }

    public void generateMovesInDirection(int square, int direction) {
        for (int currentSquare = getNextSquare(square, BISHOP, direction);
             !isOutOfBounds(currentSquare);
             currentSquare = getNextSquare(currentSquare, BISHOP, direction)) {
            if (isOccupied(board.color, currentSquare)) {
                board.handleOccupiedSquare(square, currentSquare);
                break;
            }
            board.addMove(square, currentSquare, 0);  // Ajouter le mouvement si la case est libre
        }
    }
}
