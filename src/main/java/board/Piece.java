package board;

import static board.Constants.*;
import static board.Constants.offset;

public class Piece {


    public Piece() {
    }

    public static boolean isOpponentPiece(int[] boardColors, int opponentSide, int square) {
        return boardColors[square] == opponentSide;
    }   public static boolean canSlide(int pieceType) {
        return slide[pieceType];
    }  public static int getNextSquare(int currentSquare, int pieceType, int direction) {
        return mailbox[mailbox64[currentSquare] + offset[pieceType][direction]];
    }
}
