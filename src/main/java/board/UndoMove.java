package board;

public class UndoMove {

    public Move mov = new Move();
    public int capture;
    public int castle;
    public int ep;
    public int fifty;

    public void takeback(Board board) {
        board.side ^= 1;
        board.xside ^= 1;

        Move m = mov;
        board.castle = castle;
        board.ep = ep;
        board.fifty = fifty;

        board.color[m.from] = board.side;
        if ((m.bits & 32) != 0) {
            board.piece[m.from] = Constants.PAWN;
        } else {
            board.piece[m.from] = board.piece[m.to];
        }
        if (capture == Constants.EMPTY) {
            board.color[m.to] = Constants.EMPTY;
            board.piece[m.to] = Constants.EMPTY;
        } else {
            board.color[m.to] = board.xside;
            board.piece[m.to] = capture;
        }
        if ((m.bits & 2) != 0) {
            int from;
            int to;

            switch (m.to) {
                case 62:
                    from = Constants.F1;
                    to = Constants.H1;
                    break;
                case 58:
                    from = Constants.D1;
                    to = Constants.A1;
                    break;
                case 6:
                    from = Constants.F8;
                    to = Constants.H8;
                    break;
                case 2:
                    from = Constants.D8;
                    to = Constants.A8;
                    break;
                default: // shouldn't get here
                    from = -1;
                    to = -1;
                    break;
            }
            board.color[to] = board.side;
            board.piece[to] = Constants.ROOK;
            board.color[from] = Constants.EMPTY;
            board.piece[from] = Constants.EMPTY;
        }
        if ((m.bits & 4) != 0) {
            if (board.side == Constants.LIGHT) {
                board.color[m.to + 8] = board.xside;
                board.piece[m.to + 8] = Constants.PAWN;
            } else {
                board.color[m.to - 8] = board.xside;
                board.piece[m.to - 8] = Constants.PAWN;
            }
        }
    }
    //public int hash;
}
