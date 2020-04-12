package student_player;

import Saboteur.SaboteurMove;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

import java.util.ArrayList;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {
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
        ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        return  moves.get(0);
    }

}
