package student_player;

import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;
import boardgame.Move;
import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A player file submitted by a student.
 */
public class StudentPlayer extends SaboteurPlayer {

    public StudentPlayer() {
        super("260784819 & 260776911");
    }

    //add global variable for probability of discarding a card, will increase with tile placements, and decrease with turn number
    //hidden objectives, -1 for hidden, 0 for cross, 1 for gold
    public static int[] hidden = {-1, -1, -1};
    public static int[][] hiddenCoordinates = {{12, 3}, {12, 5}, {12, 7}};
    public static boolean foundGold = false, justDiscarded = false;
    public static int numberOfCardsDiscarded = 0, numberOfTilesPlaced = 0;
    public static ArrayList<String> discardable = new ArrayList<String>(Arrays.asList("1", "2", "2_flip", "3", "3_flip", "4", "4_flip", "10", "11", "11_flip", "12", "12_flip", "13", "14", "14_flip", "15"));
    public static ArrayList<String> vertical = new ArrayList<String>(Arrays.asList("0", "6", "6_flip", "8"));

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */

    /*
     * General strategy: Rush + aggro -> find gold asap, move whenever possible, block opponent if able to when getting
     * close to the objective, and discard undesired cards when still able to draw to increase chances of having good hand
     * for our strat, let's consider destroys like dead end tiles. There are in total 12/56 cards that are discardable
     */
    public static boolean goldTileRevealed = false;
    public static ArrayList<SaboteurMove> moves;
    public static ArrayList<SaboteurCard> cards;
    public static int[] goldCoord = new int[]{-1, -1};
    public static boolean critical = false;


