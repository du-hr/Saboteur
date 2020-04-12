package student_player;

import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

import java.util.ArrayList;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {
    public static boolean goldTileRevealed = false;
    public static ArrayList<SaboteurMove> moves;
    public static ArrayList<SaboteurCard> cards;
    public static int[] goldCoord = new int[]{-1,-1};
    public static int mapCount = 0;

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

            // if the gold card has not yet been revealed
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
                    // 3, 5, 7
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
                    } else if (boardState.getBoardForDisplay()[12][7].getName().equals("Tile:goalTile")) {
                        for (SaboteurMove mov : moves) {
                            // use the map card at the hidden object at once
                            if (mov.getCardPlayed().getName().equals(cardSelected.getName()) && mov.getPosPlayed()[1] == 7)
                                return moves.indexOf(mov);
                        }
                    }
                }
            }
        }
        return 0;
    }
    
}


