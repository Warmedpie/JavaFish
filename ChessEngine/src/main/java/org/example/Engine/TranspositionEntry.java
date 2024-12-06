package org.example.Engine;
import com.github.bhlangonijr.chesslib.move.Move;

public class TranspositionEntry {

    public int score;
    public int depth;
    public Move move;

    /*
    -1: Fail Low (All-Node)
    0: PV-Node
    1: Fail-high (Cut-node)
     */
    public int nodeType;

    public TranspositionEntry(int score, int depth, int nodeType, Move move) {
        this.score = score;
        this.depth = depth;
        this.nodeType = nodeType;
        this.move = move;
    }

}
