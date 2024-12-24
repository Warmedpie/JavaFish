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
import java.io.PrintWriter;
import java.util.*;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        CommandHandler uci = new CommandHandler();

        while (true) {

            String command = scanner.nextLine();

            uci.parse(command);
        }

    }

}