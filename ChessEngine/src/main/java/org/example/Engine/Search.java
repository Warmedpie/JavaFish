package org.example.Engine;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Side;

import java.util.*;

public class Search {

    Evaluation evaluation = new Evaluation();
    Board board = new Board();
    TranspositionTable TT = new TranspositionTable();

    Book openingBook = new Book();


    List<Integer> orderValueScore = new ArrayList<>();

    int nodes = 0;

    public Search() {
        orderValueScore.add(71);
        orderValueScore.add(279);
        orderValueScore.add(280);
        orderValueScore.add(456);
        orderValueScore.add(905);
        orderValueScore.add(0);

        orderValueScore.add(71);
        orderValueScore.add(279);
        orderValueScore.add(280);
        orderValueScore.add(456);
        orderValueScore.add(905);
        orderValueScore.add(0);

    }

    public int PVS(int alpha, int beta, int depth, int plyDeep) {

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.

        if (duration / 1000000 > time) {
            return -312312;
        }

        nodes++;

        TranspositionEntry node = TT.get(board.getZobristKey());

        if (node.depth >= depth) {

            //this is the PV node.
            if (node.nodeType == 0) {
                return node.score;
            }

            //Fail-high (Cut-node)
            if (node.nodeType == 1) {
                if (node.score >= beta)
                    return beta;
            }

            //Fail-low (All-Node)
            if (node.nodeType == -1) {
                if (node.score <= alpha)
                    return alpha;
            }

        }

        int winLossDraw = mateScore(plyDeep);
        if (winLossDraw < 1)
            return winLossDraw;

        if (depth <= 2) {
            return negamax(alpha, beta, depth, plyDeep);
        }


        //Order Moves
        List<ScoredMove> orderedMoves = orderMoves(depth, alpha, beta);

        int alphaOrig = alpha;

        Move bestMove = new Move(Square.A1,Square.A1);

        int i = 0;
        boolean inCheck = board.isKingAttacked();
        for (ScoredMove scoredMove : orderedMoves) {
            Move move = scoredMove.move;

            boolean capture = board.getPiece(move.getTo()) != Piece.NONE;

            board.doMove(move);

            int score = -9999999;

            //Late move reductions
            //Do not reduce when in check
            //do not reduce moves that are checks
            //do not reduce captures
            int LMR = 0;
            if (!inCheck && !board.isKingAttacked() && !capture && i > 2) {
                LMR = (int)(0.7844 + Math.log(depth) * Math.log(i) / 2.4696);

                if (i > 5) {
                    LMR += i / 5;
                }

            }

            //Search with the full window until alpha improves
            if (alpha == alphaOrig) {
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
            }

            //Fail-hard beta cut-off
            if (score >= beta)
                break;

            i++;
        }

        insertTable(alpha, beta, alphaOrig, depth, bestMove);

        return Math.min(alpha,beta);

    }

