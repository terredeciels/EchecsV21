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

    public static final int BOARD_SIZE = 64;
    private final Map<Integer, Piece> pieceMap = new HashMap<>();
    // Tableaux représentant les couleurs et les pièces sur l'échiquier
    public int[] color = new int[BOARD_SIZE];
    public int[] piece = new int[BOARD_SIZE];
    // Informations sur l'état de la partie
    public int side;  // Joueur courant
    public int xside;  // Joueur adverse
    public int castle;  // Droit de roque
    public int ep;  // Case de prise en passant
    public int halfMoveClock;
    public int plyNumber;
    public int fifty;
    public List<Move> pseudomoves = new ArrayList<>();  // Liste des pseudo-mouvements
    public UndoMove um = new UndoMove();  // Historique des mouvements
    public Pawn pawn;  // Classe pour les mouvements des pions
    public King king;  // Classe pour les mouvements du king
    public Rook rook;
    public Bishop bishop;
    public Queen queen;
    public Knight knight;


    public Board() {

        pawn = new Pawn(this);
        king = new King(this);
        rook = new Rook(this);
        bishop = new Bishop(this);
        queen = new Queen(this);
        knight = new Knight(this);

        pieceMap.put(KING, king);
        pieceMap.put(QUEEN, queen);
        pieceMap.put(ROOK, rook);
        pieceMap.put(BISHOP, bishop);

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

        pawn = new Pawn(this);
        king = new King(this);
        rook = new Rook(this);
        bishop = new Bishop(this);
        queen = new Queen(this);
        knight = new Knight(this);
        pieceMap.put(KING, king);
        pieceMap.put(QUEEN, queen);
        pieceMap.put(ROOK, rook);
        pieceMap.put(BISHOP, bishop);
    }

    public static boolean isOccupied(int[] boardColors, int square) {
        return boardColors[square] != EMPTY;
    }

    public static boolean isOutOfBounds(int square) {
        return square < 0;
    }

    /**
     * Génère tous les mouvements possibles pour l'état actuel de l'échiquier.
     */
    public void generateMoves() {
        range(0, BOARD_SIZE).filter(square -> color[square] == side).forEach(square -> {
            if (piece[square] == PAWN) {
                pawn.generatePawnMoves(square);
            } else {
                generatePieceMoves(square);
            }
        });
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
        if (piece != null) {
            for (int direction = 0; direction < offsets[pieceType]; ++direction) {
                piece.generateMovesInDirection(square, direction);
            }
        } else {
            throw new IllegalArgumentException("Type de pièce non valide : " + pieceType);
        }
    }

    /**
     * Gère le cas où la case est occupée.
     *
     * @param fromSquare La case de départ.
     * @param toSquare   La case d'arrivée.
     */
    void handleOccupiedSquare(int fromSquare, int toSquare) {
        if (isOpponentPiece(color, xside, toSquare)) addMove(fromSquare, toSquare, 1);  // Capture
    }

    /**
     * Vérifie si le mouvement est une promotion de pawn.
     *
     * @param moveFlags         Les drapeaux du mouvement.
     * @param destinationSquare La case de destination.
     * @return true si c'est un mouvement de promotion, sinon false.
     */
    private boolean isPromotionMove(int moveFlags, int destinationSquare) {
        return isPromotionFlagSet(moveFlags) && isOnPromotionRank(destinationSquare);
    }

    /**
     * Vérifie si la case de destination est sur la rangée de promotion.
     *
     * @param destinationSquare La case de destination.
     * @return true si la case est sur la rangée de promotion, sinon false.
     */
    private boolean isOnPromotionRank(int destinationSquare) {
        return side == LIGHT ? destinationSquare >= A8 && destinationSquare <= H8 : destinationSquare >= A1 && destinationSquare <= H1;
    }

    /**
     * Ajoute un mouvement standard (non promotion) à la liste des mouvements.
     *
     * @param fromSquare La case de départ.
     * @param toSquare   La case d'arrivée.
     * @param moveFlags  Les informations supplémentaires sur le mouvement.
     */
    private void addStandardMove(int fromSquare, int toSquare, int moveFlags) {
        pseudomoves.add(new Move((byte) fromSquare, (byte) toSquare, (byte) 0, (byte) moveFlags));
    }

    void addMove(int from, int to, int bits) {
        if (isPromotionMove(bits, to)) pawn.gen_promote(from, to, bits);
        else addStandardMove(from, to, bits);
    }
}
