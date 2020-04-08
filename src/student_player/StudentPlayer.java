package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {


    public StudentPlayer() {
        super("260784819");
    }

//let's make one each and we'll just delete the other's constructor before submitting!
/*
    public StudentPlayer() {
*        super("xxxxxxxxx");
*    }
*/
    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        MyTools.getSomething();

        // Is random the best you can do?
        Move myMove = boardState.getRandomMove();

        // Return your move to be processed by the server.
        return myMove;
    }
}