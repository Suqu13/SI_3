package algorithm

import engine.Board
import engine.State

val EVALUATION_TABLE = arrayOf(
    intArrayOf(3, 4, 5, 7, 5, 4, 3),
    intArrayOf(4, 6, 8, 10, 8, 6, 4),
    intArrayOf(5, 8, 11, 13, 11, 8, 5),
    intArrayOf(5, 8, 11, 13, 11, 8, 5),
    intArrayOf(4, 6, 8, 10, 8, 6, 4),
    intArrayOf(3, 4, 5, 7, 5, 4, 3)
)

class PromoteCenteredCellsHeuristic : Heuristic {

    override fun score(board: Board, state: State, opponentState: State): Int =
        board.obtainArraysOfFour().map { scoreFour(state, opponentState, it) }.sum() + scoreCenteredMoves(
            board,
            state,
            opponentState
        )

    private fun scoreCenteredMoves(board: Board, state: State, opponentState: State): Int {
        var sum = 138
        board.cells.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, cell ->
                if (cell == state) sum += EVALUATION_TABLE[rowIndex][columnIndex]
                else if (cell == opponentState) sum -= EVALUATION_TABLE[rowIndex][columnIndex]
            }
        }
        return sum
    }
}