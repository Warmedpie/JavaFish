package org.example.UCI;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import org.example.Engine.Search;

import java.io.PrintWriter;

import static java.lang.Thread.sleep;

public class CommandHandler {
    boolean UCI = false;
    boolean debug = false;

    Search search = new Search();
    Board board = new Board();

    int multiPv = 1;

    public void parse(String command) {
        String[] arguments = command.split("\\s+");

        int i = 0;
        for (String argument : arguments) {
            if (argument.equalsIgnoreCase("UCI")) {
                UCI = true;

                System.out.println("id name JavaBot v1.0");
                System.out.println("id author Warmedpie");
                System.out.println("option name MultiPV type spin default 1 min 1 max 5");
                System.out.println("uciok");

                continue;
            }

            if (argument.equalsIgnoreCase("DEBUG")) {
                if (arguments.length > i + 1) {
                    if (arguments[i + 1].equalsIgnoreCase("on")) {
                        debug = true;
                        i++;
                    } else if (arguments[i + 1].equalsIgnoreCase("off")) {
                        debug = false;
                        i++;
                    }
                }

                continue;
            }

            if (argument.equalsIgnoreCase("ISREADY")) {
                System.out.println("readyok");

                continue;
            }

            if (argument.equalsIgnoreCase("SETOPTION")) {
                if (arguments.length > i + 1) {
                    if (arguments[i + 1].equalsIgnoreCase("name")) {
                        if (arguments.length > i + 4) {
                            if (arguments[i + 2].equalsIgnoreCase("MultiPV") && arguments[i + 3].equalsIgnoreCase("value")) {
                                multiPv = Math.min(Integer.parseInt(arguments[i + 4]), 5);

                                if (multiPv < 1)
                                    multiPv = 1;
                            }
                        }
                    }
                }

                continue;
            }

            if (argument.equalsIgnoreCase("REGISTER")) {
                continue;
            }

            if (argument.equalsIgnoreCase("UCINEWGAME")) {
                search = new Search();
                board = new Board();

                continue;
            }

            if (argument.equalsIgnoreCase("POSITION")) {
                if (arguments.length > i + 1) {
                    if (arguments[i + 1].equalsIgnoreCase("FEN")) {
                        i++;

                        board.loadFromFen(arguments[++i] + " " + arguments[++i] + " " + arguments[++i] + " " + arguments[++i] + " " + arguments[++i] + " " + arguments[++i]);

                        if (arguments.length > i + 1) {
                            if (arguments[++i].equalsIgnoreCase("MOVES")) {
                                for (int q = ++i; q < arguments.length; q++) {
                                    board.doMove(arguments[q]);
                                }
                            }
                        }
                    } else if (arguments[i + 1].equalsIgnoreCase("STARTPOS")) {
                        board = new Board();
                        i += 1;
                        if (arguments.length > i + 1) {
                            if (arguments[++i].equalsIgnoreCase("MOVES")) {
                                for (int q = ++i; q < arguments.length; q++) {
                                    board.doMove(arguments[q]);
                                }
                            }
                        }

                    }
                }

                continue;
            }

            if (argument.equalsIgnoreCase("GO")) {

                int depth = 32;
                int moveTime = 999999999;
                int smartTime = 0;
                boolean mateOnly = false;

                i++;

                while (i < arguments.length) {
                    if (arguments[i].equalsIgnoreCase("DEPTH")) {
                        i++;
                        depth = Integer.parseInt(arguments[i++]);
                    } else if (arguments[i].equalsIgnoreCase("MOVETIME")) {
                        i++;
                        moveTime = Integer.parseInt(arguments[i++]);
                    } else if (arguments[i].equalsIgnoreCase("INFINITE") || arguments[i].equalsIgnoreCase("PONDER")) {
                        i++;
                    } else if (arguments[i].equalsIgnoreCase("MATE")) {
                        mateOnly = true;
                        i++;
                        depth = Integer.parseInt(arguments[i++]);
                    } else if (arguments[i].equalsIgnoreCase("wtime")) {
                        i++;
                        if (board.getSideToMove() == Side.WHITE)
                            smartTime += Integer.parseInt(arguments[i++]) / 600;
                    } else if (arguments[i].equalsIgnoreCase("btime")) {
                        i++;
                        if (board.getSideToMove() == Side.BLACK)
                            smartTime += Integer.parseInt(arguments[i++]) / 600;
                    } else if (arguments[i].equalsIgnoreCase("winc")) {
                        i++;
                        smartTime += Integer.parseInt(arguments[i++]);
                    } else if (arguments[i].equalsIgnoreCase("binc")) {
                        i++;
                        smartTime += Integer.parseInt(arguments[i++]);
                    } else {
                        moveTime = 5000;
                        depth = 32;
                        i++;
                    }

                }

                ThreadEngine engine = new ThreadEngine();

                if (smartTime > 0 && moveTime == 999999999) {
                    moveTime = smartTime;
                }

                engine.init(board, depth, moveTime, multiPv, debug);
                engine.run();

                break;

            }

            if (argument.equalsIgnoreCase("STOP")) {
                Search.time = 0;
            }

            if (argument.equalsIgnoreCase("QUIT")) {
                System.exit(0);
            }

            i++;
        }
    }

}
