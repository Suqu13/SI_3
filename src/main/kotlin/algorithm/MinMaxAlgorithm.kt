package algorithm

import engine.Board
import engine.State
import java.lang.Integer.min
import kotlin.math.max

class MinMaxAlgorithm(private val maximizingPlayerState: State, private val minimizingPlayerState: State, private val heuristic: Heuristic = ScoreContentOfFourHeuristic()) {

    private fun checkIfTerminalNodeOccurred(board: Board): Boolean =
        board.winner != null || board.findPossibleMoves().isEmpty()

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
            return Pair(-1, heuristic.score(board, maximizingPlayerState, minimizingPlayerState))
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