package org.example;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import org.example.Engine.Evaluation;
import org.example.Engine.Search;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        Board b = new Board();
        Search search = new Search();
        Evaluation e = new Evaluation();

        Scanner scanner = new Scanner(System.in);

        int depthToSearch = 0;
        int timeToSearch = 0;
        boolean playResult = false;
        boolean useBook = true;

        while(true) {

            displayBoard(b);

            System.out.println("Type a command (help for options)");

            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("help")) {
                help();
            }


            String[] arguments = command.split("\\s+");

            try {
                if (arguments[0].equalsIgnoreCase("play")) {
                    playMove(b, arguments[1]);
                }

                if (arguments[0].equalsIgnoreCase("undo")) {
                    b.undoMove();
                }

                if (arguments[0].equalsIgnoreCase("setup")) {
                    b.loadFromFen(command.substring(6));
                }

                if (arguments[0].equalsIgnoreCase("engine")) {

                    if (arguments.length == 1) {
                        Move m = search.findMove(b, depthToSearch, timeToSearch, useBook);

                        if (playResult) {
                            b.doMove(m);
                        }

                    }

                    else {
                        int depth = Integer.parseInt(arguments[1]);
                        int time;


                        if (!Objects.equals(arguments[2], "-ut")) {
                            time = Integer.parseInt(arguments[2]);
                        }
                        else {
                            time = 999999999;
                        }
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

                if (arguments[0].equalsIgnoreCase("static")) {
                    if (b.getSideToMove() == Side.WHITE) {
                        System.out.println("score: " + ((float) e.evaluate(b) / 100));
                    }
                    else {
                        System.out.println("score: " + ((float) -e.evaluate(b) / 100));
                    }

                }

                if (arguments[0].equalsIgnoreCase("exit")) {
                    break;
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
        System.out.println("ENGINE {MAX DEPTH} {MAX TIME IN MS} {-flag}: Generates an engine move, stopping after a certain depth or time (-p: play move, -nb no book, -ut for unlimited time (must replace TIME param))");
        System.out.println("STATIC: Returns the static evaluation of the current position");
        System.out.println("EXIT: exits the program");
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

        int color = -1;



        List<Piece> board = new ArrayList<>(List.of(b.boardToArray()));
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
                display[y].append("   ");
                continue;
            }

            if (p==Piece.WHITE_PAWN) {
                display[y].append(WHITE_FG).append(" ♟ ");
                continue;
            }

            if (p==Piece.WHITE_KNIGHT) {
                display[y].append(WHITE_FG).append(" ♞ ");
                continue;
            }

            if (p==Piece.WHITE_BISHOP) {
                display[y].append(WHITE_FG).append(" ♝ ");
                continue;
            }

            if (p==Piece.WHITE_ROOK) {
                display[y].append(WHITE_FG).append(" ♜ ");
                continue;
            }

            if (p==Piece.WHITE_QUEEN) {
                display[y].append(WHITE_FG).append(" ♛ ");
                continue;
            }

            if (p==Piece.WHITE_KING) {
                display[y].append(WHITE_FG).append(" ♚ ");
                continue;
            }

            if (p==Piece.BLACK_PAWN) {
                display[y].append(BLACK_FG).append(" ♟ ");
                continue;
            }

            if (p==Piece.BLACK_KNIGHT) {
                display[y].append(BLACK_FG).append(" ♞ ");
                continue;
            }

            if (p==Piece.BLACK_BISHOP) {
                display[y].append(BLACK_FG).append(" ♝ ");
                continue;
            }

            if (p==Piece.BLACK_ROOK) {
                display[y].append(BLACK_FG).append(" ♜ ");
                continue;
            }

            if (p==Piece.BLACK_QUEEN) {
                display[y].append(BLACK_FG).append(" ♛ ");
                continue;
            }

            if (p==Piece.BLACK_KING) {
                display[y].append(BLACK_FG).append(" ♚ ");
            }

        }
        display[7].append(WHITE_FG).append(BLACK_BG);
        for (int i = 7; i >= 0; i--) {
            printer.append(display[i]);
        }
        printer.append(WHITE_FG).append(BLACK_BG);

        System.out.println(printer);

    }

}