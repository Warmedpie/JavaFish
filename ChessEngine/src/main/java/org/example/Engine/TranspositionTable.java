package org.example.Engine;

import java.util.HashMap;
import java.util.Map;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class TranspositionTable {

    Map<Long, TranspositionEntry> table = new HashMap<>();

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
            return null;
        }
        return table.get(key);
    }

    void clear() {

        table.clear();

    }

}
