package student_player;

import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDestroy;
import Saboteur.cardClasses.SaboteurMalus;
import boardgame.Move;
import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;
import java.util.ArrayList;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    public StudentPlayer() {
        super("260784819 & 260776911");
    }

    //add global variable for probability of discarding a card
    // will increase with tile placements, and decrease with turn number

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState boardState) {
        int myTurn = boardState.getTurnPlayer();
        boolean canDiscard = boardState.getTurnNumber() < 42;
        boolean hasMalus = boardState.getNbMalus(myTurn) > 0;
        SaboteurCard ownsMalus = null;
        ArrayList<SaboteurCard> hand = boardState.getCurrentPlayerCards();
        //create arraylists of int containing indx for diff cart types
        //for our strat, let's consider destroys like dead end tiles. There are in total 12/56 cards that are discardable
        System.out.println(boardState.getHiddenIntBoard());
        for(SaboteurCard card : hand){
            if(card instanceof SaboteurBonus && hasMalus)
                return(new SaboteurMove(card, 0, 0, myTurn));
            else if (card instanceof SaboteurMalus)
                ownsMalus = card;
            else if(card instanceof SaboteurDestroy)
                continue;

        }
        //if enters the condition, then has malus and does not have a bonus
        if(hasMalus){

        }
        if(canDiscard){

        }else{
            if(ownsMalus != null)
                return(new SaboteurMove(card, 0, 0, myTurn));
        }

        System.out.println();
        MyTools.getSomething();

        // Is random the best you can do?
        Move myMove = boardState.getRandomMove();

        // Return your move to be processed by the server.
        return myMove;
    }
}
