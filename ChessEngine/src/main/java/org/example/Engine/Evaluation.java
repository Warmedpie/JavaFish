package org.example.Engine;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;

public class Evaluation {

    int mgPawn = 82;
    int mgKnight = 337;
    int mgBishop = 365;
    int mgRook = 477;
    int mgQueen = 1025;

    int egPawn = 101;
    int egKnight = 300;
    int egBishop = 319;
    int egRook = 550;
    int egQueen = 1005;

    int[] mgPawnTableWhite;
    int[] mgPawnTableBlack;

    int[] egPawnTableWhite;
    int[] egPawnTableBlack;

    int[] knightTable;

    int[] bishopTableWhite;
    int[] bishopTableBlack;

    int[] rookTableWhite;
    int[] rookTableBlack;

    int[] queenTable;

    int[] mgKingTableWhite;
    int[] mgKingTableBlack;

    int[] egKingTableWhite;
    int[] egKingTableBlack;

    int[] kingAttackers;

    public Evaluation() {
        mgPawnTableBlack = new int[]
                {       0,   0,   0,   0,   0,   0,  0,   0,
                        98, 134,  61,  95,  68, 126, 34, -11,
                        -6,   7,  26,  31,  65,  56, 25, -20,
                        -14,  13,   6,  21,  23,  12, 17, -23,
                        -27,  -2,  -5,  12,  17,   6, 10, -25,
                        -26,  -4,  -4, -10,   3,   3, 33, -12,
                        -35,  -1, -20, -23, -15,  24, 38, -22,
                        0,   0,   0,   0,   0,   0,  0,   0,
                };

        mgPawnTableWhite = new int[]
                {       0,   0,   0,   0,   0,   0,  0,   0,
                        -35,  -1, -20, -23, -15,  24, 38, -22,
                        -26,  -4,  -4, -10,   3,   3, 33, -12,
                        -27,  -2,  -5,  12,  17,   6, 10, -25,
                        -14,  13,   6,  21,  23,  12, 17, -23,
                        -6,   7,  26,  31,  65,  56, 25, -20,
                        98, 134,  61,  95,  68, 126, 34, -11,
                        0,   0,   0,   0,   0,   0,  0,   0,
                };

        egPawnTableBlack = new int[]
                {        0,   0,   0,   0,   0,   0,   0,   0,
                        178, 173, 158, 134, 147, 132, 165, 187,
                        94, 100,  85,  67,  56,  53,  82,  84,
                        32,  24,  13,   5,  -2,   4,  17,  17,
                        13,   9,  -3,  -7,  -7,  -8,   3,  -1,
                        4,   7,  -6,   1,   0,  -5,  -1,  -8,
                        13,   8,   8,  10,  13,   0,   2,  -7,
                        0,   0,   0,   0,   0,   0,   0,   0,
                };

        egPawnTableWhite = new int[]
                {        0,   0,   0,   0,   0,   0,   0,   0,
                        13,   8,   8,  10,  13,   0,   2,  -7,
                        4,   7,  -6,   1,   0,  -5,  -1,  -8,
                        13,   9,  -3,  -7,  -7,  -8,   3,  -1,
                        32,  24,  13,   5,  -2,   4,  17,  17,
                        94, 100,  85,  67,  56,  53,  82,  84,
                        178, 173, 158, 134, 147, 132, 165, 187,
                        0,   0,   0,   0,   0,   0,   0,   0,
                };

        knightTable = new int[]
                {        -50, -40, -30, -30, -30, -30, -40, -50,
                         -40, -20,   0,   0,   0,   0, -20, -40,
                         -30,   0,  10,  15,  15,  10,   0, -30,
                         -30,   5,  15,  20,  20,  15,   5, -30,
                         -30,   5,  15,  20,  20,  15,   5, -30,
                         -30,   0,  10,  15,  15,  10,   0, -30,
                         -40, -20,   0,   0,   0,   0, -20, -40,
                         -50, -40, -30, -30, -30, -30, -40, -50
                };

        bishopTableWhite = new int[]
                {
                        -33,  -3, -14, -21, -13, -12, -39, -21,
                        4,  15,  16,   0,   7,  21,  33,   1,
                        0,  15,  15,  4,  3,  27,  18,  10,
                        -6,  13,  13,  26,  34,  12,  10,   4,
                        -4,   5,  19,  50,  37,  37,   7,  -2,
                        -16,  37,  43,  40,  35,  50,  37,  -2,
                        -26,  16, -18, -13,  30,  59,  18, -47,
                        -29,   4, -82, -37, -25, -42,   7,  -8,
                };

        bishopTableBlack = new int[]
                {       -29,   4, -82, -37, -25, -42,   7,  -8,
                        -26,  16, -18, -13,  30,  59,  18, -47,
                        -16,  37,  43,  40,  35,  50,  37,  -2,
                        -4,   5,  19,  50,  37,  37,   7,  -2,
                        -6,  13,  13,  26,  34,  12,  10,   4,
                        0,  15,  15,  15,  14,  27,  18,  10,
                        4,  15,  16,   0,   7,  21,  33,   1,
                        -33,  -3, -14, -21, -13, -12, -39, -21,
                };

        rookTableWhite = new int[]
                {
                        -19, -13,   1,  17, 16,  7, -37, -26,
                        -44, -16, -20,  -9, -1, 11,  -6, -71,
                        -45, -25, -16, -17,  3,  0,  -5, -33,
                        -36, -26, -12,  -1,  9, -7,   6, -23,
                        -24, -11,   7,  26, 24, 35,  -8, -20,
                        -5,  19,  26,  36, 17, 45,  61,  16,
                        27,  32,  58,  62, 80, 67,  26,  44,
                        32,  42,  32,  51, 63,  9,  31,  43,
                };

        rookTableBlack = new int[]
                {       32,  42,  32,  51, 63,  9,  31,  43,
                        27,  32,  58,  62, 80, 67,  26,  44,
                        -5,  19,  26,  36, 17, 45,  61,  16,
                        -24, -11,   7,  26, 24, 35,  -8, -20,
                        -36, -26, -12,  -1,  9, -7,   6, -23,
                        -45, -25, -16, -17,  3,  0,  -5, -33,
                        -44, -16, -20,  -9, -1, 11,  -6, -71,
                        -19, -13,   1,  17, 16,  7, -37, -26,
                };

        queenTable = new int[]
                {      -20, -10, -10,  -5,  -5, -10, -10, -20,
                       -10,   0,   0,   0,   0,   0,   0, -10,
                       -10,   0,   5,   5,   5,   5,   0, -10,
                       -10,   0,   5,   8,   8,   5,   0, -10,
                       -10,   0,   5,   8,   8,   5,   0, -10,
                       -10,   0,   5,   5,   5,   5,   0, -10,
                       -10,   0,   0,   0,   0,   0,   0, -10,
                       -20, -10, -10,  -5,  -5, -10, -10, -20,
                };

        mgKingTableWhite = new int[]
                {
                        -15,  36,  12, -54,   -40, -28,  44,  14,
                        1,   7,  -8, -64, -43, -16,   9,   8,
                        -14, -14, -22, -46, -44, -30, -15, -27,
                        -49,  -1, -27, -39, -46, -44, -33, -51,
                        -17, -20, -12, -27, -30, -25, -14, -36,
                        -9,  24,   2, -16, -20,   6,  22, -22,
                        29,  -1, -20,  -7,  -8,  -4, -38, -29,
                        -65,  23,  16, -15, -56, -34,   2,  13, 0
                };

        mgKingTableBlack = new int[]
                {       -65,  23,  16, -15, -56, -34,   2,  13,
                        29,  -1, -20,  -7,  -8,  -4, -38, -29,
                        -9,  24,   2, -16, -20,   6,  22, -22,
                        -17, -20, -12, -27, -30, -25, -14, -36,
                        -49,  -1, -27, -39, -46, -44, -33, -51,
                        -14, -14, -22, -46, -44, -30, -15, -27,
                        1,   7,  -8, -64, -43, -16,   9,   8,
                        -15,  36,  12, -54, -40, -28,  44,  14,
                };

        egKingTableWhite = new int[]
                {
                        -53, -34, -21, -11, -28, -14, -24, -43
                        -27, -11,   4,  13,  14,   4,  -5, -17,
                        -19,  -3,  11,  21,  23,  16,   7,  -9,
                        -18,  -4,  21,  24,  27,  23,   9, -11,
                        -8,  22,  24,  27,  26,  33,  26,   3,
                        10,  17,  23,  15,  20,  45,  44,  13,
                        -12,  17,  14,  17,  17,  38,  23,  11,
                        -74, -35, -18, -18, -11,  15,   4, -17, 0
                };

        egKingTableBlack = new int[]
                {           -74, -35, -18, -18, -11,  15,   4, -17,
                        -12,  17,  14,  17,  17,  38,  23,  11,
                        10,  17,  23,  15,  20,  45,  44,  13,
                        -8,  22,  24,  27,  26,  33,  26,   3,
                        -18,  -4,  21,  24,  27,  23,   9, -11,
                        -19,  -3,  11,  21,  23,  16,   7,  -9,
                        -27, -11,   4,  13,  14,   4,  -5, -17,
                        -53, -34, -21, -11, -28, -14, -24, -43
                };

        kingAttackers = new int[]{0,81,52,44,10,0,81,52,44,10,0};

    }

