package org.example;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import org.example.Engine.Evaluation;
import org.example.Engine.Search;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        Board b = new Board();
        Search search = new Search();

        Scanner scanner = new Scanner(System.in);

        int depthToSearch = 0;
        int timeToSearch = 0;
        boolean playResult = false;
        boolean useBook = true;

        while(true) {

            displayBoard(b);

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
                        Move m = search.findMove(b, depthToSearch, timeToSearch, useBook);

                        if (playResult) {
                            b.doMove(m);
                        }

                    }

                    else {
                        int depth = Integer.parseInt(arguments[1]);
                        int time = Integer.parseInt(arguments[2]);

                        if (arguments.length == 4 && arguments[3].equalsIgnoreCase("-nb")) {
                            useBook = false;
                        }
                        if (arguments.length == 5 && arguments[3].equalsIgnoreCase("-nb")) {
                            useBook = false;
                        }
                        if (arguments.length == 5 && arguments[4].equalsIgnoreCase("-nb")) {
                            useBook = false;
                        }

                        depthToSearch = depth;
                        timeToSearch = time;

                        Move m = search.findMove(b, depth, time, useBook);

                        if (arguments.length == 4 && arguments[3].equalsIgnoreCase("-p")) {
                            playResult = true;
                            b.doMove(m);
                        }
                        if (arguments.length == 5 && arguments[3].equalsIgnoreCase("-p")) {
                            playResult = true;
                            b.doMove(m);
                        }
                        if (arguments.length == 5 && arguments[4].equalsIgnoreCase("-p")) {
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
        System.out.println("ENGINE {MAX DEPTH} {MAX TIME IN MS} {-flag}: Generates an engine move, stopping after a certain depth or time (-p: play move, -nb no book)");
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

    public static void displayBoard(Board b) {

        StringBuilder[] display = new StringBuilder[8];

        for (int i = 0; i < 8; i++) {
            display[i] = new StringBuilder("\n");
        }

        StringBuilder printer = new StringBuilder();

        String BLACK_BG = "\u001B[49m";
        String GREEN_BG = "\u001B[42m";
        String WHITE_BG = "\u001B[47m";
        String BLACK_FG = "\u001B[30m";
        String WHITE_FG = "\u001B[97m";

        int x = 0;
        int y = 0;

        int color = 1;



        List<Piece> board = new ArrayList(List.of(b.boardToArray()));
        board.remove(64);

        for (Piece p : board) {
            //Switch rank
            if (x == 8) {
                display[y].append(BLACK_BG);
                y++;
                x = 0;

                if (y == 8)
                    break;

            }
            else {
                color *= -1;
            }
            x++;

            //Display color
            if (color == -1) {
                display[y].append(WHITE_BG);
            }
            else {
                display[y].append(GREEN_BG);
            }

            if (p == Piece.NONE) {
                display[y].append("   ");
                continue;
            }

            if (p==Piece.WHITE_PAWN) {
                display[y].append(WHITE_FG + " P ");
                continue;
            }

            if (p==Piece.WHITE_KNIGHT) {
                display[y].append(WHITE_FG + " N ");
                continue;
            }

            if (p==Piece.WHITE_BISHOP) {
                display[y].append(WHITE_FG + " B ");
                continue;
            }

            if (p==Piece.WHITE_ROOK) {
                display[y].append(WHITE_FG + " R ");
                continue;
            }

            if (p==Piece.WHITE_QUEEN) {
                display[y].append(WHITE_FG + " Q ");
                continue;
            }

            if (p==Piece.WHITE_KING) {
                display[y].append(WHITE_FG + " K ");
                continue;
            }

            if (p==Piece.BLACK_PAWN) {
                display[y].append(BLACK_FG + " p ");
                continue;
            }

            if (p==Piece.BLACK_KNIGHT) {
                display[y].append(BLACK_FG + " n ");
                continue;
            }

            if (p==Piece.BLACK_BISHOP) {
                display[y].append(BLACK_FG + " b ");
                continue;
            }

            if (p==Piece.BLACK_ROOK) {
                display[y].append(BLACK_FG + " r ");
                continue;
            }

            if (p==Piece.BLACK_QUEEN) {
                display[y].append(BLACK_FG + " q ");
                continue;
            }

            if (p==Piece.BLACK_KING) {
                display[y].append(BLACK_FG + " k ");
                continue;
            }

        }
        display[7].append(WHITE_FG + BLACK_BG);

        for (int i = 7; i >= 0; i--) {
            printer.append(display[i]);
        }

        System.out.println(printer);

    }

}