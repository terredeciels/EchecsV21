package board;

import static board.Board.isOccupied;
import static board.Board.isOutOfBounds;
import static board.Constants.KING;
import static java.util.stream.IntStream.range;

class King extends Piece {
    private final Board board;

    public King(Board board) {
        this.board = board;
    }

    public void genCastles() {
        if (board.side == Constants.LIGHT) {
            if ((board.castle & 1) != 0) board.addMove(Constants.E1, Constants.G1, 2);
            if ((board.castle & 2) != 0) board.addMove(Constants.E1, Constants.C1, 2);
        } else {
            if ((board.castle & 4) != 0) board.addMove(Constants.E8, Constants.G8, 2);
            if ((board.castle & 8) != 0) board.addMove(Constants.E8, Constants.C8, 2);
        }
    }

    public boolean in_check(int s) {
        return range(0, Board.BOARD_SIZE)
                .filter(i -> board.piece[i] == KING && board.color[i] == s)
                .anyMatch(i -> isAttacked(i, s ^ 1));
    }

    public boolean isAttacked(int sqTarget, int side) {
        return range(0, Board.BOARD_SIZE)
                .filter(sq -> board.color[sq] == side)
                .anyMatch(sq -> isAttackedByPiece(sq, sqTarget, board.piece[sq], side));
    }

    private boolean isAttackedByPiece(int sq, int sqTarget, int pieceType, int side) {
        if (pieceType == Constants.PAWN) {
            int offset = (side == Constants.LIGHT) ? -8 : 8;
            return (sq & 7) != 0 && sq + offset - 1 == sqTarget ||
                    (sq & 7) != 7 && sq + offset + 1 == sqTarget;
        } else return range(0, Constants.offsets[pieceType])
                .anyMatch(o -> isAttackedByOffset(sq, sqTarget, pieceType, o));
    }

    private boolean isAttackedByOffset(int sq, int sqTarget, int pieceType, int offsetIndex) {
        int sqIndex = sq;
        while ((sqIndex = Constants.mailbox[Constants.mailbox64[sqIndex] + Constants.offset[pieceType][offsetIndex]]) != -1) {
            if (sqIndex == sqTarget) return true;
            if (board.color[sqIndex] != Constants.EMPTY || !Constants.slide[pieceType]) break;
        }
        return false;
    }


    public void generateMovesInDirection(int square, int direction) {
        int currentSquare = square;

// Logique pour le roi uniquement
        currentSquare = getNextSquare(currentSquare, KING, direction);
        if (!isOutOfBounds(currentSquare)) {
            if (isOccupied(board.color, currentSquare)) {
                board.handleOccupiedSquare(square, currentSquare);
            } else {
                board.addMove(square, currentSquare, 0);  // Ajouter le mouvement si la case est libre
            }
        }

    }
}
