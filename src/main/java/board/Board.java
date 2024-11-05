package board;

import java.util.ArrayList;
import java.util.List;

import static board.ChessUtils.*;
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
    public Pion pion;  // Classe pour les mouvements des pions
    public Roi roi;  // Classe pour les mouvements du roi

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

    /**
     * Génère tous les mouvements possibles pour l'état actuel de l'échiquier.
     */
    public void generateMoves() {
        range(0, BOARD_SIZE).filter(square -> color[square] == side).forEach(square -> {
            if (piece[square] == PAWN) {
                pion.generatePawnMoves(square);
            } else {
                generatePieceMoves(square);
            }
        });
        roi.gen_castles();  // Génère les mouvements de roque possibles
        pion.gen_enpassant();  // Génère les prises en passant possibles
    }

    /**
     * Génère les mouvements possibles pour une pièce donnée.
     *
     * @param square La case sur laquelle se trouve la pièce.
     */
    private void generatePieceMoves(int square) {
        int pieceType = piece[square];
        for (int direction = 0; direction < offsets[pieceType]; ++direction) {
            generateMovesInDirection(square, pieceType, direction);
        }
    }

    /**
     * Génère les mouvements dans une direction donnée pour une pièce.
     *
     * @param startSquare La case de départ de la pièce.
     * @param pieceType Le type de la pièce (tour, fou, etc.).
     * @param direction La direction de déplacement de la pièce.
     */
    private void generateMovesInDirection(int startSquare, int pieceType, int direction) {
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
     * @param toSquare La case d'arrivée.
     */
    private void handleOccupiedSquare(int fromSquare, int toSquare) {
        if (isOpponentPiece(color, xside, toSquare)) {
            addMove(fromSquare, toSquare, 1);  // Capture
        }
    }

    /**
     * Ajoute un mouvement à la liste des mouvements possibles.
     *
     * @param from La case de départ.
     * @param to La case d'arrivée.
     * @param bits Les informations supplémentaires sur le mouvement.
     */
    public void addMove(int from, int to, int bits) {
        if (isPromotionMove(bits, to)) {
            pion.gen_promote(from, to, bits);
        } else {
            addStandardMove(from, to, bits);
        }
    }

    /**
     * Vérifie si le mouvement est une promotion de pion.
     *
     * @param moveFlags Les drapeaux du mouvement.
     * @param destinationSquare La case de destination.
     * @return true si c'est un mouvement de promotion, sinon false.
     */
    private boolean isPromotionMove(int moveFlags, int destinationSquare) {
        return isPromotionFlagSet(moveFlags) && isOnPromotionRank(destinationSquare);
    }

    /**
     * Vérifie si le drapeau de promotion est défini dans les flags du mouvement.
     *
     * @param moveFlags Les drapeaux du mouvement.
     * @return true si le drapeau de promotion est défini, sinon false.
     */
    private boolean isPromotionFlagSet(int moveFlags) {
        return (moveFlags & PROMOTION_FLAG) != 0;
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
     * @param toSquare La case d'arrivée.
     * @param moveFlags Les informations supplémentaires sur le mouvement.
     */
    private void addStandardMove(int fromSquare, int toSquare, int moveFlags) {
        pseudomoves.add(new Move((byte) fromSquare, (byte) toSquare, (byte) 0, (byte) moveFlags));
    }
}
