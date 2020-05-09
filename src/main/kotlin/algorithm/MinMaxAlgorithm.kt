package algorithm

import engine.Board
import engine.State
import java.lang.Integer.min
import kotlin.math.max

class MinMaxAlgorithm(private val maximizingPlayerState: State, private val minimizingPlayerState: State) {

    private fun checkIfTerminalNodeOccurred(board: Board): Boolean =
        board.winner != null || board.findPossibleMoves().isEmpty()

    private fun scoreFour(state: State, opponentState: State, arrayOfFourStates: Array<State>): Int {
        val stateCount = arrayOfFourStates.count { it == state }
        val opponentStateCount = arrayOfFourStates.count { it == opponentState }
        val emptyStateCount = arrayOfFourStates.count { it == State.EMPTY }

        return if (stateCount == 4) 500
        else if (stateCount == 3 && emptyStateCount == 1) 5
        else if (stateCount == 2 && emptyStateCount == 2) 2
        else if (opponentStateCount == 3 && emptyStateCount == 1) -5
        else 0
    }

    private fun score(board: Board, state: State, opponentState: State): Int =
        board.obtainArraysOfFour().map { scoreFour(state, opponentState, it) }.sum()

    //  returns pair where first -> columnNum, second -> actualScore
    fun minimax(
        board: Board,
        depth: Int,
        alpha: Int,
        beta: Int,
        alphaBeta: Boolean = true,
        maximizingPlayer: Boolean = true
    ): Pair<Int, Int> {
        if (depth == 0) {
            return Pair(-1, score(board, maximizingPlayerState, minimizingPlayerState))
        } else if (checkIfTerminalNodeOccurred(board)) {
            if (board.winner == maximizingPlayerState)
                return Pair(-1, Int.MAX_VALUE)
            if (board.winner == minimizingPlayerState)
                return Pair(-1, Int.MIN_VALUE)
            return Pair(-1, 0)
        }

        var value: Int
        val possibleMoves = board.findPossibleMoves()
        var columnNum = possibleMoves.random()

        if (maximizingPlayer) {
            value = Int.MIN_VALUE
            for (it in possibleMoves) {
                val boardCopy = board.copy()
                boardCopy.move(it, maximizingPlayerState)
                val (_, score) = minimax(boardCopy, depth - 1, alpha, beta, alphaBeta, false)
                if (score > value) {
                    value = score
                    columnNum = it
                }
                if (alphaBeta && max(alpha, value) >= beta)
                    break
            }
            return Pair(columnNum, value)
        } else {
            value = Int.MAX_VALUE
            for (it in possibleMoves) {
                val boardCopy = board.copy()
                boardCopy.move(it, minimizingPlayerState)
                val (_, score) = minimax(boardCopy, depth - 1, alpha, beta, alphaBeta, true)
                if (score < value) {
                    value = score
                    columnNum = it
                }
                if (alphaBeta && alpha >= min(beta, value))
                    break
            }
            return Pair(columnNum, value)
        }
    }


}