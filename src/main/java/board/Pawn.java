package board;

class Pawn {
    private final Board board;

    public Pawn(Board board) {
        this.board = board;
    }

    public void generatePawnMoves(int c) {
        final int offset = (board.side == Constants.LIGHT) ? -8 : 8;
        final int oppositeColor = board.side ^ 1;

        if ((c & 7) != 0 && board.color[c + offset - 1] == oppositeColor) board.addMove(c, c + offset - 1, 17);
        if ((c & 7) != 7 && board.color[c + offset + 1] == oppositeColor) board.addMove(c, c + offset + 1, 17);

        if (board.color[c + offset] == Constants.EMPTY) {
            board.addMove(c, c + offset, 16);
            if ((board.side == Constants.LIGHT && c >= 48) || (board.side == Constants.DARK && c <= 15))
                if (board.color[c + (offset << 1)] == Constants.EMPTY) board.addMove(c, c + (offset << 1), 24);
        }
    }

    public void gen_enpassant() {
        if (board.ep != -1) {
            if (board.side == Constants.LIGHT) {
                if ((board.ep & 7) != 0 && board.color[board.ep + 7] == Constants.LIGHT && board.piece[board.ep + 7] == Constants.PAWN)
                    board.addMove(board.ep + 7, board.ep, 21);
                if ((board.ep & 7) != 7 && (board.color[board.ep + 9] == Constants.LIGHT && board.piece[board.ep + 9] == Constants.PAWN))
                    board.addMove(board.ep + 9, board.ep, 21);
            } else {
                if ((board.ep & 7) != 0 && (board.color[board.ep - 9] == Constants.DARK && board.piece[board.ep - 9] == Constants.PAWN))
                    board.addMove(board.ep - 9, board.ep, 21);
                if ((board.ep & 7) != 7 && (board.color[board.ep - 7] == Constants.DARK && board.piece[board.ep - 7] == Constants.PAWN))
                    board.addMove(board.ep - 7, board.ep, 21);
            }
        }
    }

    public void gen_promote(int from, int to, int bits) {
        for (int i = Constants.KNIGHT; i <= Constants.QUEEN; ++i)
            board.pseudomoves.add(new Move((byte) from, (byte) to, (byte) i, (byte) (bits | 32)));
    }
}
