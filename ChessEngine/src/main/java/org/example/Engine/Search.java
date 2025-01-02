package org.example.Engine;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

import java.util.*;

import static com.github.bhlangonijr.chesslib.move.MoveGenerator.*;

public class Search {

    Evaluation evaluation = new Evaluation();
    static Board board = new Board();
    TranspositionTable TT = new TranspositionTable();


    List<Integer> orderValueScore = new ArrayList<>();

    //Every time a beta cut occurs, raise this number
    int[][] HistoryHeuristic = new int[64][64];

    //Every time alpha is raised, raise this number
    int[][] MiniHistory = new int[64][64];

    Move[] killerMove = new Move[64];
    Move[][] counterMove = new Move[64][64];

    int nodes = 0;
    int firstMoveBetaCuts = 0;
    int cutMoves = 0;

    public Search() {
        orderValueScore.add(71);
        orderValueScore.add(337);
        orderValueScore.add(365);
        orderValueScore.add(456);
        orderValueScore.add(905);
        orderValueScore.add(0);

        orderValueScore.add(71);
        orderValueScore.add(337);
        orderValueScore.add(365);
        orderValueScore.add(456);
        orderValueScore.add(905);
        orderValueScore.add(0);
        orderValueScore.add(0);

        for (int p = 0; p < 64; p++) {
            killerMove[p] = null;
            for (int s = 0; s < 64; s++) {
                HistoryHeuristic[p][s] = 0;
                MiniHistory[p][s] = 0;
                counterMove[p][s] = null;
            }
        }

    }
    public int PVSIgnore(int alpha, int beta, int depth, int plyDeep, List<Move> ignore) {

        if (checkTimeOut())
            return -312312;

        nodes++;

        int winLossDraw = mateScore(plyDeep);
        if (winLossDraw < 1)
            return winLossDraw;

        //Order Moves
        Move TableMove = null;

        int mq = 0;
        while (multipv[mq] == null || ignore.contains(multipv[mq].move)) {
            mq++;

            if (mq == 5)
                break;
        }
        if (mq < 5 && multipv[mq] != null && !ignore.contains(multipv[mq].move)) {
            if (board.getPiece(multipv[mq].move.getFrom()) != Piece.NONE && board.isMoveLegal(multipv[mq].move, true))
                TableMove = multipv[mq].move;
        }

        Move bestMove = null;
        int alphaOrig = alpha;

        int i = 0;
        if (TableMove != null) {
            i++;
            board.doMove(TableMove);
            int score = -PVS(-beta,-alpha,depth - 1, plyDeep + 1);
            board.undoMove();

            if (score > alpha) {
                alpha = score;
                bestMove = TableMove;
            }

        }

        List<ScoredMove> orderedMoves = orderAllMoves(depth, TableMove);
        boolean inCheck = board.isKingAttacked();
        for (ScoredMove scoredMove : orderedMoves) {
            Move move = scoredMove.move;

            if (plyDeep == 0 && ignore.contains(move)) {
                continue;
            }

            boolean capture = board.getPiece(move.getTo()) != Piece.NONE;

            board.doMove(move);

            int score;
            //Late move reductions
            //Do not reduce when in check
            //do not reduce moves that are checks
            int LMR = 0;
            if (!inCheck && !board.isKingAttacked()) {
                LMR = (int)(0.7844 + Math.log(depth) * Math.log(i) / 2.4696);
            }

            //Search with the full window on first move
            if (i == 0) {
                score = -PVS(-beta,-alpha,depth - 1, plyDeep + 1);
            }
            else {
                //Search with a null window until alpha improves
                score = -PVS(-alpha-1,-alpha,depth-1 - LMR, plyDeep + 1);

                //re-search required
                if (score > alpha && beta - alpha > 1) {
                    score = -PVS(-beta,-alpha,depth-1, plyDeep + 1);
                }

            }
            board.undoMove();

            if (score > alpha) {
                alpha = score;
                bestMove = move;

                MiniHistory[move.getFrom().ordinal()][move.getTo().ordinal()] += depth + i;

            }

            if (score >= beta) {

                if (!capture) {
                    HistoryHeuristic[move.getFrom().ordinal()][move.getTo().ordinal()] += depth*depth;

                    if (killerMove[depth] == null)
                        killerMove[depth] = move;
                }

                break;

            }

            i++;
        }

        if (alpha >= beta) {
            if (i==0)
                firstMoveBetaCuts++;
            cutMoves++;
        }

        if (ignore.isEmpty())
            insertTable(alpha, beta, alphaOrig, depth, bestMove);

        multipv[ignore.size()] = new ScoredMove(bestMove, alpha);

        return Math.min(alpha,beta);

    }
    public int PVS(int alpha, int beta, int depth, int plyDeep) {

        if (checkTimeOut())
            return -312312;

        if (board.isRepetition())
            return 0;

        nodes++;
        TranspositionEntry node = TT.get(board.getZobristKey());
        if (nodeTableBreak(node,depth,alpha,beta)) {
            TBhits++;
            return node.score;
        }

        int winLossDraw = mateScore(plyDeep);
        if (winLossDraw < 1)
            return winLossDraw;

        if (depth <= 3) {
            return negamax(alpha, beta, depth, plyDeep);
        }

        boolean inCheck = board.isKingAttacked();

        //NULL MOVE PRUNE
        //DO NOT PRUNE IF IN CHECK
        //ONLY PRUNE IN NULL WINDOWS
        if (depth > 4 && Math.abs(alpha-beta) == 1 && !inCheck && !evaluation.onlyPawns(board)) {
            int staticEval = evaluation.evaluate(board);
            //DO NOT NULL PRUNE DRAWS, ONLY NULL PRUNE WHEN STATIC EVAL IS GREATER THAN OR EQUAL TO BETA
            if (staticEval != 0 && staticEval >= beta) {
                int R = depth > 6 ? 4 : 3;

                board.doNullMove();
                int s = -PVS(-beta, -beta + 1, depth - R - 1, 0);
                board.undoMove();

                if (s >= beta)
                    return s;
            }
        }

        Move TableMove = null;

        if (node != null) {
            TableMove = node.move;
        }

        int score;
        Move bestMove = null;
        int alphaOrig = alpha;

        int i = 0;

        //Test the table move
        if (TableMove != null) {
            i++;
            board.doMove(TableMove);
            score = -PVS(-beta,-alpha,depth - 1, plyDeep + 1);
            board.undoMove();

            if (score > alpha) {
                alpha = score;
                bestMove = TableMove;
            }

        }

        if (alpha < beta) {
            //Order all moves
            List<ScoredMove> orderedMoves = orderAllMoves(depth, TableMove);

            for (ScoredMove scoredMove : orderedMoves) {
                Move move = scoredMove.move;

                boolean capture = board.getPiece(move.getTo()) != Piece.NONE;

                board.doMove(move);

                //Late move reductions
                //Do not reduce when in check
                //do not reduce moves that are checks
                int LMR = 0;
                if (!inCheck && !board.isKingAttacked()) {
                    LMR = (int) (0.7844 + Math.log(depth) * Math.log(i) / 2.4696);
                }

                //Search with the full window on first move
                if (i == 0) {
                    score = -PVS(-beta, -alpha, depth - 1, plyDeep + 1);
                } else {
                    //Search with a null window until alpha improves
                    score = -PVS(-alpha - 1, -alpha, depth - 1 - LMR, plyDeep + 1);

                    //re-search required
                    if (score > alpha && beta - alpha > 1) {
                        score = -PVS(-beta, -alpha, depth - 1, plyDeep + 1);
                    }

                }
                board.undoMove();

                if (score > alpha) {
                    alpha = score;
                    bestMove = move;

                    MiniHistory[move.getFrom().ordinal()][move.getTo().ordinal()] += depth + i;

                }

                if (score >= beta) {

                    if (!capture) {
                        HistoryHeuristic[move.getFrom().ordinal()][move.getTo().ordinal()] += depth * depth;

                        if (killerMove[depth] == null)
                            killerMove[depth] = move;
                    }

                    break;

                }

                i++;
            }

        }

        if (alpha >= beta) {
            if (i==0)
                firstMoveBetaCuts++;
            cutMoves++;
        }

        Move lastMove = getLastMove();

        if (lastMove.getFrom().ordinal() < 64 && lastMove.getTo().ordinal() < 64)
            counterMove[lastMove.getFrom().ordinal()][lastMove.getTo().ordinal()] = bestMove;

        insertTable(alpha, beta, alphaOrig, depth, bestMove);

        return Math.min(alpha,beta);

    }

