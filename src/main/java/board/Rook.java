package board;

import static board.Constants.ROOK;

public class Rook extends Piece {


    private final Board board;

    public Rook(Board board) {
        this.board = board;
    }

    public void generateMovesInDirection(int square, int direction) {

        int currentSquare = square;
        while (true) {
            currentSquare = getNextSquare(currentSquare, ROOK, direction);
            if (IBoard.isOutOfBounds(currentSquare)) break;
            if (IBoard.isOccupied(board.color, currentSquare)) {
                board.handleOccupiedSquare(square, currentSquare);
                break;
            }
            board.addMove(square, currentSquare, 0);  // Ajouter le mouvement si la case est libre
            // La tour peut toujours glisser, donc on continue la boucle
        }
    }
}
