package board;

import static board.Board.isOccupied;
import static board.Board.isOutOfBounds;
import static board.Constants.BISHOP;
import static board.Piece.getNextSquare;

public class Bishop {

    private final Board board;

    public Bishop(Board board) {
        this.board = board;
    }

    public void generateMovesInDirection(int square, int direction) {
        int currentSquare = square;
        while (true) {
            currentSquare = getNextSquare(currentSquare, BISHOP, direction);
            if (isOutOfBounds(currentSquare)) break;
            if (isOccupied(board.color, currentSquare)) {
                board.handleOccupiedSquare(square, currentSquare);
                break;
            }
            board.addMove(square, currentSquare, 0);  // Ajouter le mouvement si la case est libre
            // Le fou peut toujours glisser, donc on continue la boucle
        }
    }
}
