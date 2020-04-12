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
    public static int[] goldCoord = new int[]{-1,-1};
    public static int mapCount = 0;
    public static final ArrayList<String> deadEndTileNames = new ArrayList<>() {
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
        System.out.println("student player acting as player number: "+boardState.getTurnPlayer());
        moves = boardState.getAllLegalMoves();
        cards = boardState.getCurrentPlayerCards();
//        System.out.println("Testing: Board " + Arrays.deepToString(boardState.getHiddenBoard()));
//        System.out.println("Testing: Board " + boardState.getBoardForDisplay()[12][3].getIdx());
        if (mapCount == 2){
            goldTileRevealed = true;
            goldCoord[0] = 12;
            goldCoord[1] = 7;
        }
        if (boardState.getBoardForDisplay()[12][3].getName().equals("Tile:nugget") || boardState.getBoardForDisplay()[12][5].getName().equals("Tile:nugget") || boardState.getBoardForDisplay()[12][7].getName().equals("Tile:nugget")){
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

    // simple greedy approach
    private int AIDecision(SaboteurBoardState boardState){
        SaboteurCard cardSelected = null;

        // if in malus state
        if (boardState.getNbMalus(boardState.getTurnPlayer()) > 0){
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
        else{
            // if the malus card is in hand, play it at once to reduce the chance of the random player messing up the path
//            for (SaboteurCard card : cards) {
//                if (card instanceof SaboteurMalus)
//                    cardSelected = card;
//            }
//            if (cardSelected != null) {
//                for (SaboteurMove mov : moves) {
//                    if (mov.getCardPlayed().getName().equals(cardSelected.getName())) {
//                        return moves.indexOf(mov);
//                    }
//                }
//            }

            // if no malus card in hand and the gold card has not yet been revealed
//            else if (!goldTileRevealed){
            if (!goldTileRevealed){
                // check if the map card is hand
                for (SaboteurCard card : cards) {
                    if (card instanceof SaboteurMap)
                        cardSelected = card;
                }
                if (cardSelected != null) {
                    System.out.println("(12,3) :"+boardState.getBoardForDisplay()[12][3].getName());
                    System.out.println("(12,5) :"+boardState.getBoardForDisplay()[12][5].getName());
                    System.out.println("(12,7) :"+boardState.getBoardForDisplay()[12][7].getName());
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
            }

            // no malus card in hand and gold location is known
            else {
                // discard map cards in hand
                for (SaboteurCard card : cards) {
                    if (card instanceof SaboteurMap)
                        cardSelected = card;
                }
                if (cardSelected != null){
                    for (SaboteurMove mov : moves) {
                        if (mov.getCardPlayed().getName().equals("Drop") && mov.getPosPlayed()[0] == cards.indexOf(cardSelected))
                            return moves.indexOf(mov);
                    }
                }

                // drop all dead end cards
                for (SaboteurMove mov : moves) {
                    if (mov.getCardPlayed().getName().equals("Drop")){
                        for (String dTile : deadEndTileNames){
//                            System.out.println("HERE!!!");
//                            System.out.println(cards.get(mov.getPosPlayed()[0]).getName());
//                            System.out.println(dTile);
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

                // destroy all dead end currently on the board
                for (SaboteurCard card : cards) {
                    if (card instanceof SaboteurDestroy)
                        cardSelected = card;
                }
                if (cardSelected != null){
                    for (SaboteurMove mov : moves){
                        if (mov.getCardPlayed().getName().equals("Destroy")){
                            for (String dTile : deadEndTileNames) {
                                if (dTile.equals(boardState.getBoardForDisplay()[mov.getPosPlayed()[0]][mov.getPosPlayed()[1]].getName()))
                                    return moves.indexOf(mov);
                            }
                        }
                    }
                }

                // greedy approach to the gold tile

                for (SaboteurMove mov : moves){
                    if (mov.getPosPlayed()[0] > 5){
                        if (Math.abs(mov.getPosPlayed()[1]-goldCoord[1]) <= 1)
                        return moves.indexOf(mov);
                    }
                }
            }
        }

        return 0;
    }

}