    //At low depths, we use standard minimax with alpha beta to enhance ordering and accuracy of low depth nodes
    public int negamax(int alpha, int beta, int depth, int plyDeep) {

        if (checkTimeOut())
            return -312312;

        if (board.isRepetition())
            return 0;

        nodes++;
        TranspositionEntry node = TT.get(board.getZobristKey());
        if (nodeTableBreak(node,depth,alpha,beta)) {
            TBhits++;
            return node.score;
        }

        if (node != null && node.depth >= depth) {
            //this is the PV node.
            if (node.nodeType == 0) {
                return node.score;
            }

            //Fail-high (Cut-node)
            if (node.nodeType == 1) {
                if (node.score >= beta)
                    return node.score;
            }

            //Fail-low (All-Node)
            if (node.nodeType == -1) {
                if (node.score <= alpha)
                    return node.score;
            }
        }

        int winLossDraw = mateScore(plyDeep);
        if (winLossDraw < 1)
            return winLossDraw;

        if (depth <= 0) {
            return qSearch(alpha, beta, 7);
        }

        //Razoring
        //if our position is really poor, we do not need to investigate at low depth nodes
        //We do not razor if we are in check.
        if (!board.isKingAttacked()) {
            if (depth == 1) {
                int staticEval = evaluation.evaluate(board);

                int value = staticEval + (82*2);
                if (value < alpha) {
                    return Math.max(qSearch(alpha, beta, 5), value);
                }
            }
            if (depth == 2) {
                int staticEval = evaluation.evaluate(board);

                int value = staticEval + (82*5);
                if (value < alpha) {
                    return Math.max(qSearch(alpha, beta, 5), value);
                }
            }
        }

        Move TableMove = null;
        if (node != null) {
            TableMove = node.move;
        }

        Move bestMove = null;
        int alphaOrig = alpha;

        if (TableMove != null) {
            board.doMove(TableMove);
            int score = -negamax(-beta, -alpha, depth - 1, plyDeep + 1);
            board.undoMove();

            if (score > alpha) {
                alpha = score;
                bestMove = TableMove;
            }
        }

        if (alpha < beta) {
            List<ScoredMove> orderedCaptures = orderCaptures(TableMove);

            //Winning Captures
            for (ScoredMove scoredMove : orderedCaptures) {

                if (scoredMove.score < 10000)
                    continue;

                Move move = scoredMove.move;

                board.doMove(move);

                int score = -negamax(-beta, -alpha, depth - 1, plyDeep + 1);

                board.undoMove();

                if (score > alpha) {
                    alpha = score;
                    bestMove = move;

                    //Fail-hard beta cut-off
                    if (alpha >= beta) {
                        break;
                    }

                }

            }

            //Equal Captures
            if (alpha < beta) {
                for (ScoredMove scoredMove : orderedCaptures) {

                    if (scoredMove.score >= 10000 || scoredMove.score < 0)
                        continue;

                    Move move = scoredMove.move;

                    board.doMove(move);

                    int score = -negamax(-beta, -alpha, depth - 1, plyDeep + 1);

                    board.undoMove();

                    if (score > alpha) {
                        alpha = score;
                        bestMove = move;

                        //Fail-hard beta cut-off
                        if (alpha >= beta) {
                            break;
                        }

                    }

                }
            }

            //Quiet Moves
            if (alpha < beta) {
                //Order Moves
                List<ScoredMove> orderedMoves = orderQuiet(depth, TableMove);

                for (ScoredMove scoredMove : orderedMoves) {
                    Move move = scoredMove.move;

                    board.doMove(move);

                    int score = -negamax(-beta, -alpha, depth - 1, plyDeep + 1);

                    board.undoMove();

                    if (score > alpha) {
                        alpha = score;
                        bestMove = move;

                        //Fail-hard beta cut-off
                        if (alpha >= beta) {

                            if (killerMove[depth] == null)
                                killerMove[depth] = move;

                            break;

                        }

                    }

                }
            }

            //Losing Captures
            if (alpha < beta) {
                for (ScoredMove scoredMove : orderedCaptures) {

                    if (scoredMove.score >= 0)
                        continue;

                    Move move = scoredMove.move;

                    board.doMove(move);

                    int score = -negamax(-beta, -alpha, depth - 1, plyDeep + 1);

                    board.undoMove();

                    if (score > alpha) {
                        alpha = score;
                        bestMove = move;

                        //Fail-hard beta cut-off
                        if (alpha >= beta) {
                            break;
                        }

                    }

                }
            }

        }

        insertTable(alpha, beta, alphaOrig, depth, bestMove);

        return Math.min(alpha,beta);
    }

