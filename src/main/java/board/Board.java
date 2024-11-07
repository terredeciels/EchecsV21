package board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static board.Move.isPromotionFlagSet;
import static board.Piece.isOpponentPiece;
import static java.lang.System.arraycopy;
import static java.util.stream.IntStream.range;

public class Board implements Constants {
    public final Map<Integer, Piece> pieceMap = new HashMap<>();
    public final King king;
    private final Pawn pawn;
    public int[] color = new int[Constants.BOARD_SIZE], piece = new int[Constants.BOARD_SIZE];
    public int side, xside, castle, ep, halfMoveClock, plyNumber, fifty;
    public List<Move> pseudomoves = new ArrayList<>();
    public UndoMove um = new UndoMove();
    public Rook rook;
    public Bishop bishop;
    public Queen queen;
    public Knight knight;


    public Board() {
        // Initialize pieces and add relevant ones to pieceMap
        pieceMap.put(KING, king = new King(this));
        pieceMap.put(QUEEN, queen = new Queen(this));
        pieceMap.put(ROOK, rook = new Rook(this));
        pieceMap.put(BISHOP, bishop = new Bishop(this));
        knight = new Knight(this);
        pawn = new Pawn(this);
    }

    public Board(Board board) {
        // Copy arrays
        arraycopy(board.color, 0, color, 0, Constants.BOARD_SIZE);
        arraycopy(board.piece, 0, piece, 0, Constants.BOARD_SIZE);

        // Copy primitive fields
        side = board.side;
        xside = board.xside;
        castle = board.castle;
        ep = board.ep;
        fifty = board.fifty;

        // Initialize move list and undo move
        pseudomoves = new ArrayList<>();
        um = new UndoMove();

        // Initialize pieces and add relevant ones to pieceMap
        pieceMap.put(KING, king = new King(this));
        pieceMap.put(QUEEN, queen = new Queen(this));
        pieceMap.put(ROOK, rook = new Rook(this));
        pieceMap.put(BISHOP, bishop = new Bishop(this));
        knight = new Knight(this);
        pawn = new Pawn(this);
    }

    public static boolean isOccupied(int[] boardColors, int square) {
        return boardColors[square] != EMPTY;
    }

    public static boolean isOutOfBounds(int square) {
        return square < 0;
    }

    public void generateMoves() {
        range(0, Constants.BOARD_SIZE).filter(square -> color[square] == side).forEach(this::isPawn);
        king.genCastles();  // Génère les mouvements de roque possibles
        pawn.genEnpassant();  // Génère les prises en passant possibles
    }

    private void generatePieceMoves(int square) {
        int pieceType = piece[square];
        switch (pieceType) {
            case KING:
            case QUEEN:
            case ROOK:
            case BISHOP:
                generateSlidingPieceMoves(pieceType, square);
                break;

            case KNIGHT:
                knight.generateMovesInDirection(square);
                break;

            default:
                throw new IllegalArgumentException("Type de pièce invalide: " + pieceType);
        }
    }

    private void generateSlidingPieceMoves(int pieceType, int square) {
        Piece piece = pieceMap.get(pieceType);
        if (piece != null) for (int direction = 0; direction < OFFSETS[pieceType]; ++direction)
            piece.generateMovesInDirection(square, direction);
        else throw new IllegalArgumentException("Type de pièce non valide : " + pieceType);
    }

    void handleOccupiedSquare(int fromSquare, int toSquare) {
        if (isOpponentPiece(color, xside, toSquare)) addMove(fromSquare, toSquare, 1);  // Capture
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

    void addMove(int from, int to, int bits) {
        if (isPromotionMove(bits, to)) pawn.gen_promote(from, to, bits);
        else addStandardMove(from, to, bits);
    }

    private void isPawn(int square) {
        if (piece[square] == PAWN) pawn.generatePawnMoves(square);
        else generatePieceMoves(square);
    }
}
