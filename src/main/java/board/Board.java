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

    public void generateMoves() {
        range(0, BOARD_SIZE).filter(square -> color[square] == side).forEach(square -> {
            if (piece[square] == PAWN) {
                pion.generatePawnMoves(square);
            } else {
                generatePieceMoves(square);
            }
        });
        roi.gen_castles();
        pion.gen_enpassant();
    }

    private void generatePieceMoves(int square) {
        int pieceType = piece[square];
        for (int direction = 0; direction < offsets[pieceType]; ++direction)
            generateMovesInDirection(square, pieceType, direction);
    }

    private void generateMovesInDirection(int startSquare, int pieceType, int direction) {
        int currentSquare = startSquare;

        while (true) {
            currentSquare = getNextSquare(currentSquare, pieceType, direction);
            if (isOutOfBounds(currentSquare)) break;
            if (isOccupied(color, currentSquare)) {
                occupiedSquare(startSquare, currentSquare);
                break;
            }
            // Ajouter le mouvement si la case est libre
            addMove(startSquare, currentSquare, 0);
            // Si la pièce ne peut pas glisser, arrêter la boucle
            if (!canSlide(pieceType)) break;
        }
    }


    void occupiedSquare(int fromSquare, int toSquare) {
        if (isOpponentPiece(color, xside, toSquare)) addMove(fromSquare, toSquare, 1);  // Capture
    }


    public void addMove(int from, int to, int bits) {
        if (isPromotionMove(bits, to)) pion.gen_promote(from, to, bits);
        else addStandardMove(from, to, bits);
    }

    private boolean isPromotionMove(int moveFlags, int destinationSquare) {
        return isPromotionFlagSet(moveFlags) && isOnPromotionRank(destinationSquare);
    }

    private boolean isOnPromotionRank(int destinationSquare) {
        return side == LIGHT ? destinationSquare >= A8 && destinationSquare <= H8 : destinationSquare >= A1 && destinationSquare <= H1;
    }

    private void addStandardMove(int fromSquare, int toSquare, int moveFlags) {
        pseudomoves.add(new Move((byte) fromSquare, (byte) toSquare, (byte) 0, (byte) moveFlags));
    }


}