    public int qSearch(int alpha, int beta, int depth) {

        if (checkTimeOut())
            return -312312;

        nodes++;
        int staticEval = evaluation.evaluate(board);
        if (depth <= 0) {
            return staticEval;
        }

        if (staticEval >= beta)
            return beta;

        if (alpha - 1125 > staticEval)
            return alpha;

        if (alpha < staticEval)
            alpha = staticEval;

        List<ScoredMove> orderedCaptures = orderCaptures(null);

        for (ScoredMove scoredMove : orderedCaptures) {
            Move m = scoredMove.move;

            int to = orderValueScore.get(board.getPiece(m.getTo()).ordinal());
            int from = orderValueScore.get(board.getPiece(m.getFrom()).ordinal());

            if (to + staticEval + 200 < alpha )
                continue;

            Side defender = Side.WHITE;

            if (board.getSideToMove() == Side.WHITE)
                defender = Side.BLACK;

            if(board.squareAttackedBy(m.getTo(), defender) != 0 && to + 200 < from)
                continue;

            board.doMove(m);
            int score = -qSearch(-beta, -alpha, depth - 1);
            board.undoMove();

            if( score > alpha ) {
                alpha = score;

                if( alpha >= beta )
                    return beta;

            }

        }

        if (alpha < beta) {
            List<ScoredMove> orderedChecks = orderChecks();

            for (ScoredMove scoredMove : orderedChecks) {
                Move m = scoredMove.move;

                board.doMove(m);
                int score = -qSearch(-beta, -alpha, depth - 1);
                board.undoMove();

                if( score > alpha ) {
                    alpha = score;

                    if( alpha >= beta )
                        return beta;

                }

            }

        }

        return alpha;
    }

