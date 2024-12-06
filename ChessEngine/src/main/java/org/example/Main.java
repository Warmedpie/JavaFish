package org.example;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import org.example.Engine.Evaluation;
import org.example.Engine.Search;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Board b = new Board();
        Search search = new Search();

        Scanner scanner = new Scanner(System.in);

        int depthToSearch = 0;
        int timeToSearch = 0;
        boolean playResult = false;

        while(true) {

            System.out.println(b);

            System.out.println("Type a command (help for options)");

            String command = scanner.nextLine();

            if (command.toLowerCase().equals("help")) {
                help();
            }


            String[] arguments = command.split("\\s+");

            try {
                if (arguments[0].toLowerCase().equals("play")) {
                    playMove(b, arguments[1]);
                }

                if (arguments[0].toLowerCase().equals("undo")) {
                    b.undoMove();
                }

                if (arguments[0].toLowerCase().equals("setup")) {
                    b.loadFromFen(command.substring(6));
                }

                if (arguments[0].toLowerCase().equals("engine")) {

                    if (arguments.length == 1) {
                        Move m = search.findMove(b, depthToSearch, timeToSearch);

                        if (playResult) {
                            b.doMove(m);
                        }

                    }

                    else {
                        int depth = Integer.parseInt(arguments[1]);
                        int time = Integer.parseInt(arguments[2]);

                        depthToSearch = depth;
                        timeToSearch = time;

                        Move m = search.findMove(b, depth, time);

                        if (arguments.length == 4 && arguments[3].toLowerCase().equals("-p")) {
                            playResult = true;
                            b.doMove(m);
                        }
                    }

                }
            }

            catch(Exception ignore) {}


        }

    }

    public static void help() {
        System.out.println("Command guide:");
        System.out.println("PLAY {MOVE}: plays a from a square to a given square");
        System.out.println("UNDO: undos last move");
        System.out.println("SETUP {fen}: Loads a fen into the engine");
        System.out.println("ENGINE {MAX DEPTH} {MAX TIME IN MS} {-p}: Generates an engine move, stopping after a certain depth or time");
    }

    public static void playMove(Board b, String move) {

        for (Move m : b.legalMoves()) {
            if (m.toString().equals(move)) {
                b.doMove(m);
                return;
            }
        }

        try {
            b.doMove(move);
        }
        catch (Exception ignore) {}
    }

}