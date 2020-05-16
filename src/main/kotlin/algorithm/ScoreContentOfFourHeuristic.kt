package algorithm

import engine.Board
import engine.State

class ScoreContentOfFourHeuristic : Heuristic {

    override fun score(board: Board, state: State, opponentState: State): Int =
        board.obtainArraysOfFour().map { scoreFour(state, opponentState, it) }.sum()
}