    void insertTable(int alpha, int beta, int alphaOrig, int depth, Move bestMove) {
        int nodeType = 0;

        //Fail-high (Cut-node)
        if (alpha >= beta) {
            nodeType = 1;
        }
        //Fail Low (All-node)
        else if (alpha == alphaOrig) {
            nodeType = -1;
        }

        long hash = board.getZobristKey();

        TranspositionEntry te = new TranspositionEntry(alpha, depth, nodeType, bestMove, hash);
        TT.insert(hash, te);
    }

    public List<ScoredMove> orderChecks() {

        //Get Legal Moves
        List<Move> legalMoves = generatePseudoLegalQuiet();

        //Return this list
        List<ScoredMove> moves = new ArrayList<>();

        //Try all moves
        for (Move move : legalMoves) {

            if (!board.isMoveLegal(move, false))
                continue;

            boolean checkOrInCheck = board.isKingAttacked();

            board.doMove(move);

            if (board.isKingAttacked())
                checkOrInCheck = true;

            board.undoMove();

            if (!checkOrInCheck)
                continue;

            if (board.getPiece(move.getTo()) != Piece.NONE)
                continue;

            int score = evaluation.PsqM(board.getPiece(move.getFrom()),move);

            ScoredMove m = new ScoredMove(move, score);
            moves.add(m);
        }

        if (moves.size() > 1) {
            Collections.sort(moves);
        }

        return moves;

    }

