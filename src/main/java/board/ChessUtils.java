package board;

public class ChessUtils {
    public static boolean isOpponentPiece(int[] color, int xside, int square) {
        return color[square] == xside;
    }

    public static boolean isOccupied(int[] color, int square) {
        return color[square] != Constants.EMPTY;
    }

    public static boolean isOutOfBounds(int square) {
        return square == -1;
    }
}