    //At low depths, we use standard minimax with alpha beta to enhance ordering and accuracy of low depth nodes
    public int negamax(int alpha, int beta, int depth, int plyDeep) {

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.

        if (duration / 1000000 > time) {
            return -312312;
        }

        nodes++;
        TranspositionEntry node = TT.get(board.getZobristKey());

        if (node.depth >= depth) {

            //this is the PV node.
            if (node.nodeType == 0) {
                return node.score;
            }

            //Fail-high (Cut-node)
            if (node.nodeType == 1) {
                if (node.score >= beta)
                    return beta;
            }

            //Fail-low (All-Node)
            if (node.nodeType == -1) {
                if (node.score <= alpha)
                    return alpha;
            }

        }

        int winLossDraw = mateScore(plyDeep);
        if (winLossDraw < 1)
            return winLossDraw;

        if (depth <= 0) {
            return qSearch(alpha, beta, 20);
        }

        //Razoring
        //if our position is really poor, we do not need to investigate at low depth nodes
        //We do not razor if we are in check.
        if (!board.isKingAttacked()) {
            if (depth == 1) {
                int staticEval = evaluation.evaluate(board);

                int value = staticEval + 125;
                if (value < beta) {
                    return Math.max(qSearch(alpha, beta, 20), value);
                }
            }
        }

        //Order Moves
        List<ScoredMove> orderedMoves = orderMoves(depth, alpha, beta);

        Move bestMove = new Move(Square.A1,Square.A1);
        int alphaOrig = alpha;

        int i = 0;
        boolean inCheck = board.isKingAttacked();
        for (ScoredMove scoredMove : orderedMoves) {
            Move move = scoredMove.move;

            boolean capture = board.getPiece(move.getTo()) != Piece.NONE;

            board.doMove(move);

            int score = -9999999;

            score = -negamax(-beta, -alpha, depth - 1, plyDeep + 1);

            board.undoMove();

            if (score > alpha) {
                alpha = score;
                bestMove = move;
            }

            //Fail-hard beta cut-off
            if (score >= beta)
                break;

            i++;
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

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.

        if (duration / 1000000 > time) {
            return -312312;
        }

        nodes++;
        int staticEval = evaluation.evaluate(board);
        if (depth == 0) {
            return staticEval;
        }

        if (staticEval >= beta)
            return beta;

        if (alpha - 1125 > staticEval)
            return alpha;

        if (alpha < staticEval)
            alpha = staticEval;

        List<ScoredMove> orderedMoves = orderCapturesAndChecks();

        for (ScoredMove scoredMove : orderedMoves) {
            Move m = scoredMove.move;

            if (board.getPiece(m.getTo()).ordinal() < 11 && orderValueScore.get(board.getPiece(m.getTo()).ordinal()) + staticEval + 200 < alpha )
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

    public List<ScoredMove> orderMoves(int depth, int alpha, int beta) {

        //Get Legal Moves
        List<Move> legalMoves = board.legalMoves();

        //Return this list
        List<ScoredMove> moves = new ArrayList<ScoredMove>();

        //Try all moves
        for (Move move : legalMoves) {
            int score = -99999;
            board.doMove(move);
            //See if move is in TT
            TranspositionEntry node = TT.get(board.getZobristKey());
            if (node.depth > 0) {
                score = -node.score;

                //this was the PV node
                if (node.nodeType == 0)
                    score += 10000;

            }
            else if (depth > 7) {
                score = -qSearch(-beta, -alpha, depth - 7) - 300;
            }
            //if not in TT, score based on static eval (Tapered)
            else {
                score = -evaluation.evaluate(board) - 500;

            }
            board.undoMove();

            if (node.depth <= 0 && board.getPiece(move.getTo()) != Piece.NONE) {

                //Better value capture
                if (orderValueScore.get(board.getPiece(move.getTo()).ordinal()) > orderValueScore.get(board.getPiece(move.getFrom()).ordinal())) {
                    score += 600;
                }

            }

            ScoredMove m = new ScoredMove(move, score);
            moves.add(m);

        }

        Collections.sort(moves);
        Collections.reverse(moves);

        return moves;

    }

    public List<ScoredMove> orderCapturesAndChecks() {

        //Get Legal Moves
        List<Move> legalMoves = board.legalMoves();

        //Return this list
        List<ScoredMove> moves = new ArrayList<ScoredMove>();

        //Try all moves
        for (Move move : legalMoves) {

            boolean aggressiveOrInCheck = board.getPiece(move.getTo()) != Piece.NONE;

            if (board.isKingAttacked())
                aggressiveOrInCheck = true;

            board.doMove(move);

            if (board.isKingAttacked())
                aggressiveOrInCheck = true;

            if (aggressiveOrInCheck) {
                int score = -99999;

                score = -evaluation.evaluate(board);

                board.undoMove();

                //Better value capture
                if (board.getPiece(move.getTo()).ordinal() < 11 && board.getPiece(move.getFrom()).ordinal() < 11  && orderValueScore.get(board.getPiece(move.getTo()).ordinal()) > orderValueScore.get(board.getPiece(move.getFrom()).ordinal())) {
                    score += 600;
                }

                //Worse value capture
                if (board.getPiece(move.getTo()).ordinal() < 11 && board.getPiece(move.getFrom()).ordinal() < 11  && orderValueScore.get(board.getPiece(move.getTo()).ordinal()) < orderValueScore.get(board.getPiece(move.getFrom()).ordinal())) {
                    score -= 600;
                }

                ScoredMove m = new ScoredMove(move, score);
                moves.add(m);
            }
            else {
                board.undoMove();
            }
        }

        Collections.sort(moves);
        Collections.reverse(moves);

        return moves;

    }

    public int mateScore(int depth) {

        if (board.isRepetition())
            return 0;

        if (board.legalMoves().isEmpty()) {

            if (board.isKingAttacked()) {
                return -999999 + depth;
            }

            return 0;
        }

        return 999999;
    }

    public Move getBestMove() {
        return TT.get(board.getZobristKey()).move;
    }
    public Move getBestMove(Board b) {
        return TT.get(b.getZobristKey()).move;
    }

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

    public int getNodes() {
        return nodes;
    }

    void resetNodes() {
        nodes = 0;
    }

    long startTime;
    int time = 0;
    public Move findMove(Board b, int maxDepth, int time, boolean useBook) {
        this.board = b;

        //Check the opening book
        Move openingMove = openingBook.getOpening(b);
        if (useBook && openingMove != null) {
            System.out.println("Book move: " + openingMove);
            return openingMove;
        }

        startTime = System.nanoTime();
        configureStats(time);

        //Iterative deepening
        Move bestMove = new Move(Square.A1,Square.A1);
        for (int depth = 3; depth <= maxDepth; depth++) {

            int score = PVS(-9999999, 9999999, depth, 0);
            int mateIn = mateDisplayScore(score);

            if (displayStats(score,mateIn,depth) == -1)
                break;

            if (mateIn > 0)
                break;

            bestMove = getPV().get(0);
        }

        return bestMove;

    }

    void configureStats(int time) {
        this.time = time;
        nodes = 0;
        TT.clear();
    }

    int mateDisplayScore(int score) {

        if (Math.abs(score) > 98999) {
            return (int)(1000000 - Math.abs(score)) / 2;
        }

        return 0;

    }

    int displayStats(int score, int mateIn, int depth) {
        if (board.getSideToMove() == Side.BLACK) {
            score *= -1;
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        if (duration/ 1000000 > time)
            return -1;

        System.out.println("depth: " + depth + ": " + duration / 1000000 + "ms");

        StringBuilder s = new StringBuilder();
        for (Move m : getPV()) {
            s.append(m.toString() + " ");
        }

        String sign = "+";

        if (score < 0)
            sign = "";
        if (score < -98999)
            sign = "-";

        if (mateIn == 0)
            System.out.println(sign + ((float)score / 100) + ": " + s);
        else {
            System.out.println(sign + "M" + mateIn + ": " + s);
        }

        if (duration > 1000000 && nodes > 0)
            System.out.println("Nodes searched: " +  getNodes() + "(" + (long)getNodes() / (duration / 1000000)  + "knps)");

        return 0;
    }

}