    public List<ScoredMove> orderAllMoves(int depth, Move best) {

        //Get Legal Moves
        List<Move> legalMoves = board.legalMoves();

        //Return this list
        List<ScoredMove> moves = new ArrayList<>();

        //Try all moves
        for (Move move : legalMoves) {
            int score;

            //Table PV move already tested
            if (move == best)
                continue;

            Move lastMove = getLastMove();
            if (lastMove != null) {
                if (lastMove.getFrom().ordinal() < 64 && lastMove.getTo().ordinal() < 64)
                    if (counterMove[lastMove.getFrom().ordinal()][lastMove.getTo().ordinal()] == move) {
                        score = 999999;
                        ScoredMove m = new ScoredMove(move, score);
                        moves.add(m);

                        continue;
                    }
            }

            //Piece square table score
            score = evaluation.PsqM(board.getPiece(move.getFrom()),move);

            //Captures
            if (board.getPiece(move.getTo()) != Piece.NONE) {
                int to = orderValueScore.get(board.getPiece(move.getTo()).ordinal());
                int from = orderValueScore.get(board.getPiece(move.getFrom()).ordinal());

                Side defender = Side.WHITE;

                if (board.getSideToMove() == Side.WHITE)
                    defender = Side.BLACK;

                //Hanging piece
                if(board.squareAttackedBy(move.getTo(), defender) == 0)
                    score += 20000 + to;
                    //Better value capture
                else if (to > from) {
                    score += 20000 + (to - from);
                }

                //Equal value capture
                else if (Math.abs(to - from) < 30) {
                    score += 5000 + to - from;
                }

                else
                    score += -2000 + (to - from);

            }
            else {

                score += HistoryHeuristic[move.getFrom().ordinal()][move.getTo().ordinal()];
                score += MiniHistory[move.getFrom().ordinal()][move.getTo().ordinal()];

                if (multipv[0] != null && move == multipv[0].move)
                    score += 7000;
                if (multipv[1] != null && move == multipv[1].move)
                    score += 6900;
                if (multipv[2] != null && move == multipv[2].move)
                    score += 6800;
                if (multipv[3] != null && move == multipv[3].move)
                    score += 6700;
                if (multipv[4] != null && move == multipv[4].move)
                    score += 6600;

                if (move == killerMove[depth])
                    score += 3200;

                else if (move == killerMove[depth + 1]) {
                    score += 3100;
                }

                else if (depth > 1 && move == killerMove[depth - 1]) {
                    score += 3095;
                }
            }

            ScoredMove m = new ScoredMove(move, score);
            moves.add(m);

        }

        if (moves.size() > 1) {
            Collections.sort(moves);
        }

        return moves;

    }

