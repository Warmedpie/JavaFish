package org.example.Engine;

import com.github.bhlangonijr.chesslib.move.Move;

//This class is used for move ordering
public class ScoredMove implements Comparable<ScoredMove> {

    public Move move;
    public int score;

    public ScoredMove(Move m, int score) {
        this.move = m;
        this.score = score;
    }

    @Override
    public int compareTo(ScoredMove o) {
        return Integer.compare(this.score, o.score);
    }
}