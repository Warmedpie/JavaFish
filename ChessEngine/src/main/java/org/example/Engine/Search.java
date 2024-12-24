package org.example.Engine;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Side;

import javax.naming.ldap.PagedResultsResponseControl;
import java.util.*;

public class Search {

    Evaluation evaluation = new Evaluation();
    static Board board = new Board();
    TranspositionTable TT = new TranspositionTable();


    List<Integer> orderValueScore = new ArrayList<>();

    //Every time a beta cut occurs, raise this number
    int[][] HistoryHeuristic = new int[64][64];

    //Every time alpha is raised, raise this number
    int[][] MiniHistory = new int[64][64];

    int[] killerMove = new int[64];

    int nodes = 0;
    int firstMoveBetaCuts = 0;
    int firstMoves = 0;

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
            killerMove[p] = 0;
            for (int s = 0; s < 64; s++) {
                HistoryHeuristic[p][s] = 0;
                MiniHistory[p][s] = 0;
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

        if (depth <= 2) {
            return negamax(alpha, beta, depth, plyDeep);
        }

        //Order Moves
        List<ScoredMove> orderedMoves = orderMoves(depth, null);

        int alphaOrig = alpha;

        Move bestMove = new Move(Square.A1,Square.A1);

        int i = 0;
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

            //Fail-hard beta cut-off
            if (i==0)
                firstMoves++;
            if (score >= beta) {

                if (!capture) {
                    HistoryHeuristic[move.getFrom().ordinal()][move.getTo().ordinal()] += depth*depth;

                    if (killerMove[depth] == 0)
                        killerMove[depth] = move.hashCode();
                }

                if (i == 0)
                    firstMoveBetaCuts ++;

                break;

            }

            i++;
        }

        if (ignore.isEmpty())
            insertTable(alpha, beta, alphaOrig, depth, bestMove);

        multipv[ignore.size()] = new ScoredMove(bestMove, alpha);