    public List<ScoredMove> orderCaptures(Move best) {

        //Get Legal Moves
        List<Move> legalMoves = MoveGenerator.generatePseudoLegalCaptures(board);

        //Return this list
        List<ScoredMove> moves = new ArrayList<>();

        //Try all moves
        for (Move move : legalMoves) {


            if (!board.isMoveLegal(move, false))
                continue;

            int score;

            //Table PV move already tested
            if (move == best)
                continue;

            //Piece square table score
            score = evaluation.PsqM(board.getPiece(move.getFrom()),move);

            int to = orderValueScore.get(board.getPiece(move.getTo()).ordinal());
            int from = orderValueScore.get(board.getPiece(move.getFrom()).ordinal());

            Side defender = Side.WHITE;

            if (board.getSideToMove() == Side.WHITE)
                defender = Side.BLACK;

            //Hanging piece
            if(board.squareAttackedBy(move.getTo(), defender) == 0)
                score += 20000 + to;
                //Better value capture
            else if (to > from) {
                score += 20000 + (to - from);
            }

            //Equal value capture
            else if (Math.abs(to - from) < 30) {
                score += 5000 + to - from;
            }

            else
                score += (to - from);

            ScoredMove m = new ScoredMove(move, score);
            moves.add(m);

        }


        if (moves.size() > 1) {
            Collections.sort(moves);
        }

        return moves;
    }

    public List<ScoredMove> orderQuiet(int depth, Move best) {

        //Get Legal Moves
        List<Move> legalMoves = generatePseudoLegalQuiet();

        //Return this list
        List<ScoredMove> moves = new ArrayList<>();

        //Try all moves
        for (Move move : legalMoves) {

            if (!board.isMoveLegal(move, false))
                continue;

            int score = 0;

            //Table PV move already tested
            if (move == best)
                continue;

            Move lastMove = getLastMove();
            if (lastMove != null) {
                if (lastMove.getFrom().ordinal() < 64 && lastMove.getTo().ordinal() < 64)
                    if (counterMove[lastMove.getFrom().ordinal()][lastMove.getTo().ordinal()] == move) {
                        score = 999999;
                        ScoredMove m = new ScoredMove(move, score);
                        moves.add(m);

                        continue;
                    }
            }

            //Piece square table score
            score = evaluation.PsqM(board.getPiece(move.getFrom()),move);

            score += HistoryHeuristic[move.getFrom().ordinal()][move.getTo().ordinal()];
            score += MiniHistory[move.getFrom().ordinal()][move.getTo().ordinal()];

            if (multipv[0] != null && move == multipv[0].move)
                score += 7000;
            if (multipv[1] != null && move == multipv[1].move)
                score += 6900;
            if (multipv[2] != null && move == multipv[2].move)
                score += 6800;
            if (multipv[3] != null && move == multipv[3].move)
                score += 6700;
            if (multipv[4] != null && move == multipv[4].move)
                score += 6600;

            if (move == killerMove[depth])
                score += 3200;

            else if (move == killerMove[depth + 1]) {
                score += 3100;
            }

            else if (depth > 1 && move == killerMove[depth - 1]) {
                score += 3095;
            }

            ScoredMove m = new ScoredMove(move, score);
            moves.add(m);

        }

        if (moves.size() > 1) {
            Collections.sort(moves);
        }

        return moves;

    }

    //Search Helper functions
    public int mateScore(int depth) {

        if (!hasLegalMoves()) {

            if (board.isKingAttacked()) {
                return -999999 + depth;
            }

            return 0;
        }

        return 2;
    }

    boolean checkTimeOut() {
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.

        return duration / 1000000 > time;
    }

    boolean nodeTableBreak(TranspositionEntry node, int depth, int alpha, int beta) {

        if (node != null &&node.depth >= depth) {

            //this is the PV node.
            if (node.nodeType == 0) {
                return true;
            }
            //Fail-high (Cut-node)
            if (node.nodeType == 1) {
                return node.score >= beta;
            }
            //Fail-low (All-Node)
            if (node.nodeType == -1) {
                return node.score <= alpha;
            }
        }

        return false;
    }

    long startTime;
    public static int time = 0;

