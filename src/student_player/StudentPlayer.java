package student_player;

import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {
    public static boolean goldTileRevealed = false;
    public static ArrayList<SaboteurMove> moves;
    public static ArrayList<SaboteurCard> cards;
    public static int[] goldCoord = new int[]{-1, -1};
    public static int mapCount = 0;
    public static boolean critical = false;
    public static final ArrayList<String> deadEndTileNames = new ArrayList<String>() {
        {
            add("Tile:1");
            add("Tile:2");
            add("Tile:2_flip");
            add("Tile:3");
            add("Tile:3_flip");
            add("Tile:4");
            add("Tile:4_flip");
            add("Tile:11");
            add("Tile:11_flip");
            add("Tile:12");
            add("Tile:12_flip");
            add("Tile:13");
            add("Tile:14");
            add("Tile:14_flip");
            add("Tile:15");
        }
    };

    public StudentPlayer() {
//        super("260784819"); // Cameron Cherif
        super("260776911"); // Haoran Du
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    @Override
    // simple greedy approach
    public Move chooseMove(SaboteurBoardState boardState) {
        System.out.println("student player acting as player number: " + boardState.getTurnPlayer());
        moves = boardState.getAllLegalMoves();
        cards = boardState.getCurrentPlayerCards();
        if (mapCount == 2) {
            goldTileRevealed = true;
            goldCoord[0] = 12;
            goldCoord[1] = 7;
        }
        if (boardState.getBoardForDisplay()[12][3].getName().equals("Tile:nugget") || boardState.getBoardForDisplay()[12][5].getName().equals("Tile:nugget") || boardState.getBoardForDisplay()[12][7].getName().equals("Tile:nugget")) {
            goldTileRevealed = true;
            if (boardState.getBoardForDisplay()[12][3].getName().equals("Tile:nugget")) {
                goldCoord[0] = 12;
                goldCoord[1] = 3;
            }
            if (boardState.getBoardForDisplay()[12][5].getName().equals("Tile:nugget")) {
                goldCoord[0] = 12;
                goldCoord[1] = 5;
            }
            if (boardState.getBoardForDisplay()[12][7].getName().equals("Tile:nugget")) {
                goldCoord[0] = 12;
                goldCoord[1] = 7;
            }
        }
        System.out.println(goldTileRevealed);
        System.out.println(goldCoord[1]);
        for (SaboteurMove mov : moves)
            System.out.println("Testing: " + mov.toPrettyString());
        return moves.get(AIDecision(boardState));
    }

    /**
     * General strategy: Modified Greedy Algorithm + aggro -> find gold asap, move whenever possible, block opponent if able to when getting
     * close to the objective, and discard undesired cards when still able to draw to increase chances of having good hand
     * for our strat, let's consider destroys like dead end tiles. There are in total 12/56 cards that are be discarded.
     */

    private int AIDecision(SaboteurBoardState boardState) {
        SaboteurCard cardSelected = null;

        // if in malus state
        if (boardState.getNbMalus(boardState.getTurnPlayer()) > 0) {
            for (SaboteurCard card : cards) {
                if (card instanceof SaboteurBonus)
                    cardSelected = card;
            }
            // use the bonus card at once if possible
            if (cardSelected != null) {
                for (SaboteurMove mov : moves) {
                    if (mov.getCardPlayed().getName().equals(cardSelected.getName())) {
                        return moves.indexOf(mov);
                    }
                }
            }
        }

        // if not in malus state
        else {
            // if the gold card has not yet been revealed
            if (!goldTileRevealed) {
                // check if the map card is in hand
                for (SaboteurCard card : cards) {
                    if (card instanceof SaboteurMap)
                        cardSelected = card;
                }
                if (cardSelected != null) {
                    // check for (12,3) (12,5)
                    if (boardState.getBoardForDisplay()[12][3].getName().equals("Tile:goalTile")) {
                        for (SaboteurMove mov : moves) {
                            // use the map card at the hidden object at once
                            if (mov.getCardPlayed().getName().equals(cardSelected.getName()) && mov.getPosPlayed()[1] == 3) {
                                mapCount++;
                                return moves.indexOf(mov);
                            }
                        }
                    } else if (boardState.getBoardForDisplay()[12][5].getName().equals("Tile:goalTile")) {
                        for (SaboteurMove mov : moves) {
                            // use the map card at the hidden object at once
                            if (mov.getCardPlayed().getName().equals(cardSelected.getName()) && mov.getPosPlayed()[1] == 5) {
                                mapCount++;
                                return moves.indexOf(mov);
                            }
                        }
                    }
                }

                // drop all dead end cards
                for (SaboteurMove mov : moves) {
                    if (mov.getCardPlayed().getName().equals("Drop")) {
                        for (String dTile : deadEndTileNames) {
                            if (cards.get(mov.getPosPlayed()[0]).getName().equals(dTile))
                                return moves.indexOf(mov);
                        }
                    }
                }

                // destroy all dead end currently on the board below entrance
                for (SaboteurCard card : cards) {
                    if (card instanceof SaboteurDestroy)
                        cardSelected = card;
                }
                if (cardSelected != null) {
                    for (SaboteurMove mov : moves) {
                        if (mov.getCardPlayed().getName().equals("Destroy")) {
                            for (String dTile : deadEndTileNames) {
                                if (dTile.equals(boardState.getBoardForDisplay()[mov.getPosPlayed()[0]][mov.getPosPlayed()[1]].getName()) && mov.getPosPlayed()[0] > 5)
                                    return moves.indexOf(mov);
                            }
                        }
                    }
                }

                // perform simple greedy approach to go down
                for (SaboteurMove mov : moves) {
                    if (mov.getPosPlayed()[0] >= 5) {
                        if (mov.getPosPlayed()[1] <= 7 && (mov.getPosPlayed()[1] >= 3))
                            return moves.indexOf(mov);
                    }
                }
            }

            // gold location is known
            else {
                // discard map cards in hand
                for (SaboteurCard card : cards) {
                    if (card instanceof SaboteurMap)
                        cardSelected = card;
                }
                if (cardSelected != null) {
                    for (SaboteurMove mov : moves) {
                        if (mov.getCardPlayed().getName().equals("Drop") && mov.getPosPlayed()[0] == cards.indexOf(cardSelected))
                            return moves.indexOf(mov);
                    }
                }

                // drop all dead end cards
                for (SaboteurMove mov : moves) {
                    if (mov.getCardPlayed().getName().equals("Drop")) {
                        for (String dTile : deadEndTileNames) {
                            if (cards.get(mov.getPosPlayed()[0]).getName().equals(dTile))
                                return moves.indexOf(mov);
                        }
                    }
                }

                // convert the cards on board to ArrayList
                List<SaboteurTile> collection = Arrays.stream(boardState.getBoardForDisplay())
                        .flatMap(Arrays::stream)
                        .collect(Collectors.toList());
                ArrayList<SaboteurTile> cardsOnBoard = new ArrayList<>(collection);

                // destroy all dead end currently on the board below entrance
                for (SaboteurCard card : cards) {
                    if (card instanceof SaboteurDestroy)
                        cardSelected = card;
                }
                if (cardSelected != null) {
                    for (SaboteurMove mov : moves) {
                        if (mov.getCardPlayed().getName().equals("Destroy")) {
                            for (String dTile : deadEndTileNames) {
                                if (dTile.equals(boardState.getBoardForDisplay()[mov.getPosPlayed()[0]][mov.getPosPlayed()[1]].getName()) && mov.getPosPlayed()[0] > 5)
                                    return moves.indexOf(mov);
                            }
                        }
                    }
                }

                // not in critical region, performing simple greedy approach
                if (!critical) {
                    for (SaboteurMove mov : moves) {
                        // reserve the + tile to the critical area
                        if (mov.getCardPlayed().getName().equals("Tile:8"))
                            continue;
                        if (mov.getPosPlayed()[0] > 5) {
                            if (Math.abs(mov.getPosPlayed()[1] - goldCoord[1]) <= 1) {
                                // reaching the critical region.
                                if (mov.getPosPlayed()[0] > 8)
                                    critical = true;
                                return moves.indexOf(mov);
                            }
                        }
                    }
                }
                // in critical region
                else {
                    // if the malus card is in hand, play it at once to reduce the chance of the random player messing up the path
                    for (SaboteurCard card : cards) {
                        if (card instanceof SaboteurMalus)
                            cardSelected = card;
                    }
                    if (cardSelected != null) {
                        for (SaboteurMove mov : moves) {
                            if (mov.getCardPlayed().getName().equals(cardSelected.getName())) {
                                return moves.indexOf(mov);
                            }
                        }
                    }
                    // TODO the + and | tiles in the critical area has priority
                    boolean hasTileVer = false;
                    for (SaboteurCard card : cards){
                        if (card.getName().equals("Tile:8") || card.getName().equals("Tile:0") || card.getName().equals("Tile:6") || card.getName().equals("Tile:6_flip")){
                            hasTileVer = true;
                            break;
                        }
                    }
                    if (!hasTileVer) {
                        for (SaboteurMove mov : moves) {
                            if (mov.getPosPlayed()[0] > 8) {
                                if (Math.abs(mov.getPosPlayed()[1] - goldCoord[1]) <= 1) {
                                    return moves.indexOf(mov);
                                }
                            }
                        }
                    }
                    else{
                        for (SaboteurMove mov : moves){
                            if (mov.getPosPlayed()[0] > 8 && mov.getPosPlayed()[0] < 12 && Math.abs(mov.getPosPlayed()[1] - goldCoord[1]) <= 1 && (mov.getCardPlayed().getName().equals("Tile:8") || mov.getCardPlayed().getName().equals("Tile:0") || mov.getCardPlayed().getName().equals("Tile:6") || mov.getCardPlayed().getName().equals("Tile:6_flip") ))
                                return moves.indexOf(mov);
                        }
                    }
                }
            }
        }
        // default
        return moves.size() / 2;
    }
}
