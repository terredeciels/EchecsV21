package board;

import static board.Constants.*;

public abstract class Piece {

    public Piece() {
    }

    public static boolean isOpponentPiece(int[] boardColors, int opponentSide, int square) {
        return boardColors[square] == opponentSide;
    }

    public static int getNextSquare(int currentSquare, int pieceType, int direction) {
        return mailbox[mailbox64[currentSquare] + offset[pieceType][direction]];
    }

    void generateMovesInDirection(int square, int direction) {

    }
}
