package board;

import static board.Board.isOccupied;
import static board.Board.isOutOfBounds;
import static board.Constants.*;
import static java.util.stream.IntStream.range;

class King extends Piece {
    private final Board board;

    public King(Board board) {
        this.board = board;
    }

    public void genCastles() {
        int[][] castleMoves = {{LIGHT, 1, E1, G1, 2}, {LIGHT, 2, E1, C1, 2}, {DARK, 4, E8, G8, 2}, {DARK, 8, E8, C8, 2}};
        for (int[] move : castleMoves) {
            if (board.side == move[0] && (board.castle & move[1]) != 0) {
                board.addMove(move[2], move[3], move[4]);
            }
        }
    }

    public boolean inCheck(int side) {
        return range(0, BOARD_SIZE)
                .filter(i -> board.piece[i] == KING && board.color[i] == side)
                .anyMatch(i -> isAttacked(i, side ^ 1));
    }

    public boolean isAttacked(int sqTarget, int side) {
        return range(0, BOARD_SIZE)
                .filter(sq -> board.color[sq] == side)
                .anyMatch(sq -> isAttackedByPiece(sq, sqTarget, board.piece[sq], side));
    }

    private boolean isAttackedByPiece(int sq, int sqTarget, int pieceType, int side) {
        if (pieceType == PAWN) {
            int offset = (side == LIGHT) ? -8 : 8;
            return (sq & 7) != 0 && sq + offset - 1 == sqTarget ||
                    (sq & 7) != 7 && sq + offset + 1 == sqTarget;
        }
        return range(0, OFFSETS[pieceType])
                .anyMatch(o -> isAttackedByOffset(sq, sqTarget, pieceType, o));
    }

    private boolean isAttackedByOffset(int sq, int sqTarget, int pieceType, int offsetIndex) {
        int sqIndex = sq;
        while ((sqIndex = MAILBOX[MAILBOX64[sqIndex] + OFFSET[pieceType][offsetIndex]]) != -1) {
            if (sqIndex == sqTarget) return true;
            if (board.color[sqIndex] != EMPTY || !SLIDE[pieceType]) break;
        }
        return false;
    }

    public void generateMovesInDirection(int square, int direction) {
        int currentSquare = getNextSquare(square, KING, direction);
        if (!isOutOfBounds(currentSquare)) {
            if (isOccupied(board.color, currentSquare)) {
                board.handleOccupiedSquare(square, currentSquare);
            } else {
                board.addMove(square, currentSquare, 0);
            }
        }
    }
}
