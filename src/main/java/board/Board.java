package board;

import java.util.ArrayList;
import java.util.List;

import static board.Move.isPromotionFlagSet;
import static board.Piece.*;
import static java.lang.System.arraycopy;
import static java.util.stream.IntStream.range;

public class Board implements Constants {

    public static final int BOARD_SIZE = 64;

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

    public Board() {
        pawn = new Pawn(this);
        king = new King(this);
        rook=new Rook(this);
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
        rook=new Rook(this);
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

    /**
     * Génère les mouvements possibles pour une pièce donnée.
     *
     * @param square La case sur laquelle se trouve la pièce.
     */
//    private void generatePieceMoves2(int square) {
//        int pieceType = piece[square];
//        for (int direction = 0; direction < offsets[pieceType]; ++direction) {
//            generateMovesInDirection(square, pieceType, direction);
//        }
//    }

    private void generatePieceMoves(int square) {
        int pieceType = piece[square];
        switch (pieceType) {
            case KING:
                for (int direction = 0; direction < offsets[KING]; ++direction) {
                    king.generateMovesInDirection(square, direction);
                }
                break;

            case QUEEN:
                for (int direction = 0; direction < offsets[QUEEN]; ++direction) {
                    generateMovesInDirection(square, QUEEN, direction);
                }
                break;

            case ROOK:
                for (int direction = 0; direction < offsets[ROOK]; ++direction) {
                    rook.generateMovesInDirection(square, direction);
                }
                break;

            case BISHOP:
                for (int direction = 0; direction < offsets[BISHOP]; ++direction) {
                    generateMovesInDirection(square, BISHOP, direction);
                }
                break;

            case KNIGHT:
                for (int direction = 0; direction < offsets[KNIGHT]; ++direction) {
                    generateMovesInDirection(square, KNIGHT, direction);
                }
                break;

            default:
                throw new IllegalArgumentException("Type de pièce invalide: " + pieceType);
        }
    }

    /**
     * Génère les mouvements dans une direction donnée pour une pièce.
     *
     * @param startSquare La case de départ de la pièce.
     * @param pieceType   Le type de la pièce (tour, fou, etc.).
     * @param direction   La direction de déplacement de la pièce.
     */
    public void generateMovesInDirection(int startSquare, int pieceType, int direction) {
        int currentSquare = startSquare;

        while (true) {
            currentSquare = getNextSquare(currentSquare, pieceType, direction);
            if (isOutOfBounds(currentSquare)) break;
            if (isOccupied(color, currentSquare)) {
                handleOccupiedSquare(startSquare, currentSquare);
                break;
            }
            addMove(startSquare, currentSquare, 0);  // Ajouter le mouvement si la case est libre
            if (!canSlide(pieceType)) break;  // Si la pièce ne peut pas glisser, arrêter la boucle
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
