package board;

import static board.Constants.KNIGHT;
import static board.Constants.OFFSETS;
import static java.util.stream.IntStream.range;

public class Knight extends Piece {

    private final Board board;

    public Knight(Board board) {
        this.board = board;
    }

    public void generateMovesInDirection(int square) {
        range(0, OFFSETS[KNIGHT]).map(direction ->
                getNextSquare(square, KNIGHT, direction)).filter(currentSquare ->
                !IBoard.isOutOfBounds(currentSquare)).forEach(currentSquare -> {
            if (IBoard.isOccupied(board.color, currentSquare)) board.handleOccupiedSquare(square, currentSquare);
            else board.addMove(square, currentSquare, 0);  // Ajouter le mouvement si la case est libre
        });
    }
}
