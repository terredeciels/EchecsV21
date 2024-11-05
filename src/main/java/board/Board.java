package board;

import java.util.ArrayList;
import java.util.List;

import static board.ChessUtils.*;
import static java.lang.System.arraycopy;
import static java.util.stream.IntStream.range;

public class Board implements Constants {

    public static final int BOARD_SIZE = 64;
    public int[] color = new int[BOARD_SIZE];
    public int[] piece = new int[BOARD_SIZE];

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

    public void gen() {
        range(0, BOARD_SIZE).filter(square -> color[square] == side).forEach(square -> {
            if (piece[square] == PAWN) {
                pion.gen_pawn(square);
            } else {
                generatePieceMoves(square);
            }
        });
        roi.gen_castles();
        pion.gen_enpassant();
    }

    private void generatePieceMoves(int square) {
        int pieceType = piece[square];
        for (int direction = 0; direction < offsets[pieceType]; ++direction) {
            generateMovesInDirection(square, pieceType, direction);
        }
    }

    private void generateMovesInDirection(int square, int pieceType, int direction) {
        int currentSquare = square;
        while (true) {
            currentSquare = mailbox[mailbox64[currentSquare] + offset[pieceType][direction]];
            if (isOutOfBounds(currentSquare)) break;
            if (isOccupied(color, currentSquare)) {
                if (isOpponentPiece(color, xside, currentSquare)) {
                    addMove(square, currentSquare, 1);
                }
                break;
            }
            addMove(square, currentSquare, 0);
            if (!slide[pieceType]) break;
        }
    }

    public void addMove(int from, int to, int bits) {
        if ((bits & 16) != 0 && (side == LIGHT ? to <= H8 : to >= A1)) {
            pion.gen_promote(from, to, bits);
        } else {
            pseudomoves.add(new Move((byte) from, (byte) to, (byte) 0, (byte) bits));
        }
    }
}

