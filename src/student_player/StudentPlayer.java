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
    public static int enemyID;
    // -1 : DN
    // 0: No
    // 1: Yes
    public static int[] isGold = {-1, -1, -1};

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
        for (SaboteurCard card : cards)
            System.out.println("Testing: " + card.getName());
        for (SaboteurMove mov : moves)
            System.out.println("Testing: " + mov.toPrettyString());
        return moves.get(AIDecision(boardState));
    }

    // simple greedy approach
    private int AIDecision(SaboteurBoardState boardState){
        SaboteurCard cardSelected = null;

        // if in malus state, use the bonus card at once if possible
        if (boardState.getNbMalus(boardState.getTurnPlayer()) > 0){
            for (SaboteurCard card : this.cards) {
                if (card instanceof SaboteurBonus)
                    cardSelected = card;
            }
            for (SaboteurMove mov : this.moves){
                if(mov.getCardPlayed().getName()== cardSelected.getName()) {
                    return this.moves.indexOf(mov);
                }
            }
        }

        // if the malus card is in hand, play it at once to reduce the chance of the random player messing up the path
        for (SaboteurCard card : this.cards) {
            if (card instanceof SaboteurMalus)
                cardSelected = card;
        }
        for (SaboteurMove mov : this.moves){
            if(mov.getCardPlayed().getName() == cardSelected.getName()){
                return this.moves.indexOf(mov);
            }
        }
//
//        // if the gold card has not yet been revealed, use the map card if possible
//        if (!goldTileRevealed){
//            // check if the map card is hand
//            for (SaboteurCard card : cards) {
//                if (card instanceof SaboteurMap)
//                    cardSelected = card;
//            }
//            // 3, 5, 7
//            for (SaboteurMove mov : moves) {
//                if (mov.getCardPlayed().equals(cardSelected))
//                    return moves.indexOf(mov);
//            }
////            if (boardState.getBoardForDisplay()[12][3].getName() == "goalTile"){
////                for (SaboteurMove mov : moves) {
////                    // use the map card at the hidden object at once
////                    if (mov.getCardPlayed().equals(cardSelected) && mov.getPosPlayed()[1] == 3) {
////                        goldTileRevealed = true;
////                        isGold[0] = true;
////                        return moves.indexOf(mov);
////                    }
////                }
////            }
////            else if (boardState.getBoardForDisplay()[12][5].getName() == "goalTile"){
////                for (SaboteurMove mov : moves) {
////                    // use the map card at the hidden object at once
////                    if (mov.getCardPlayed().equals(cardSelected) && mov.getPosPlayed()[1] == 5) {
////                        goldTileRevealed = true;
////                        isGold[1] = true;
////                        return moves.indexOf(mov);
////                    }
////                }
////            }
////            else if (boardState.getBoardForDisplay()[12][7].getName() == "goalTile") {
////                for (SaboteurMove mov : moves) {
////                    // use the map card at the hidden object at once
////                    if (mov.getCardPlayed().equals(cardSelected) && mov.getPosPlayed()[1] == 7) {
////                        goldTileRevealed = true;
////                        isGold[2] = true;
////                        return moves.indexOf(mov);
////                    }
////                }
////            }
//        }
//
////        // gold card is revealed
////        else {
////            System.out.println("Drop Map!!!!!!!!!");
////            // drop all map cards in hand
////            for (SaboteurCard card : cards) {
////                if (card instanceof SaboteurMap)
////                    cardSelected = card;
////            }
////            for (SaboteurMove mov : moves) {
////                if (mov.getCardPlayed() instanceof SaboteurDrop && mov.getPosPlayed()[0] == cards.indexOf(cardSelected))
////                    return moves.indexOf(mov);
////            }
////        }
//
//        // drop dead end cards in hand
//        // greedy building the calculated path
        return 0;
    }



}
