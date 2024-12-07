package org.example.Engine;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;

import java.io.File;
import java.util.*;

public class Book {
    Map<String, List<String>> book = new HashMap<>();

    public Book() {
        try {
            String file ="src/main/resources/Book.txt";

            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();

                String[] data = line.split("_");
                if (truncate(data[0]," ").length() > 0 && truncate(data[1]," ").length()> 0) {
                    String fen1 = truncate(data[0], " ");
                    if (!book.containsKey(fen1))
                        book.put(fen1, new ArrayList<String>());

                    book.get(fen1).add(truncate(data[1], " "));
                }

            }
            myReader.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String checkBook(String fen) {
        if (book.containsKey(fen)) {

            Random rand = new Random();
            return book.get(fen).get(rand.nextInt(book.get(fen).size()));

        }

        return "";
    }

    public static String truncate(String str, String delim) {
        int idx = str.indexOf(delim);

        if (idx == -1)
            return "";

        return str.substring(0, idx);
    }

    public Move getOpening(Board b) {
        String fen = truncate(b.getFen(), " ");
        String fenAfterMove = checkBook(fen);


        return openMoveFromFen(b, fenAfterMove);
    }

    public Move openMoveFromFen(Board b, String fen) {
        for (Move m : b.legalMoves()) {
            b.doMove(m);

            if (truncate(b.getFen(), " ").equals(fen))
                return m;

            b.undoMove();
        }

        return null;
    }

}
