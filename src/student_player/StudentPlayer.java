package student_player;

import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;
import boardgame.Move;
import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

import java.util.*;

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
    public Move chooseMove(SaboteurBoardState boardState) {
        int myTurn = boardState.getTurnPlayer();
        boolean canDiscard = boardState.getTurnNumber() < 42;
        boolean hasMalus = boardState.getNbMalus(myTurn) > 0;
        SaboteurCard ownsMalus = null;
        ArrayList<SaboteurCard> hand = boardState.getCurrentPlayerCards();
        //ArrayLists containing the index of cards in hand
        ArrayList<Integer> discardInHand = new ArrayList<>();
        ArrayList<Integer> usefulTiles = new ArrayList<>();
        SaboteurTile[][] boardThisRound = boardState.getHiddenBoard();
        //first thing to do, check if we need to check any objectives that would have been revealed by the map
        for (int i = 0; i < 2; i++) {
            SaboteurTile objective = boardThisRound[this.hiddenCoordinates[i][0]][this.hiddenCoordinates[i][1]];
            if (objective.getIdx().equals("nugget")) {
                this.hidden[i] = 1;
                this.foundGold = true;
            } else
                this.hidden[i] = 0;
        }
        //Get information on current hand
        // Priority on two moves: 1- If own bonus and have malus then that will be our move
        //  2- If own map and still have not found the gold, then that will be our move.
        for (SaboteurCard card : hand) {
            if (card instanceof SaboteurBonus && hasMalus)
                return (new SaboteurMove(card, 0, 0, myTurn));
            else if (card instanceof SaboteurMalus)
                ownsMalus = card;
            else if (card instanceof SaboteurTile) {
                if (discardable.contains(((SaboteurTile) card).getIdx()))
                    discardInHand.add(hand.indexOf(card));
                else usefulTiles.add(hand.indexOf(card));
            } else if (card instanceof SaboteurDestroy)
                discardInHand.add(hand.indexOf(card));
            else if (card instanceof SaboteurMap) {
                if (!this.foundGold) {
                    for (int i = 0; i < 2; i++) {
                        if (this.hidden[i] == -1)
                            return (new SaboteurMove(card, this.hiddenCoordinates[i][0], this.hiddenCoordinates[i][1], myTurn));
                    }
                }
            }
        }
        //if we enter this condition, then have malus and own no bonus nor map.
        // Two moves possible: 1- Counter with malus if own one
        //  2- Discard a card: if have discardable card in hand then drop it/first one if have many,
        //                      else discard first one in hand
        if (hasMalus) {
            if (ownsMalus != null)
                return (new SaboteurMove(ownsMalus, 0, 0, myTurn));
            else if (!discardInHand.isEmpty())
                return (new SaboteurMove(hand.get(discardInHand.get(0)), 0, 0, myTurn));
            else
                return (new SaboteurMove(hand.get(0), 0, 0, myTurn));
        }
        //Check if we are still at a turn where we can discard and get another card
        else if (canDiscard) {
            double probabilityOfDiscarding = -Math.sqrt(boardState.getTurnNumber() + numberOfCardsDiscarded - numberOfTilesPlaced - discardInHand.size()) / 1764 + 1;
            double probabilityOfPlacing = Math.sqrt(boardState.getTurnNumber() - numberOfTilesPlaced + numberOfCardsDiscarded + usefulTiles.size())/1764;
            //choose whether to discard an undesired card probabilistically
            //TODO: have to check if we can make any advantageous move, else just discard
            //TODO: once we are able to do distance calculations, check if path close to gold
            //                                                           if so, then check if have malus, if so use it, else MAKE a move
            if(probabilityOfDiscarding >= probabilityOfPlacing && justDiscarded==false && !discardInHand.isEmpty())
                return(new SaboteurMove(hand.get(discardInHand.get(0)), 0, 0, myTurn));
            else{
                //No card to discard, under no malus, no bonus to use, so make a move!!
                //TODO: MAKE THE MOVE: 1- if gold known, towards gold. 2- if gold not known, towards closest unknown objective
            }
        }
        //Cannot discard anymore
        else {
            //Aggressive strategy chosen, if malus in hand and path close to gold, use it
            if (ownsMalus != null)
                return (new SaboteurMove(ownsMalus, 0, 0, myTurn));
            else{
                //TODO:Check if we can make any advantageous move, if so: Make it. Else, if still have discardable cards, discard them
            }
        }

        MyTools.getSomething();
        //If all fails, just make a random move!
        Move myMove = boardState.getRandomMove();

        // Return your move to be processed by the server.
        return myMove;
    }
}
