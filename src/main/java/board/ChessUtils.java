package board;

import static board.Constants.*;

public class ChessUtils {

    /**
     * Vérifie si la pièce sur la case donnée appartient à l'adversaire.
     *
     * @param boardColors  Tableau représentant les couleurs des pièces sur l'échiquier.
     * @param opponentSide La couleur du camp adverse.
     * @param square       L'indice de la case à vérifier.
     * @return true si la case contient une pièce adverse, sinon false.
     */
    public static boolean isOpponentPiece(int[] boardColors, int opponentSide, int square) {
        return boardColors[square] == opponentSide;
    }

    /**
     * Vérifie si la case donnée est occupée par une pièce.
     *
     * @param boardColors Tableau représentant les couleurs des pièces sur l'échiquier.
     * @param square      L'indice de la case à vérifier.
     * @return true si la case est occupée, sinon false.
     */
    public static boolean isOccupied(int[] boardColors, int square) {
        return boardColors[square] != EMPTY;
    }

    /**
     * Vérifie si la case donnée est hors de l'échiquier.
     *
     * @param square L'indice de la case à vérifier.
     * @return true si la case est hors de l'échiquier, sinon false.
     */
    public static boolean isOutOfBounds(int square) {
        return square < 0;
    }

    /**
     * Vérifie si la pièce donnée peut glisser sur plusieurs cases (comme une tour, un fou ou une reine).
     *
     * @param pieceType Le type de la pièce à vérifier.
     * @return true si la pièce peut glisser, sinon false.
     */
    public static boolean canSlide(int pieceType) {
        return slide[pieceType];
    }

    public static int getNextSquare(int currentSquare, int pieceType, int direction) {
        return mailbox[mailbox64[currentSquare] + offset[pieceType][direction]];
    }

    /**
     * Vérifie si le drapeau de promotion est défini dans les flags du mouvement.
     *
     * @param moveFlags Les drapeaux du mouvement.
     * @return true si le drapeau de promotion est défini, sinon false.
     */
    public static boolean isPromotionFlagSet(int moveFlags) {
        return (moveFlags & PROMOTION_FLAG) != 0;
    }
}

