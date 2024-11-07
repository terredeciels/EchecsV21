package board;

import static board.Constants.CASTLE_MASK;
import static board.Constants.PROMOTION_FLAG;

/**
 * 1	capture 2	castle 4	en passant capture 8	pushing a pawn 2 squares 16	pawn
 * move 32	promote
 */
public class Move {

    byte from;
    byte to;
    byte promote;
    byte bits;

    Move() {
    }

    Move(byte from, byte to, byte promote, byte bits) {
        this.from = from;
        this.to = to;
        this.promote = promote;
        this.bits = bits;
    }

    public static boolean isPromotionFlagSet(int moveFlags) {
        return (moveFlags & PROMOTION_FLAG) != 0;
    }

    public boolean makemove(Board board) {
        if ((bits & 2) != 0) {
            int from;
            int to;

            if (board.king.inCheck(board.side)) return false;
            switch (this.to) {
                case 62:
                    if (board.color[Constants.F1] != Constants.EMPTY || board.color[Constants.G1] != Constants.EMPTY || board.king.isAttacked(Constants.F1, board.xside) || board.king.isAttacked(Constants.G1, board.xside)) {
                        return false;
                    }
                    from = Constants.H1;
                    to = Constants.F1;
                    break;
                case 58:
                    if (board.color[Constants.B1] != Constants.EMPTY || board.color[Constants.C1] != Constants.EMPTY || board.color[Constants.D1] != Constants.EMPTY || board.king.isAttacked(Constants.C1, board.xside) || board.king.isAttacked(Constants.D1, board.xside)) {
                        return false;
                    }
                    from = Constants.A1;
                    to = Constants.D1;
                    break;
                case 6:
                    if (board.color[Constants.F8] != Constants.EMPTY || board.color[Constants.G8] != Constants.EMPTY || board.king.isAttacked(Constants.F8, board.xside) || board.king.isAttacked(Constants.G8, board.xside)) {
                        return false;
                    }
                    from = Constants.H8;
                    to = Constants.F8;
                    break;
                case 2:
                    if (board.color[Constants.B8] != Constants.EMPTY || board.color[Constants.C8] != Constants.EMPTY || board.color[Constants.D8] != Constants.EMPTY || board.king.isAttacked(Constants.C8, board.xside) || board.king.isAttacked(Constants.D8, board.xside)) {
                        return false;
                    }
                    from = Constants.A8;
                    to = Constants.D8;
                    break;
                default: // shouldn't get here
                    from = -1;
                    to = -1;
                    break;
            }
            board.color[to] = board.color[from];
            board.piece[to] = board.piece[from];
            board.color[from] = Constants.EMPTY;
            board.piece[from] = Constants.EMPTY;
        }

        /* back up information, so we can take the move back later. */
        board.um.mov = this;
        board.um.capture = board.piece[to];
        board.um.castle = board.castle;
        board.um.ep = board.ep;
        board.um.fifty = board.fifty;

        board.castle &= CASTLE_MASK[from] & CASTLE_MASK[to];

        if ((bits & 8) != 0) board.ep = board.side == Constants.LIGHT ? to + 8 : to - 8;
        else board.ep = -1;

        board.fifty = (bits & 17) != 0 ? 0 : board.fifty + 1;

        /* move the piece */
        board.color[to] = board.side;
        board.piece[to] = (bits & 32) != 0 ? promote : board.piece[from];
        board.color[from] = Constants.EMPTY;
        board.piece[from] = Constants.EMPTY;

        /* erase the pawn if this is an en passant move */
        if ((bits & 4) != 0) {
            int offset = (board.side == Constants.LIGHT) ? 8 : -8;
            board.color[to + offset] = board.piece[to + offset] = Constants.EMPTY;
        }

        board.side ^= 1;
        board.xside ^= 1;
        if (board.king.inCheck(board.xside)) {
            board.um.takeback(board);
            return false;
        }

        return true;
    }


}
