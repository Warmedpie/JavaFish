package org.example.UCI;

import com.github.bhlangonijr.chesslib.Board;
import org.example.Engine.Search;

public class CommandHandler {
    boolean UCI = false;
    boolean debug = false;

    Search search = new Search();
    Board board = new Board();

    ThreadEngine engine = new ThreadEngine();

    public void parse(String command) {

        String[] arguments = command.split("\\s+");

        int i = 0;
        for (String argument : arguments) {
            if (argument.equalsIgnoreCase("UCI")) {
                UCI = true;

                System.out.println("id name JavaBot v1.0");
                System.out.println("id author Warmedpie");
                System.out.println("uciok");

                continue;
            }

            if (argument.equalsIgnoreCase("DEBUG")) {
                if (arguments[i + 1].equalsIgnoreCase("on")) {
                    debug = true;
                    i++;
                }
                else if (arguments[i + 1].equalsIgnoreCase("off")) {
                    debug = false;
                    i++;
                }

                continue;
            }

            if (argument.equalsIgnoreCase("ISREADY")) {
                System.out.println("readyok");

                continue;
            }

            if (argument.equalsIgnoreCase("SETOPTION")) {
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

                if (arguments[i + 1].equalsIgnoreCase("FEN")) {
                    i++;

                    board.loadFromFen(arguments[++i] + " " + arguments[++i] + " " + arguments[++i] + " " + arguments[++i] + " " + arguments[++i] + " " + arguments[++i]);

                    if (i + 1 == arguments.length) {
                        continue;
                    }

                    if (arguments[i + 1].equalsIgnoreCase("MOVES")) {
                        for (int q = i + 2; q < arguments.length; q++) {
                            System.out.println(arguments[q]);
                            board.doMove(arguments[q]);
                        }
                    }
                }
                else if (arguments[i + 1].equalsIgnoreCase("STARTPOS")) {
                    board = new Board();
                    i += 1;
                }

                continue;
            }

            if (argument.equalsIgnoreCase("GO")) {

                int depth = 99;
                int moveTime = 999999999;
                int smartTime = 0;
                boolean mateOnly = false;

                i++;

                while (i < arguments.length) {

                    if (arguments[i].equalsIgnoreCase("DEPTH")) {
                        i++;
                        depth = Integer.parseInt(arguments[i++]);

                        continue;
                    }

                    if (arguments[i].equalsIgnoreCase("MOVETIME")) {
                        i++;
                        moveTime = Integer.parseInt(arguments[i++]);

                        continue;
                    }
                    if (arguments[i].equalsIgnoreCase("INFINITE") || arguments[i].equalsIgnoreCase("PONDER")) {
                        depth = 99;
                        moveTime = 999999999;

                        break;
                    }
                    if (arguments[i].equalsIgnoreCase("MATE")) {
                        mateOnly = true;
                        i++;
                        depth = Integer.parseInt(arguments[i++]);

                        continue;
                    }
                    if (arguments[i].equalsIgnoreCase("wtime")) {
                        i++;
                        smartTime += Integer.parseInt(arguments[i++])/20;

                        continue;
                    }
                    if (arguments[i].equalsIgnoreCase("btime")) {
                        i++;
                        smartTime += Integer.parseInt(arguments[i++])/20;

                        continue;
                    }
                    if (arguments[i].equalsIgnoreCase("winc")) {
                        i++;
                        smartTime += Integer.parseInt(arguments[i++]);

                        continue;
                    }
                    if (arguments[i].equalsIgnoreCase("binc")) {
                        i++;
                        smartTime += Integer.parseInt(arguments[i++]);

                        continue;
                    }


                    i++;
                }

                engine = new ThreadEngine();

                if (smartTime > 0 && moveTime == 999999999) {
                    moveTime = smartTime;
                }

                engine.init(board,depth,moveTime);
                engine.start();

                break;

            }

            if (argument.equalsIgnoreCase("STOP")) {
                engine.stopThink();
            }

            if (argument.equalsIgnoreCase("QUIT")) {
                System.exit(0);
            }

            i++;
        }

    }

}
