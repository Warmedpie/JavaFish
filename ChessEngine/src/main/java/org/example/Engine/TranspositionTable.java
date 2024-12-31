package org.example.Engine;

import java.util.HashMap;
import java.util.Map;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class TranspositionTable {

    Map<Long, TranspositionEntry> table = new HashMap<>();

    int N = 10000000;
    TranspositionEntry[] tableArray = new TranspositionEntry[N];

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

    void insertArray(Long key, TranspositionEntry entry) {
        int index = getTableIndex(key);

        if (tableArray[index] == null) {
            tableArray[index] = entry;
            return;
        }

        if (tableArray[index].depth < entry.depth || tableArray[index].hash != key) {
            tableArray[index] = entry;
        }

    }

    TranspositionEntry getArray(Long key) {
        int index = getTableIndex(key);

        if (tableArray[index] == null)
            return null;

        if (tableArray[index].hash == key)
            return tableArray[index];

        return null;
    }

    TranspositionEntry get(Long key) {
        if (!table.containsKey(key)) {
            return null;
        }
        return table.get(key);
    }

    protected int getTableIndex(long zobristKey) {
        return (int)(zobristKey & 0x7FFFFFFF) % N;
    }

    void clear() {

        table.clear();

    }

}