    public int PsqM(Board b, Move m) {
        Piece p = b.getPiece(m.getFrom());
        if (p==Piece.WHITE_PAWN) {
            return mgPawnTableWhite[m.getTo().ordinal()] - mgPawnTableWhite[m.getFrom().ordinal()];
        }
        if (p==Piece.WHITE_KNIGHT) {
            return knightTable[m.getTo().ordinal()] - knightTable[m.getFrom().ordinal()];
        }

        if (p==Piece.WHITE_BISHOP) {
            return bishopTableWhite[m.getTo().ordinal()] - bishopTableBlack[m.getFrom().ordinal()];
        }

        if (p==Piece.WHITE_ROOK) {
            return rookTableWhite[m.getTo().ordinal()] - rookTableWhite[m.getFrom().ordinal()];
        }

        if (p==Piece.WHITE_QUEEN) {
            return queenTable[m.getTo().ordinal()] - queenTable[m.getFrom().ordinal()];
        }
        if (p==Piece.WHITE_KING) {
            return mgKingTableWhite[m.getTo().ordinal()] - mgKingTableWhite[m.getFrom().ordinal()];
        }
        if(p==Piece.BLACK_PAWN) {
            return mgPawnTableBlack[m.getTo().ordinal()] - mgPawnTableBlack[m.getFrom().ordinal()];
        }
        if (p==Piece.BLACK_KNIGHT) {
            return knightTable[m.getTo().ordinal()] - knightTable[m.getFrom().ordinal()];
        }

        if (p==Piece.BLACK_BISHOP) {
            return bishopTableBlack[m.getTo().ordinal()] - bishopTableBlack[m.getFrom().ordinal()];
        }

        if (p==Piece.BLACK_ROOK) {
            return rookTableBlack[m.getTo().ordinal()] - rookTableBlack[m.getFrom().ordinal()];
        }

        if (p==Piece.BLACK_QUEEN) {
            return queenTable[m.getTo().ordinal()] - queenTable[m.getFrom().ordinal()];
        }
        if (p==Piece.BLACK_KING) {
            return mgKingTableBlack[m.getTo().ordinal()] - mgKingTableBlack[m.getFrom().ordinal()];
        }

        return 0;
    }

