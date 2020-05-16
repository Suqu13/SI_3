package algorithm

import engine.Board
import engine.State

interface Heuristic {
    fun score(board: Board, state: State, opponentState: State): Int

    fun scoreFour(state: State, opponentState: State, arrayOfFourStates: Array<State>): Int {
        val stateCount = arrayOfFourStates.count { it == state }
        val opponentStateCount = arrayOfFourStates.count { it == opponentState }
        val emptyStateCount = arrayOfFourStates.count { it == State.EMPTY }

        return if (stateCount == 4) 400
        else if (stateCount == 3 && emptyStateCount == 1) 5
        else if (stateCount == 2 && emptyStateCount == 2) 2
        else if (opponentStateCount == 3 && emptyStateCount == 1) -5
        else if (opponentStateCount == 4) -400
        else 0
    }
}