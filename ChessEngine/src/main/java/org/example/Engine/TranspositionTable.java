package org.example.Engine;

import java.util.HashMap;
import java.util.Map;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class TranspositionTable {

    Map<Long, TranspositionEntry> table = new HashMap<Long, TranspositionEntry>();

    void insert(Long key, TranspositionEntry entry) {

        if (table.containsKey(key)) {

            if (table.get(key).depth < entry.depth) {

                table.put(key, entry);
            }

        }
        else {

            table.put(key, entry);
        }

    }

    TranspositionEntry get(Long key) {
        if (!table.containsKey(key)) {
            return new TranspositionEntry(-1,-1,-9,new Move(Square.A1, Square.A1));
        }
        return table.get(key);
    }

    void clear() {
        table.clear();
    }

}