    public int evaluate(Board board) {
        int score = 0;
        float whiteMat = 0;
        float blackMat = 0;

        int mgScoreWhite = 0;
        int egScoreWhite = 0;
        int mgScoreBlack = 0;
        int egScoreBlack = 0;

        int atkDefScoreWhite = 0;
        int atkDefScoreBlack = 0;

        int i = -1;
        for (Piece p : board.boardToArray()) {
            i++;

            //Attackers and defenders
            if (i < 64) {
                if (isKingSquare(board, i) == 1) {
                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.BLACK, PieceType.KNIGHT) != 0)
                        atkDefScoreBlack += 81;
                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.BLACK, PieceType.BISHOP) != 0)
                        atkDefScoreBlack += 52;
                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.BLACK, PieceType.QUEEN) != 0)
                        atkDefScoreBlack += 30;
                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.BLACK, PieceType.ROOK) != 0)
                        atkDefScoreBlack += 44;

                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.WHITE, PieceType.KNIGHT) != 0)
                        atkDefScoreWhite += 45;
                }
                if (isKingSquare(board, i) == -1) {
                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.WHITE, PieceType.KNIGHT) != 0)
                        atkDefScoreWhite += 81;
                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.WHITE, PieceType.BISHOP) != 0)
                        atkDefScoreWhite += 52;
                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.WHITE, PieceType.QUEEN) != 0)
                        atkDefScoreWhite += 30;
                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.WHITE, PieceType.ROOK) != 0)
                        atkDefScoreWhite += 44;

                    if (board.squareAttackedByPieceType(Square.squareAt(i), Side.BLACK, PieceType.KNIGHT) != 0)
                        atkDefScoreBlack += 45;

                }
            }
            //Space
            if (i < 64) {
                if (board.getPiece(Square.squareAt(i - 8)) == Piece.WHITE_PAWN) {
                    score += 2;
                }
                if (board.getPiece(Square.squareAt(i - 16)) == Piece.WHITE_PAWN) {
                    score += 2;
                }
                if (board.squareAttackedByPieceType(Square.squareAt(i), Side.WHITE, PieceType.PAWN) != 0) {
                    score += 1;
                }

                if (board.getPiece(Square.squareAt(i + 8)) == Piece.BLACK_PAWN) {
                    score -= 2;
                }
                if (board.getPiece(Square.squareAt(i + 16)) == Piece.BLACK_PAWN) {
                    score -= 2;
                }
                if (board.squareAttackedByPieceType(Square.squareAt(i), Side.BLACK, PieceType.PAWN) != 0) {
                    score -= 1;
                }
            }
            if (p == Piece.NONE)
                continue;

            if (p==Piece.WHITE_PAWN) {
                mgScoreWhite += mgPawn + mgPawnTableWhite[i];
                egScoreWhite += egPawn + egPawnTableWhite[i];
                //Pawn shield
                if (isKingSquare(board, i) == 1)
                    score += 10;

                continue;
            }

            if (p==Piece.WHITE_KNIGHT) {
                mgScoreWhite += mgKnight + knightTable[i];
                egScoreWhite += egKnight + knightTable[i];

                whiteMat += 3;

                continue;
            }

            if (p==Piece.WHITE_BISHOP) {
                mgScoreWhite += mgBishop + bishopTableWhite[i];
                egScoreWhite += egBishop + bishopTableWhite[i];

                whiteMat += 3;

                continue;
            }

            if (p==Piece.WHITE_ROOK) {

                mgScoreWhite += mgRook + rookTableWhite[i];
                egScoreWhite += egRook + rookTableWhite[i];

                whiteMat += 5;

                continue;
            }

            if (p==Piece.WHITE_QUEEN) {
                mgScoreWhite += mgQueen + queenTable[i];
                egScoreWhite += egQueen + queenTable[i];

                whiteMat += 9;

                continue;
            }

            if (p==Piece.WHITE_KING) {
                mgScoreWhite += mgKingTableWhite[i];
                egScoreWhite += egKingTableWhite[i];

                continue;
            }

            if (p==Piece.BLACK_PAWN) {
                mgScoreBlack += mgPawn + mgPawnTableBlack[i];
                egScoreBlack += egPawn + egPawnTableBlack[i];

                //Pawn shield
                if (isKingSquare(board, i) == -1)
                    score -= 10;

                continue;
            }

            if (p==Piece.BLACK_KNIGHT) {
                mgScoreBlack += mgKnight + knightTable[i];
                egScoreBlack += egKnight + knightTable[i];

                blackMat += 3;

                continue;
            }

            if (p==Piece.BLACK_BISHOP) {

                mgScoreBlack += mgBishop + bishopTableBlack[i];
                egScoreBlack += egBishop + bishopTableBlack[i];

                blackMat += 3;

                continue;
            }

            if (p==Piece.BLACK_ROOK) {

                mgScoreBlack += mgRook + rookTableBlack[i];
                egScoreBlack += egRook + rookTableBlack[i];

                blackMat += 5;

                continue;
            }

            if (p==Piece.BLACK_QUEEN) {
                mgScoreBlack += mgQueen + queenTable[i];
                egScoreBlack += egQueen + queenTable[i];

                blackMat += 9;

                continue;
            }

            if (p==Piece.BLACK_KING) {

                mgScoreBlack += mgKingTableBlack[i];
                egScoreBlack += egKingTableBlack[i];

            }

        }

        float whiteMgPercentage =  Math.max(0,((whiteMat - 10) / 21));
        float blackMgPercentage =  Math.max(0,((blackMat - 10) / 21));
        float whiteEgPercentage =  1 - Math.max(0,((whiteMat - 10) / 21));
        float blackEgPercentage =  1 - Math.max(0,((blackMat - 10) / 21));

        score -= (int) ((whiteMgPercentage * mgScoreBlack) + (whiteEgPercentage * egScoreBlack));
        score += (int) ((blackMgPercentage * mgScoreWhite) + (blackEgPercentage * egScoreWhite));

        score += (int) (atkDefScoreWhite * blackMgPercentage);
        score -= (int) (atkDefScoreBlack * whiteMgPercentage);


        if (board.getSideToMove() == Side.BLACK)
            score *= -1;

        return score;

    }

    int isKingSquare(Board b, int sq) {
        int ksW = b.getKingSquare(Side.WHITE).ordinal();
        int ksB = b.getKingSquare(Side.BLACK).ordinal();

        if (Math.abs(ksW - sq) == 1 || Math.abs(ksW - sq) == 8 || Math.abs(ksW - sq) == 7 || Math.abs(ksW - sq) == 9)
            return 1;

        if (Math.abs(ksB - sq) == 1 || Math.abs(ksB - sq) == 8 || Math.abs(ksB - sq) == 7 || Math.abs(ksB - sq) == 9)
            return -1;

        return 0;
    }

}
