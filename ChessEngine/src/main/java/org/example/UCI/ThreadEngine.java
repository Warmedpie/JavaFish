package org.example.UCI;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import org.example.Engine.Search;

import java.util.ArrayList;
import java.util.List;

public class ThreadEngine extends Thread {
    Board b = new Board();
    int depth = 0;
    int moveTime = 0;
    int multiPv = 1;
    Search s = new Search();

    public void init(Board b, int depth, int moveTime, int mPv) {
        this.b = b;
        this.depth = depth;
        this.moveTime = moveTime;
        this.multiPv = mPv;
        this.s = new Search();
    }

    public void stopThink() {
        depth = 0;
        s.configureStats(0);
    }

    public void run() {
        s.setup(b,moveTime);

        int legalMoves = b.legalMoves().size();
        boolean book = false;

        List<Move> pv = new ArrayList<>();
        Move best = null;
            for (int i = 3; i <= depth; i++) {

                List<Move> ignore = new ArrayList<>();

                for (int pvL = 0; pvL < Math.min(multiPv, legalMoves); pvL++) {

                    int score = 0;

                    score = s.PVSIgnore(-9999999, 9999999, i, 0, ignore);

                    if (score == -312312 || score == 312312)
                        break;

                    pv = s.getPV(pvL);
                    ignore.add(pv.get(0));

                    if (pvL == 0)
                        best = pv.get(0);


                    StringBuilder info = new StringBuilder();

                    int mateScore = mateDisplayScore(score);

                    if (score < 0)
                        mateScore *= -1;

                    if (mateScore == 0) {
                        info.append("info multipv ").append(pvL + 1).append(" depth " ).append(i).append(" seldepth ").append(pv.size()).append(" score cp ").append(score).append(" time ").append(s.getTime()).append(" nodes ").append(s.getNodes()).append(" nps ").append(s.getnps()).append(" tbhits ").append(s.getTBhits()).append(" hashfull 0").append(" pv ");
                        for (Move m : pv) {
                            info.append(m.toString()).append(" ");
                        }
                    } else {
                        info.append("info multipv ").append(pvL + 1).append(" depth " ).append(i).append(" seldepth ").append(pv.size()).append(" score mate ").append(mateScore).append(" time ").append(s.getTime()).append(" nodes ").append(s.getNodes()).append(" nps ").append(s.getnps()).append(" tbhits ").append(s.getTBhits()).append(" hashfull 0").append(" pv ");
                        for (Move m : pv) {
                            info.append(m.toString()).append(" ");
                        }
                    }

                    System.out.println(info);

                }

                if (book)
                    break;
            }
        System.out.println("bestmove " + best);
    }

    int mateDisplayScore(int score) {

        if (Math.abs(score) > 98999) {
            return (1000000 - Math.abs(score)) / 2;
        }

        return 0;

    }

}
