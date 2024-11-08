package board;

public interface IBoard {
    static boolean isOccupied(int[] boardColors, int square) {
        return boardColors[square] != Constants.EMPTY;
    }

    static boolean isOutOfBounds(int square) {
        return square < 0;
    }
}
