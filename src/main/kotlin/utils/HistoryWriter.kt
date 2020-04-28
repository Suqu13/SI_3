package utils

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import engine.State
import gui.DifficultyLevel
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

    fun writeHistoryToFile(
        winner: State?,
        cells: Array<Array<State>>,
        firstAiLevel: DifficultyLevel,
        secondAiLevel: DifficultyLevel,
        firstState: State,
        secondState: State
    ) {
        writer.open("${winner?.name}_${firstAiLevel.name}-${secondAiLevel.name}_${LocalDateTime.now()}.csv") {
            writeRow(moves.filter { it.third == firstState })
            writeRow(moves.filter { it.third == secondState })
            writeRow(
                cells.map { row -> row.joinToString(prefix = "[", postfix = "]") { it.token } }
            )
        }
    }
}