    public void setup(Board b, int t) {
        time = t;
        board = b;

        startTime = System.nanoTime();

    }

    public void configureStats(int t) {
        time = t;
        this.TBhits = 0;
        nodes = 0;
        TT.clear();

        this.cutMoves = 0;
        this.firstMoveBetaCuts = 0;
    }

    public float firstMoveCuts() {
        return (float)this.firstMoveBetaCuts / (float)this.cutMoves;
    }

    //Display functions
    ScoredMove[] multipv = new ScoredMove[5];

    public List<Move> getPV(int n) {

        List<Move> toReturn = new ArrayList<>();
        toReturn.add(multipv[n].move);

        Board b = new Board();
        b.loadFromFen(board.getFen());
        b.doMove(multipv[n].move);

        int i = 0;
        while (TT.get(b.getZobristKey()) != null) {

            if (TT.get(b.getZobristKey()).move == null)
                break;

            toReturn.add(TT.get(b.getZobristKey()).move);

            if (b.getPiece(TT.get(b.getZobristKey()).move.getFrom()) != Piece.NONE)
                b.doMove(TT.get(b.getZobristKey()).move);
            else
                break;
            i++;

            if (i > 20)
                return toReturn;
        }

        return toReturn;
    }

    public int getNodes() {
        return nodes;
    }

    public int getnps() {

        long endTime = System.nanoTime();
        long duration = Math.max(1,(endTime - startTime));

        return 1000 * (int) (getNodes() / (((duration + 1) / 1000000)+1));
    }

    public int getTime() {
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        return (int)(duration / 1000000);
    }

    int TBhits = 0;
    public int getTBhits() {
        return TBhits;
    }

    public Move getLastMove() {
        if (board.getBackup().isEmpty())
            return null;

        MoveBackup b = (MoveBackup) board.getBackup().getLast();

        if (b != null) {
            return b.getMove();
        }

        return null;
    }

    static List<Move> generatePseudoLegalQuiet() {
        List<Move> moves = new LinkedList();

        long quietBB = ~board.getBitboard(board.getSideToMove()) & ~board.getBitboard(board.getSideToMove().flip());

        generatePawnMoves(board, moves);
        generateKnightMoves(board, moves, quietBB);
        generateBishopMoves(board, moves, quietBB);
        generateRookMoves(board, moves, quietBB);
        generateQueenMoves(board, moves, quietBB);
        generateKingMoves(board, moves, quietBB);
        generateCastleMoves(board, moves);

        return moves;
    }

    boolean hasLegalMoves() {
        List<Move> moves = new LinkedList();

        int i = 0;

        generateKingMoves(board, moves);

        while (i < moves.size()) {
            if (board.isMoveLegal(moves.get(i), false))
                return true;

            i++;
        }


        generateKnightMoves(board, moves);

        while (i < moves.size()) {
            if (board.isMoveLegal(moves.get(i), false))
                return true;

            i++;
        }

        generatePawnMoves(board, moves);

        while (i < moves.size()) {
            if (board.isMoveLegal(moves.get(i), false))
                return true;

            i++;
        }

        generateBishopMoves(board, moves);

        while (i < moves.size()) {
            if (board.isMoveLegal(moves.get(i), false))
                return true;

            i++;
        }

        generateRookMoves(board, moves);

        for (int q = i; i < moves.size(); i++) {
            if (board.isMoveLegal(moves.get(i), false))
                return true;
        }

        generateQueenMoves(board, moves);

        while (i < moves.size()) {
            if (board.isMoveLegal(moves.get(i), false))
                return true;

            i++;
        }

        generatePawnCaptures(board, moves);

        while (i < moves.size()) {
            if (board.isMoveLegal(moves.get(i), false))
                return true;

            i++;
        }

        generateCastleMoves(board, moves);

        while (i < moves.size()) {
            if (board.isMoveLegal(moves.get(i), false))
                return true;

            i++;
        }

        return false;
    }

}
