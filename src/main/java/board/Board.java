package board;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.arraycopy;
import static java.util.stream.IntStream.range;

public class Board implements Constants {

    public static final int BOARD_SIZE = 64;
    public int[] color = new int[BOARD_SIZE];
    public int[] piece = new int[BOARD_SIZE];

    public Piece[] pieces = new Piece[BOARD_SIZE];
    public int side;
    public int xside;
    public int castle;
    public int ep;
    public List<Move> pseudomoves = new ArrayList<>();
    public int halfMoveClock;
    public int plyNumber;
    public int fifty;
    public UndoMove um = new UndoMove();
    public Pion pion;
    public Roi roi;

    public Board() {
        initPieces();
        pion = new Pion(this);
        roi = new Roi(this);
    }

    public Board(Board board) {
        arraycopy(board.color, 0, color, 0, BOARD_SIZE);
        arraycopy(board.piece, 0, piece, 0, BOARD_SIZE);
        side = board.side;
        xside = board.xside;
        castle = board.castle;
        ep = board.ep;
        fifty = board.fifty;
        pseudomoves = new ArrayList<>();
        um = new UndoMove();
        pion = new Pion(this);
        roi = new Roi(this);
    }

    private void initPieces() {
        for (int c = 0; c < BOARD_SIZE; c++) {
            pieces[c] = new Piece();
        }
    }

    public void gen() {
        range(0, BOARD_SIZE)
                .filter(c -> color[c] == side)
                .forEach(c -> {
                    if (piece[c] == PAWN) pion.gen_pawn(c);
                    else gen(c);
                });
        roi.gen_castles();
        pion.gen_enpassant();
    }

//    private void gen_castles() {
//        roi.gen_castles();
//    }

    private void gen(int c) {
        int p = piece[c];
        for (int d = 0; d < offsets[p]; ++d) {
            int _c = c;
            while (true) {
                _c = mailbox[mailbox64[_c] + offset[p][d]];
                if (_c == -1) break;
                if (color[_c] != EMPTY) {
                    if (color[_c] == xside) gen_push(c, _c, 1);
                    break;
                }
                gen_push(c, _c, 0);
                if (!slide[p]) break;
            }
        }
    }

    public void gen_push(int from, int to, int bits) {
        if ((bits & 16) != 0 && (side == LIGHT ? to <= H8 : to >= A1)) {
            pion.gen_promote(from, to, bits);
            return;
        }
        pseudomoves.add(new Move((byte) from, (byte) to, (byte) 0, (byte) bits));
    }

}