    public Move chooseMove(SaboteurBoardState boardState) {
        int myTurn = boardState.getTurnPlayer();
        boolean canDiscard = boardState.getTurnNumber() < 42;
        boolean hasMalus = boardState.getNbMalus(myTurn) > 0;
        SaboteurCard ownsMalus = null;
        ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        ArrayList<SaboteurCard> hand = boardState.getCurrentPlayerCards();
        //ArrayLists containing the index of cards in hand
        ArrayList<Integer> discardInHand = new ArrayList<>();
        ArrayList<Integer> usefulTiles = new ArrayList<>();
        ArrayList<Integer> verticalTiles = new ArrayList<>();
        SaboteurTile[][] boardThisRound = boardState.getHiddenBoard();
        //first thing to do, check if we need to check any objectives that would have been revealed by the map
        for (int i = 0; i < 2; i++) {
            SaboteurTile objective = boardThisRound[this.hiddenCoordinates[i][0]][this.hiddenCoordinates[i][1]];
            if (objective.getIdx().equals("nugget")) {
                this.hidden[i] = 1;
                this.foundGold = true;
                this.goldCoord[0] = this.hiddenCoordinates[i][0];
                this.goldCoord[1] = this.hiddenCoordinates[i][1];
            } else
                this.hidden[i] = 0;
        }
        //Get information on current hand
        // Priority on two moves: 1- If own bonus and have malus then that will be our move
        //  2- If own map and still have not found the gold, then that will be our move.
        for (SaboteurCard card : hand) {
            if (card instanceof SaboteurBonus && hasMalus){
                justDiscarded = false;
                return (new SaboteurMove(card, 0, 0, myTurn));
            }else if (card instanceof SaboteurMalus)
                ownsMalus = card;
            else if (card instanceof SaboteurTile) {
                if (discardable.contains(((SaboteurTile) card).getIdx()))
                    discardInHand.add(hand.indexOf(card));
                else usefulTiles.add(hand.indexOf(card));
                if(vertical.contains(((SaboteurTile) card).getIdx()))
                    verticalTiles.add(hand.indexOf(card));
            } else if (card instanceof SaboteurDestroy)
                discardInHand.add(hand.indexOf(card));
            else if (card instanceof SaboteurMap) {
                if (!this.foundGold) {
                    for (int i = 0; i < 2; i++) {
                        if (this.hidden[i] == -1){
                            justDiscarded = false;
                            return (new SaboteurMove(card, this.hiddenCoordinates[i][0], this.hiddenCoordinates[i][1], myTurn));
                        }
                    }
                }else{
                    discardInHand.add(hand.indexOf(card));
            }
        }
        //if we enter this condition, then have malus and own no bonus nor map.
        // Two moves possible: 1- Counter with malus if own one
        //  2- Discard a card: if have discardable card in hand then drop it/first one if have many,
        //                      else discard first one in hand
        if (hasMalus) {
            if (ownsMalus != null){
                justDiscarded = false;
                return (new SaboteurMove(ownsMalus, 0, 0, myTurn));
            }else if (!discardInHand.isEmpty()){
                justDiscarded = true;
                for (SaboteurMove mov : moves) {
                    if (mov.getCardPlayed().getName().equals("Drop")) {
                        for (String dTile : discardable) {
                            if (cards.get(mov.getPosPlayed()[0]).getName().equals(dTile))
                                return mov;
                        }
                    }
                }
            }else{
                justDiscarded = true;
                for (SaboteurMove mov : moves) {
                    if (mov.getCardPlayed().getName().equals("Drop")) {
                        return mov;
                    }
                }
            }
        }
        //Check if critical zone entered
        else if (!critical) {
            //Check if we are still at a turn where we can discard and get another card
            if(canDiscard) {
                double probabilityOfDiscarding = -Math.sqrt(boardState.getTurnNumber() + numberOfCardsDiscarded - numberOfTilesPlaced - discardInHand.size()) / 1764 + 1;
                double probabilityOfPlacing = Math.sqrt(boardState.getTurnNumber() - numberOfTilesPlaced + numberOfCardsDiscarded + usefulTiles.size()) / 1764;
                //choose whether to discard an undesired card probabilistically
                if ((probabilityOfDiscarding >= probabilityOfPlacing && !justDiscarded && !discardInHand.isEmpty()) || usefulTiles.isEmpty()) {
                    justDiscarded = true;
                    for (SaboteurMove mov : moves) {
                        if (mov.getCardPlayed().getName().equals("Drop")) {
                            for (String dTile : discardable) {
                                if (cards.get(mov.getPosPlayed()[0]).getName().equals(dTile))
                                    return mov;
                            }
                        }
                    }
                }
            }
            //If cannot discard or chose not to, strategy divided in two cases
            //Case no.1 in which gold location not known
            if(!foundGold) {
                //Under no malus, no bonus to use, so make a move!!
                //  Performing simple greedy approach to go down, if location of the gold is known, save the cross tiles for later!`
                for (SaboteurMove mov : moves) {
                    if (mov.getPosPlayed()[0] >= 5) {
                        if (mov.getPosPlayed()[1] <= 7 && (mov.getPosPlayed()[1] >= 3)) {
                            justDiscarded = false;
                            return mov;
                        }
                    }
                }
            }
            //Case no.2
            else{
                for (SaboteurMove mov : moves) {
                    // reserve the + tile to the critical area
                    if (mov.getPosPlayed()[0] > 5 && !(mov.getCardPlayed().getName().equals("Tile:8"))) {
                        if (Math.abs(mov.getPosPlayed()[1] - goldCoord[1]) <= 1) {
                            // reaching the critical region.
                            if (mov.getPosPlayed()[0] > 8)
                                critical = true;
                            justDiscarded = false;
                            return mov;
                        }
                    }
                }
            }
            //If makes it here, then no move made regardless of whether the gold location has been found
            if (!discardInHand.isEmpty()){
                justDiscarded = true;
                for (SaboteurMove mov : moves) {
                    if (mov.getCardPlayed().getName().equals("Drop")) {
                        for (String dTile : discardable) {
                            if (cards.get(mov.getPosPlayed()[0]).getName().equals(dTile))
                                return mov;
                        }
                    }
                }
            }else{
                justDiscarded = true;
                for (SaboteurMove mov : moves) {
                    if (mov.getCardPlayed().getName().equals("Drop")) {
                        return mov;
                    }
                }
            }
        }


        //Entered critical section
        else{
                //Aggressive strategy chosen, if malus in hand and path close to gold, use it
                if (ownsMalus != null){
                    justDiscarded = false;
                    return (new SaboteurMove(ownsMalus, 0, 0, myTurn));
                }else{
                    //Check if we can make any advantageous move, if so: Make it. Else, if still have discardable cards, discard them
                    if(!verticalTiles.isEmpty()) {
                        for (SaboteurMove mov : moves) {
                            if (mov.getPosPlayed()[0] > 8) {
                                if (Math.abs(mov.getPosPlayed()[1] - goldCoord[1]) <= 1) {
                                    justDiscarded = false;
                                    return mov;
                                }
                            }
                        }
                    }else{
                        for (SaboteurMove mov : moves){
                            if (mov.getPosPlayed()[0] > 8 && mov.getPosPlayed()[0] < 12 && Math.abs(mov.getPosPlayed()[1] - goldCoord[1]) <= 1 && (mov.getCardPlayed().getName().equals("Tile:8") || mov.getCardPlayed().getName().equals("Tile:0") || mov.getCardPlayed().getName().equals("Tile:6") || mov.getCardPlayed().getName().equals("Tile:6_flip") )) {
                                justDiscarded = false;
                                return mov;
                            }
                        }
                        if (!discardInHand.isEmpty()){
                            justDiscarded = true;
                            for (SaboteurMove mov : moves) {
                                if (mov.getCardPlayed().getName().equals("Drop")) {
                                    for (String dTile : discardable) {
                                        if (cards.get(mov.getPosPlayed()[0]).getName().equals(dTile))
                                            return mov;
                                    }
                                }
                            }
                        }else{
                            justDiscarded = true;
                            for (SaboteurMove mov : moves) {
                                if (mov.getCardPlayed().getName().equals("Drop")) {
                                    return mov;
                                }
                            }
                        }
                    }
                }
            }
        }
        //If all fails, just make a random move!
        return boardState.getRandomMove();
    }
}
