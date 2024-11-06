package board;

import static board.Board.isOccupied;
import static board.Board.isOutOfBounds;
import static board.Constants.ROOK;
import static board.Piece.getNextSquare;

public class Rook {


    private final Board board;

    public Rook(Board board) {
        this.board = board;
    }

    public void generateMovesInDirection(int square, int direction) {

        int currentSquare = square;
        while (true) {
            currentSquare = getNextSquare(currentSquare, ROOK, direction);
            if (isOutOfBounds(currentSquare)) break;
            if (isOccupied(board.color, currentSquare)) {
                board.handleOccupiedSquare(square, currentSquare);
                break;
            }
            board.addMove(square, currentSquare, 0);  // Ajouter le mouvement si la case est libre
            // La tour peut toujours glisser, donc on continue la boucle
        }
    }
}