        return Math.min(alpha,beta);

    }
    public int PVS(int alpha, int beta, int depth, int plyDeep) {

        if (checkTimeOut())
            return -312312;

        nodes++;
        TranspositionEntry node = TT.get(board.getZobristKey());
        if (nodeTableBreak(node,depth,alpha,beta)) {
            TBhits++;
            return node.score;
        }

        int winLossDraw = mateScore(plyDeep);
        if (winLossDraw < 1)
            return winLossDraw;

        if (depth <= 2) {
            return negamax(alpha, beta, depth, plyDeep);
        }

        //Order Moves
        List<ScoredMove> orderedMoves = orderMoves(depth, node.move);

        int alphaOrig = alpha;

        Move bestMove = new Move(Square.A1,Square.A1);

        int i = 0;
        boolean inCheck = board.isKingAttacked();
        for (ScoredMove scoredMove : orderedMoves) {
            Move move = scoredMove.move;

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

            //Fail-hard beta cut-off
            if (i==0)
                firstMoves++;
            if (score >= beta) {

                if (!capture) {
                HistoryHeuristic[move.getFrom().ordinal()][move.getTo().ordinal()] += depth*depth;

                if (killerMove[depth] == 0)
                    killerMove[depth] = move.hashCode();
                }

                if (i == 0)
                    firstMoveBetaCuts ++;

                break;

            }

            i++;
        }

        insertTable(alpha, beta, alphaOrig, depth, bestMove);

        return Math.min(alpha,beta);

    }

    //At low depths, we use standard minimax with alpha beta to enhance ordering and accuracy of low depth nodes
    public int negamax(int alpha, int beta, int depth, int plyDeep) {

        if (checkTimeOut())
            return -312312;

        nodes++;
        TranspositionEntry node = TT.get(board.getZobristKey());
        if (nodeTableBreak(node,depth,alpha,beta)) {
            TBhits++;
            return node.score;
        }

        if (node.depth >= depth) {
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

                int value = staticEval + 125;
                if (value < beta) {
                    return Math.max(qSearch(alpha, beta, 5), value);
                }
            }
        }

        //Order Moves
        List<ScoredMove> orderedMoves = orderMoves(depth, node.move);

        Move bestMove = new Move(Square.A1,Square.A1);
        int alphaOrig = alpha;

        for (ScoredMove scoredMove : orderedMoves) {
            Move move = scoredMove.move;

            boolean capture = board.getPiece(move.getTo()) != Piece.NONE;

            board.doMove(move);

            int score = -negamax(-beta, -alpha, depth - 1, plyDeep + 1);

            board.undoMove();

            if (score > alpha) {
                alpha = score;
                bestMove = move;
            }

            //Fail-hard beta cut-off
            if (score >= beta) {

                if (!capture) {
                    if (killerMove[depth] == 0)
                        killerMove[depth] = move.hashCode();
                }

                break;

            }

        }

        insertTable(alpha, beta, alphaOrig, depth, bestMove);

        return Math.min(alpha,beta);
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

        TranspositionEntry te = new TranspositionEntry(alpha, depth, nodeType, bestMove);
        TT.insert(board.getZobristKey(), te);
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

        List<ScoredMove> orderedMoves = orderCapturesAndChecks();

        if (orderedMoves.isEmpty())
            return staticEval;

        for (ScoredMove scoredMove : orderedMoves) {
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
            }

            if( score >= beta )
                return beta;
        }

        return alpha;
    }

    public List<ScoredMove> orderMoves(int depth, Move best) {

        //Get Legal Moves
        List<Move> legalMoves = board.legalMoves();

        //Return this list
        List<ScoredMove> moves = new ArrayList<>();

        //Try all moves
        for (Move move : legalMoves) {
            int score;

            //Table PV move gets higher rating
            if (move != best) {

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
                        score += 10000 + to;
                    //Better value capture
                    else if (to > from) {
                        score += 10000 + (to - from);
                    }

                    //Equal value capture
                    else if (Math.abs(to - from) < 30) {
                        score += 5000 + to - from;
                    }

                    else
                        score += 2200 + (to - from);

                }
                else {

                    score += HistoryHeuristic[move.getFrom().ordinal()][move.getTo().ordinal()];
                    score += MiniHistory[move.getFrom().ordinal()][move.getTo().ordinal()];


                    if (multipv[0] != null && move == multipv[0].move)
                        score += 4000;
                    if (multipv[1] != null && move == multipv[1].move)
                        score += 3900;
                    if (multipv[2] != null && move == multipv[2].move)
                        score += 3800;
                    if (multipv[3] != null && move == multipv[3].move)
                        score += 3700;
                    if (multipv[4] != null && move == multipv[4].move)
                        score += 3600;

                    if (move.hashCode() == killerMove[depth])
                        score += 3200;

                    else if (move.hashCode() == killerMove[depth + 1]) {
                        score += 3100;
                    }

                    else if (depth > 1 && move.hashCode() == killerMove[depth - 1]) {
                        score += 3095;
                    }
                }

            }
            else {
                score = 9999999;
            }

            ScoredMove m = new ScoredMove(move, score);
            moves.add(m);

        }

        if (moves.size() > 1) {
            Collections.sort(moves);
        }

        return moves;

    }

    public List<ScoredMove> orderCapturesAndChecks() {

        //Get Legal Moves
        List<Move> legalMoves = board.legalMoves();

        //Return this list
        List<ScoredMove> moves = new ArrayList<>();

        //Try all moves
        for (Move move : legalMoves) {

            boolean aggressiveOrInCheck = board.getPiece(move.getTo()) != Piece.NONE;

            if (board.isKingAttacked())
                aggressiveOrInCheck = true;

            board.doMove(move);

            if (board.isKingAttacked())
                aggressiveOrInCheck = true;

            board.undoMove();

            if (aggressiveOrInCheck) {
                int score = 0;

                int to = board.getPiece(move.getTo()).ordinal();
                int from = board.getPiece(move.getFrom()).ordinal();

                Side defender = Side.WHITE;

                if (board.getSideToMove() == Side.WHITE)
                    defender = Side.BLACK;

                //Better value capture
                if (to < 11 && from < 11) {
                    //hanging piece
                    if(board.squareAttackedBy(move.getTo(), defender) == 0)
                        score += 1000 + orderValueScore.get(to);
                    //Better value capture
                    else if (orderValueScore.get(to) > orderValueScore.get(from)) {
                        score += 1000 + orderValueScore.get(to) - orderValueScore.get(from);
                    }
                    //Equal Captures
                    else if (Math.abs(orderValueScore.get(to) - orderValueScore.get(from)) < 30) {
                        score += 500 + to - from;
                    }
                    //Bad captures
                    else {
                        score += orderValueScore.get(to) - orderValueScore.get(from);
                    }
                }
                else {
                    score += evaluation.PsqM(board.getPiece(move.getFrom()),move);
                }

                ScoredMove m = new ScoredMove(move, score);
                moves.add(m);
            }
        }

        if (moves.size() > 1) {
            Collections.sort(moves);
        }

        return moves;

    }

    //Search Helper functions
    public int mateScore(int depth) {

        if (board.isRepetition())
            return 0;

        if (board.legalMoves().isEmpty()) {

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
        if (node.depth >= depth) {

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
    }

    //Display functions
    ScoredMove[] multipv = new ScoredMove[5];
    public List<Move> getPV() {

        List<Move> toReturn = new ArrayList<>();

        Board b = new Board();
        b.loadFromFen(board.getFen());
        int i = 0;
        while (TT.get(b.getZobristKey()).depth > 0) {
            if (TT.get(b.getZobristKey()).move.getTo() != TT.get(b.getZobristKey()).move.getFrom())
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

    public List<Move> getPV(int n) {

        List<Move> toReturn = new ArrayList<>();
        toReturn.add(multipv[n].move);

        Board b = new Board();
        b.loadFromFen(board.getFen());
        b.doMove(multipv[n].move);

        int i = 0;
        while (TT.get(b.getZobristKey()).depth > 0) {
            if (TT.get(b.getZobristKey()).move.getTo() != TT.get(b.getZobristKey()).move.getFrom())
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

}
