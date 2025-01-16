package org.example.Engine;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;


public class Evaluation {

    //Piece values by phase
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

    //Piece square tables
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

    //Used for KQvsK, KRvK, KBNvK, Encourage engine to force king to edge/corners of board.
    int[] mateTableKing;

    //Piece pawn adjustment
    int[] knightAdj = { -20, -16, -12, -8, -4,  0,  4,  8, 12 };
    int[] rookAdj = { 15,  12,   9,  6,  3,  0, -3, -6, -9 };

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
                        -15,  -4,  -19, -54,   -40, -28,  44,  14,
                        1,   7,  -8, -64, -43, -16,   9,   8,
                        -14, -14, -22, -46, -44, -30, -15, -27,
                        -49,  -1, -27, -39, -46, -44, -33, -51,
                        -17, -20, -12, -27, -30, -25, -14, -36,
                        -9,  24,   2, -16, -20,   6,  22, -22,
                        29,  -1, -20,  -7,  -8,  -4, -38, -29,
                        -65,  23,  16, -15, -56, -34,   2,  13
                };

        mgKingTableBlack = new int[]
                {       -65,  23,  16, -15, -56, -34,   2,  13,
                        29,  -1, -20,  -7,  -8,  -4, -38, -29,
                        -9,  24,   2, -16, -20,   6,  22, -22,
                        -17, -20, -12, -27, -30, -25, -14, -36,
                        -49,  -1, -27, -39, -46, -44, -33, -51,
                        -14, -14, -22, -46, -44, -30, -15, -27,
                        1,   7,  -8, -64, -43, -16,   9,   8,
                        -15,  -4,  -19, -54, -40, -28,  44,  14,
                };

        egKingTableWhite = new int[]
                {
                        -53, -34, -21, -11, -28, -14, -24, -43,
                        -27, -11,   4,  13,  14,   4,  -5, -17,
                        -19,  -3,  11,  21,  23,  16,   7,  -9,
                        -18,  -4,  21,  24,  27,  23,   9, -11,
                        -8,  22,  24,  27,  26,  33,  26,   3,
                        10,  17,  23,  15,  20,  45,  44,  13,
                        -12,  17,  14,  17,  17,  38,  23,  11,
                        -74, -35, -18, -18, -11,  15,   4, -17
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

        mateTableKing = new int[]
                {       -1000, -500, -500, -500, -500,  -500, -500, -1000,
                         -500, -500, -300, -300, -300,  -300, -500,  -500,
                         -500, -300, -200, -200, -200,  -200, -300,  -500,
                         -500, -300, -200, -100, -100,  -200, -300,  -500,
                         -500, -300, -200, -100, -100,  -200, -300,  -500,
                         -500, -300, -200, -200, -200,  -200, -300,  -500,
                         -500, -500, -300, -300, -300,  -300, -500,  -500,
                        -1000, -500, -500, -500, -500,  -500, -500, -1000,
                };

    }

    public int PsqM(Piece p, Move m) {
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

    public boolean onlyPawns(Board b) {
        return    b.getBitboard(Piece.WHITE_QUEEN)
                + b.getBitboard(Piece.WHITE_ROOK)
                + b.getBitboard(Piece.WHITE_BISHOP)
                + b.getBitboard(Piece.WHITE_KNIGHT)
                + b.getBitboard(Piece.BLACK_QUEEN)
                + b.getBitboard(Piece.BLACK_ROOK)
                + b.getBitboard(Piece.BLACK_BISHOP)
                + b.getBitboard(Piece.BLACK_KNIGHT) == 0;

    }

    public int evaluate(Board board) {
        int score = 0;

        float whiteMgPercentage = whitePhase(board);
        float blackMgPercentage = blackPhase(board);
        float phase = (whiteMgPercentage + blackMgPercentage) / 2;

        //Returns draws
        if (winnable(board) == 0)
            return 0;

        int whitePawns = Long.bitCount(board.getBitboard(Piece.WHITE_PAWN));
        int blackPawns = Long.bitCount(board.getBitboard(Piece.BLACK_PAWN));

        int mgScoreWhite = 0;
        int egScoreWhite = 0;
        int mgScoreBlack = 0;
        int egScoreBlack = 0;

        //Mobility
        int mobilityWhiteMg = 0;
        int mobilityBlackMg = 0;

        int mobilityWhiteEg = 0;
        int mobilityBlackEg = 0;

        //Attack / Defense score
        long pieces = board.getBitboard();
        long piecesWhite = board.getBitboard(Side.WHITE);
        long piecesBlack = board.getBitboard(Side.BLACK);
        long kingRingWhite = Bitboard.getKingAttacks(board.getKingSquare(Side.WHITE), pieces);
        long kingRingBlack = Bitboard.getKingAttacks(board.getKingSquare(Side.BLACK), pieces);

        int pawnShieldWhite = 0;
        int pawnShieldBlack = 0;

        int weakSquaresWhite = 0;
        int weakSquaresBlack = 0;

        long whiteVison = 0;
        long blackVison = 0;

        long whitePawnVison = 0;
        long blackPawnVison = 0;
        long whiteKnightVison = 0;
        long blackKnightVison = 0;
        long whiteBishopVison = 0;
        long blackBishopVison = 0;
        long whiteRookVison = 0;
        long blackRookVison = 0;
        long whiteQueenVison = 0;
        long blackQueenVison = 0;

        int attackCountWhite = 0;
        int attackWeightWhite = 0;
        int attackSquaresWhite = 0;
        int attackCountBlack = 0;
        int attackWeightBlack = 0;
        int attackSquaresBlack = 0;
        int knightDefenderWhite = 0;
        int knightDefenderBlack = 0;

        for (int i = 0; i < 64; i++) {

            Square sq = Square.squareAt(i);
            Piece p = board.getPiece(sq);

            if (p == Piece.NONE)
                continue;

            if (p==Piece.WHITE_PAWN) {
                mgScoreWhite += mgPawn + mgPawnTableWhite[i];
                egScoreWhite += egPawn + egPawnTableWhite[i];

                //Pawn shield
                if ((kingRingWhite & sq.getBitboard()) == 0)
                    pawnShieldWhite++;

                //Space
                if (board.getPiece(Square.squareAt(i + 8)) != Piece.WHITE_PAWN) {
                    score += 2;
                    if (board.getPiece(Square.squareAt(i + 16)) != Piece.WHITE_PAWN) {
                        score += 2;
                    }
                }

                long attacks = Bitboard.getPawnAttacks(Side.WHITE, sq);

                whitePawnVison |= attacks;
                whiteVison |= attacks;

                continue;
            }

            if (p==Piece.WHITE_KNIGHT) {
                mgScoreWhite += mgKnight + knightTable[i] + knightAdj[whitePawns];
                egScoreWhite += egKnight + knightTable[i] + knightAdj[whitePawns];

                if (Square.squareAt(i).getRank() == Rank.RANK_1)
                    mgScoreWhite -= 25;

                long attacks = Bitboard.getKnightAttacks(sq, ~piecesWhite);
                long defense = Bitboard.getKnightAttacks(sq, 0);

                whiteVison |= defense;

                mobilityWhiteMg += (Long.bitCount(attacks) - 4) * 4;
                mobilityWhiteEg += (Long.bitCount(attacks) - 4) * 4;

                whiteKnightVison |= attacks;

                int atkSquares = Long.bitCount(attacks & kingRingBlack);
                if (atkSquares != 0) {
                    attackCountWhite++;
                    attackWeightWhite += 81;
                    attackSquaresWhite += atkSquares;
                }

                knightDefenderWhite +=  Long.bitCount(defense);

                continue;
            }

            if (p==Piece.WHITE_BISHOP) {
                mgScoreWhite += mgBishop + bishopTableWhite[i];
                egScoreWhite += egBishop + bishopTableWhite[i];

                if (Square.squareAt(i).getRank() == Rank.RANK_1)
                    mgScoreWhite -= 25;

                long attacks = Bitboard.getBishopAttacks(pieces, sq);
                mobilityWhiteMg += (Long.bitCount(attacks) - 7) * 3;
                mobilityWhiteEg += (Long.bitCount(attacks) - 7) * 3;

                whiteBishopVison |= attacks;
                whiteVison |= attacks;

                int atkSquares = Long.bitCount(attacks & kingRingBlack);
                if (atkSquares != 0) {
                    attackCountWhite++;
                    attackWeightWhite += 52;
                    attackSquaresWhite += atkSquares;
                }

                continue;
            }

            if (p==Piece.WHITE_ROOK) {

                mgScoreWhite += mgRook + rookTableWhite[i] + rookAdj[whitePawns];
                egScoreWhite += egRook + rookTableWhite[i] + rookAdj[whitePawns];

                long attacks = Bitboard.getRookAttacks(pieces, sq);
                mobilityWhiteMg += (Long.bitCount(attacks) - 7) * 2;
                mobilityWhiteEg += (Long.bitCount(attacks) - 7) * 4;

                whiteRookVison |= attacks;
                whiteVison |= attacks;

                int atkSquares = Long.bitCount(attacks & kingRingBlack);
                if (atkSquares != 0) {
                    attackCountWhite++;
                    attackWeightWhite += 44;
                    attackSquaresWhite += atkSquares;
                }

                continue;
            }

            if (p==Piece.WHITE_QUEEN) {
                mgScoreWhite += mgQueen + queenTable[i];
                egScoreWhite += egQueen + queenTable[i];

                long attacks = Bitboard.getQueenAttacks(pieces, sq);
                mobilityWhiteMg += (Long.bitCount(attacks) - 14);
                mobilityWhiteEg += (Long.bitCount(attacks) - 14);

                whiteQueenVison |= attacks;
                whiteVison |= attacks;

                int atkSquares = Long.bitCount(attacks & kingRingBlack);
                if (atkSquares != 0) {
                    attackCountWhite++;
                    attackWeightWhite += 30;
                    attackSquaresWhite += atkSquares;
                }

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
                if ((kingRingBlack & sq.getBitboard()) == 0)
                    pawnShieldBlack++;

                //Space
                if (board.getPiece(Square.squareAt(i - 8)) != Piece.BLACK_PAWN) {
                    score = 2;
                    if (board.getPiece(Square.squareAt(i - 16)) != Piece.BLACK_PAWN) {
                        score -= 2;
                    }
                }

                long attacks = Bitboard.getPawnAttacks(Side.BLACK, sq);
                blackPawnVison |= attacks;
                blackVison |= attacks;


                continue;
            }

            if (p==Piece.BLACK_KNIGHT) {
                mgScoreBlack += mgKnight + knightTable[i] + knightAdj[blackPawns];
                egScoreBlack += egKnight + knightTable[i] + knightAdj[blackPawns];

                if (Square.squareAt(i).getRank() == Rank.RANK_8)
                    mgScoreBlack -= 25;

                long attacks = Bitboard.getKnightAttacks(sq, ~piecesBlack);
                long defense = Bitboard.getKnightAttacks(sq, 0);

                blackVison |= defense;

                mobilityBlackMg += (Long.bitCount(attacks) - 4) * 4;
                mobilityBlackEg += (Long.bitCount(attacks) - 4) * 4;

                blackKnightVison |= attacks;

                int atkSquares = Long.bitCount(attacks & kingRingWhite);
                if (atkSquares != 0) {
                    attackCountBlack++;
                    attackWeightBlack += 81;
                    attackSquaresBlack += atkSquares;
                }

                knightDefenderBlack +=  Long.bitCount(defense);

                continue;
            }

            if (p==Piece.BLACK_BISHOP) {

                mgScoreBlack += mgBishop + bishopTableBlack[i];
                egScoreBlack += egBishop + bishopTableBlack[i];

                if (Square.squareAt(i).getRank() == Rank.RANK_8)
                    mgScoreBlack -= 25;

                long attacks = Bitboard.getBishopAttacks(pieces, sq);
                mobilityBlackMg += (Long.bitCount(attacks) - 7) * 3;
                mobilityBlackEg += (Long.bitCount(attacks) - 7) * 3;

                blackBishopVison |= attacks;
                blackVison |= attacks;

                int atkSquares = Long.bitCount(attacks & kingRingWhite);
                if (atkSquares != 0) {
                    attackCountBlack++;
                    attackWeightBlack += 52;
                    attackSquaresBlack += atkSquares;
                }

                continue;
            }

            if (p==Piece.BLACK_ROOK) {

                mgScoreBlack += mgRook + rookTableBlack[i] + rookAdj[blackPawns];
                egScoreBlack += egRook + rookTableBlack[i] + rookAdj[blackPawns];

                long attacks = Bitboard.getRookAttacks(pieces, sq);
                mobilityBlackMg += (Long.bitCount(attacks) - 7) * 2;
                mobilityBlackEg += (Long.bitCount(attacks) - 7) * 4;

                blackRookVison |= attacks;
                blackVison |= attacks;

                int atkSquares = Long.bitCount(attacks & kingRingWhite);
                if (atkSquares != 0) {
                    attackCountBlack++;
                    attackWeightBlack += 44;
                    attackSquaresBlack += atkSquares;
                }

                continue;
            }

            if (p==Piece.BLACK_QUEEN) {
                mgScoreBlack += mgQueen + queenTable[i];
                egScoreBlack += egQueen + queenTable[i];

                long attacks = Bitboard.getQueenAttacks(pieces, sq);
                mobilityBlackMg += (Long.bitCount(attacks) - 14);
                mobilityBlackEg += (Long.bitCount(attacks) - 14);

                blackQueenVison |= attacks;
                blackVison |= attacks;

                int atkSquares = Long.bitCount(attacks & kingRingWhite);
                if (atkSquares != 0) {
                    attackCountBlack++;
                    attackWeightBlack += 30;
                    attackSquaresBlack += atkSquares;
                }

                continue;
            }

            if (p==Piece.BLACK_KING) {

                mgScoreBlack += mgKingTableBlack[i];
                egScoreBlack += egKingTableBlack[i];

            }

        }

        //Mobility adjustment
        mgScoreWhite += mobilityWhiteMg;
        mgScoreBlack += mobilityBlackMg;

        egScoreWhite += mobilityWhiteEg;
        egScoreBlack += mobilityBlackEg;

        //Attack/ defense adjustment
        weakSquaresWhite = Long.bitCount((blackVison & kingRingWhite) & ~whiteVison);
        weakSquaresBlack = Long.bitCount((whiteVison & kingRingBlack) & ~blackVison);

        int attackScoreWhite = attackCountWhite > 1 ?
            (attackWeightWhite * attackCountWhite) +
                    (69 * attackSquaresWhite) +
                    (90 * weakSquaresBlack) -
                    (50 * pawnShieldBlack) -
                    (45 * knightDefenderBlack)
                : 0;

        int attackScoreBlack = attackCountBlack > 1 ?
                (attackWeightBlack * attackCountBlack) +
                (69 * attackSquaresBlack) +
                (90 * weakSquaresWhite) -
                (50 * pawnShieldWhite)  -
                (45 * knightDefenderWhite)
                :0;

        mgScoreWhite += attackScoreWhite;
        mgScoreBlack += attackScoreBlack;

        //Piece value and piece square
        score -= (int) ((whiteMgPercentage * mgScoreBlack) + ((1 - whiteMgPercentage) * egScoreBlack));
        score += (int) ((blackMgPercentage * mgScoreWhite) + ((1 - blackMgPercentage) * egScoreWhite));

        //Bishop pairs
        if (Long.bitCount(board.getBitboard(Piece.WHITE_BISHOP)) == 2)
            score += (int)(10 * phase + 17 * (1-phase));

        if (Long.bitCount(board.getBitboard(Piece.BLACK_BISHOP)) == 2)
            score -= (int)(10 * phase + 17 * (1-phase));

        //Knight pair
        if (Long.bitCount(board.getBitboard(Piece.WHITE_KNIGHT)) == 2)
            score -= (int)(10 * phase + 17 * (1-phase));

        if (Long.bitCount(board.getBitboard(Piece.BLACK_KNIGHT)) == 2)
            score += (int)(10 * phase + 17 * (1-phase));

        //rook pair
        if (Long.bitCount(board.getBitboard(Piece.WHITE_ROOK)) == 2)
            score -= (int)(12 * phase);

        if (Long.bitCount(board.getBitboard(Piece.BLACK_ROOK)) == 2)
            score += (int)(12 * phase);

        if (whiteMgPercentage == 0f && Long.bitCount(board.getBitboard(Piece.WHITE_QUEEN)) + Long.bitCount(board.getBitboard(Piece.WHITE_ROOK)) == 0 && Long.bitCount(board.getBitboard(Piece.BLACK_QUEEN)) + Long.bitCount(board.getBitboard(Piece.BLACK_ROOK)) > 0) {

            int whiteKing = 63 - Long.numberOfLeadingZeros(board.getBitboard(Piece.WHITE_KING));
            int blackKing = 63 - Long.numberOfLeadingZeros(board.getBitboard(Piece.BLACK_KING));

            score += mateTableKing[whiteKing];

            Square whiteSq = Square.squareAt(whiteKing);
            Square blackSq = Square.squareAt(blackKing);

            double distance = Math.pow((whiteSq.getRank().ordinal() - blackSq.getRank().ordinal()),2) + Math.pow((whiteSq.getFile().ordinal() - blackSq.getFile().ordinal()),2);

            score += (int)distance;

        }
        else if (blackMgPercentage == 0f && Long.bitCount(board.getBitboard(Piece.WHITE_QUEEN)) + Long.bitCount(board.getBitboard(Piece.WHITE_ROOK)) > 0 && Long.bitCount(board.getBitboard(Piece.BLACK_QUEEN)) + Long.bitCount(board.getBitboard(Piece.BLACK_ROOK)) == 0) {

            int whiteKing = 63 - Long.numberOfLeadingZeros(board.getBitboard(Piece.WHITE_KING));
            int blackKing = 63 - Long.numberOfLeadingZeros(board.getBitboard(Piece.BLACK_KING));

            score -= mateTableKing[blackKing];

            Square whiteSq = Square.squareAt(whiteKing);
            Square blackSq = Square.squareAt(blackKing);

            double distance = Math.pow((whiteSq.getRank().ordinal() - blackSq.getRank().ordinal()),2) + Math.pow((whiteSq.getFile().ordinal() - blackSq.getFile().ordinal()),2);

            score -= (int)distance;

        }

        if (whiteMgPercentage < 0.10f || blackMgPercentage < 0.10f) {
            //50 move draw reduction
            int movesBeforeDraw = 100 - board.getHalfMoveCounter();
            score = (movesBeforeDraw * score) / 100;
        }


        if (board.getSideToMove() == Side.BLACK)
            score *= -1;

        return score;

    }

    int winnable(Board b) {
        if (b.getBitboard(Piece.WHITE_PAWN) != 0 || b.getBitboard(Piece.BLACK_PAWN) != 0)
            return 1;

        if (b.getBitboard(Piece.WHITE_QUEEN) != 0 || b.getBitboard(Piece.BLACK_QUEEN) != 0)
            return 1;

        if (b.getBitboard(Piece.WHITE_ROOK) != 0 || b.getBitboard(Piece.BLACK_ROOK) != 0)
            return 1;

        long bbN = b.getBitboard(Piece.WHITE_KNIGHT);
        long bbn = b.getBitboard(Piece.BLACK_KNIGHT);

        long bbB = b.getBitboard(Piece.WHITE_BISHOP);
        long bbb = b.getBitboard(Piece.BLACK_BISHOP);

        if ((bbN != 0 && bbB != 0) || (bbn != 0 && bbb != 0))
            return 1;

        if (Long.bitCount(bbN) >= 3 || Long.bitCount(bbn) >= 3)
            return 1;

        if (Long.bitCount(bbB) >= 2 || Long.bitCount(bbb) >= 2)
            return 1;

        return 0;

    }

    public float whitePhase(Board b) {
        float whiteMat = (Long.bitCount(b.getBitboard(Piece.WHITE_QUEEN)) * 9) +
                (Long.bitCount(b.getBitboard(Piece.WHITE_ROOK)) * 5) +
                (Long.bitCount(b.getBitboard(Piece.WHITE_BISHOP)) * 3) +
                (Long.bitCount(b.getBitboard(Piece.WHITE_KNIGHT)) * 3);

        return Math.max(0,((whiteMat - 10) / 21));

    }

    public float blackPhase(Board b) {
        float blackMat = (Long.bitCount(b.getBitboard(Piece.BLACK_QUEEN)) * 9) +
                (Long.bitCount(b.getBitboard(Piece.BLACK_ROOK)) * 5) +
                (Long.bitCount(b.getBitboard(Piece.BLACK_BISHOP)) * 3) +
                (Long.bitCount(b.getBitboard(Piece.BLACK_KNIGHT)) * 3);

        return  Math.max(0,((blackMat - 10) / 21));

    }

}
