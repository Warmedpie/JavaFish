package org.example.Engine;

import java.util.HashMap;
import java.util.Map;

public class MoveTable {

    public Map<Integer, MoveTableEntry> table = new HashMap<>();

    void insert(Integer key, MoveTableEntry me) {
        if (table.containsKey(key)) {
            table.get(key).score += me.score;
            table.get(key).count += 1;
        }
        else {
            table.put(key, me);
        }
    }

    int get(Integer key) {

        if (table.containsKey(key)) {
            return (int)(table.get(key).score / table.get(key).count);
        }

        return -1;

    }

    void clear() {
        table.clear();
    }


}
