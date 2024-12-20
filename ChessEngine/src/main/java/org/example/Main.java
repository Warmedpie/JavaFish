package org.example;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import org.example.Engine.Evaluation;
import org.example.Engine.Search;
import org.example.UCI.CommandHandler;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        boolean UCI = true;

        Board b = new Board();
        Search search = new Search();
        Evaluation e = new Evaluation();

        Scanner scanner = new Scanner(System.in);

        int depthToSearch = 0;
        int timeToSearch = 0;
        boolean playResult = false;
        boolean useBook = true;

        CommandHandler uci = new CommandHandler();

        while(true) {
            String command = scanner.nextLine();
            String[] arguments = command.split("\\s+");

            for (String argument : arguments) {
                if (argument.equalsIgnoreCase("UCI")) {
                    UCI = true;
                    break;
                }
            }

            if (UCI) {
                uci.parse(command);
            }


        }

    }

}