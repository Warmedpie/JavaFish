package org.example.Engine;
import com.github.bhlangonijr.chesslib.move.Move;

public class TranspositionEntry {

    public int score;
    public int depth;
    public Move move;
    public long hash;

    /*
    -1: Fail Low (All-Node)
    0: PV-Node
    1: Fail-high (Cut-node)
    2: qSearch (Ignore for all purposes but move ordering)
     */
    public int nodeType;

    public TranspositionEntry(int score, int depth, int nodeType, Move move, long hash) {
        this.score = score;
        this.depth = depth;
        this.nodeType = nodeType;
        this.move = move;
        this.hash = hash;
    }

}
