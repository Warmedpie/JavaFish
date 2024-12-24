package org.example.Engine;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;

import java.io.File;
import java.util.*;

public class Book {
    Map<String, List<String>> book = new HashMap<>();

    public Book() {
        try {
            String file ="Book.txt";

            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();

                String[] data = line.split(" ");

                Board b = new Board();
                if (book.containsKey(b.getFen())) {
                    book.get(b.getFen()).add(data[0]);
                }
                else {
                    book.put(b.getFen(), new ArrayList<>());
                    book.get(b.getFen()).add(data[0]);
                }
                for (int i = 0; i < data.length - 1; i++) {
                    b.doMove(data[i]);

                    if (book.containsKey(b.getFen())) {
                        book.get(b.getFen()).add(data[i + 1]);
                    }
                    else {
                        book.put(b.getFen(), new ArrayList<>());
                        book.get(b.getFen()).add(data[i + 1]);
                    }

                }

            }
            myReader.close();

        } catch (Exception ignore) {}

    }

    public String checkBook(String fen) {
        if (book.containsKey(fen)) {

            Random rand = new Random();
            return book.get(fen).get(rand.nextInt(book.get(fen).size()));

        }

        return "";
    }

}