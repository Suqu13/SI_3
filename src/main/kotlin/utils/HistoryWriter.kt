package utils

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import engine.State
import gui.DifficultyLevel
import gui.HeuristicKind
import java.time.LocalDateTime

class HistoryWriter {
    private var moves = arrayListOf<Triple<Int, Long, State>>()
    private val writer = csvWriter {
        delimiter = '\n'
        lineTerminator = "\n\n"
    }

    fun clearHistory() {
        moves = arrayListOf()
    }

    fun addToHistory(state: State, function: () -> Pair<Int, Int?>): Int {
        val startTime = System.currentTimeMillis()
        val result = function.invoke()
        val endTime = System.currentTimeMillis()
        moves.add(Triple(result.first, endTime - startTime, state))
        return result.first
    }

    fun prepareGameStats(firstState: State, secondState: State, winner: State?): Pair<StateResult, StateResult> {
        val firstStateMoves = moves.filter { it.third == firstState }
        val secondStateMoves = moves.filter { it.third == secondState }

        val firstStateResult = StateResult(
            firstState,
            firstStateMoves.size,
            firstStateMoves.map { it.second }.sum(),
            firstState == winner
        )
        val secondStateResult = StateResult(
            secondState,
            secondStateMoves.size,
            secondStateMoves.map { it.second }.sum(),
            secondState == winner
        )

        return Pair(firstStateResult, secondStateResult)
    }

    fun writeHistoryToFile(
        winner: State?,
        cells: Array<Array<State>>,
        firstAiLevel: DifficultyLevel,
        secondAiLevel: DifficultyLevel,
        firstHeuristicKind: HeuristicKind,
        secondHeuristicKind: HeuristicKind,
        firstState: State,
        secondState: State
    ) {
        writer.open("${winner?.name}_${firstAiLevel.name}/${firstHeuristicKind.name}-${secondAiLevel.name}/${secondHeuristicKind.name}_${LocalDateTime.now()}.csv") {
            writeRow(moves.filter { it.third == firstState })
            writeRow(moves.filter { it.third == secondState })
            writeRow(
                cells.map { row -> row.joinToString(prefix = "[", postfix = "]") { it.token } }
            )
        }
    }
}

data class StateResult(val state: State, val movesNumber: Int, val movesTime: Long, val isWinner: Boolean)