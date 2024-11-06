package board;

import static board.Board.isOccupied;
import static board.Board.isOutOfBounds;
import static board.Constants.KNIGHT;
import static board.Constants.offsets;
import static board.Piece.getNextSquare;

public class Knight {

    private final Board board;

    public Knight(Board board) {
        this.board = board;
    }


    public void generateMovesInDirection(int square) {
        for (int direction = 0; direction < offsets[KNIGHT]; ++direction) {
            int currentSquare = getNextSquare(square, KNIGHT, direction);
            if (isOutOfBounds(currentSquare)) continue;
            if (isOccupied(board.color, currentSquare)) {
                board.handleOccupiedSquare(square, currentSquare);
                continue;
            }
            board.addMove(square, currentSquare, 0);  // Ajouter le mouvement si la case est libre
        }
    }
}