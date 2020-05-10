package search

import algorithm.MinMaxAlgorithm
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import engine.Board

import engine.State
import utils.HistoryWriter
import utils.StateResult
import kotlin.random.Random

class SearchEngine(
    private val firstPlayerDepth: Int,
    private val secondPlayerDepth: Int,
    private val alphaBeta: Boolean,
    private val gamesNumber: Int
) {

    private val resultPerGame = arrayListOf<Pair<StateResult, StateResult>>()
    private val historyWriter = HistoryWriter()
    private val writer = csvWriter {
        delimiter = '\n'
        lineTerminator = "\n\n"
    }

    fun runGames() {
        (0 until gamesNumber).forEach { _ ->
            runSingleGame()
        }
        val winnersStats = countWinnerStats()
        val globalStats = countGlobalStats()

        saveResultToFile(winnersStats, globalStats)
    }

    private fun saveResultToFile(
        winnerStats: Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>>,
        globalStats: Pair<Pair<Double, Double>, Pair<Double, Double>>
    ) {
        val firstAIWins = "First AI wins: " + winnerStats.first.third
        val firstAIWinnerAVGMoves = "First AI as winner AVG moves: " + winnerStats.first.first
        val firstAIWinnerAVGTime = "First AI as winner AVG time: " + winnerStats.first.second

        val secondAIWins = "Second AI wins: " + winnerStats.second.third
        val secondAIWinnerAVGMoves = "Second AI as winner AVG moves: " + winnerStats.second.first
        val secondAIWinnerAVGTime = "Second AI as winner AVG time: " + winnerStats.second.second

        val firstAIAVGMoves = "First AI AVG moves: " + globalStats.first.first
        val firstAIAVGTime = "First AI AVG time: " + globalStats.first.second

        val secondAIAVGMoves = "Second AI AVG moves: " + globalStats.second.first
        val secondAIAVGTime = "Second AI AVG time: " + globalStats.second.second

        writer.open("${firstPlayerDepth}-${secondPlayerDepth}_alphaBeta-${alphaBeta}.csv") {
            writeRow(
                listOf(
                    firstAIWins,
                    secondAIWins,
                    firstAIWinnerAVGMoves,
                    firstAIWinnerAVGTime,
                    secondAIWinnerAVGMoves,
                    secondAIWinnerAVGTime,
                    firstAIAVGMoves,
                    firstAIAVGTime,
                    secondAIAVGMoves,
                    secondAIAVGTime
                )
            )

        }
    }

    private fun runSingleGame() {
        val board = Board()

        val firstMinMaxAlgorithm = MinMaxAlgorithm(State.FIRST_AI_PLAYER, State.SECOND_AI_PLAYER)
        val secondMinMaxAlgorithm = MinMaxAlgorithm(State.SECOND_AI_PLAYER, State.FIRST_AI_PLAYER)

        var isFirstMoveOfFirstAI = true
        var isFirstMoveOfSecondAI = true

        var currentState = State.FIRST_AI_PLAYER

        while (board.winner == null && !board.checkForDraw()) {
            if (currentState == State.FIRST_AI_PLAYER) {
                board.move(historyWriter.addToHistory(currentState) {
                    if (isFirstMoveOfFirstAI) {
                        isFirstMoveOfFirstAI = false
                        Pair(Random.nextInt(7), 0)
                    } else {
                        firstMinMaxAlgorithm.minimax(
                            board,
                            firstPlayerDepth,
                            Int.MIN_VALUE,
                            Int.MAX_VALUE,
                            alphaBeta
                        )
                    }
                }, currentState)
                currentState = State.SECOND_AI_PLAYER
            } else {
                board.move(historyWriter.addToHistory(currentState) {
                    if (isFirstMoveOfSecondAI) {
                        isFirstMoveOfSecondAI = false
                        Pair(Random.nextInt(7), 0)
                    } else {
                        secondMinMaxAlgorithm.minimax(
                            board,
                            secondPlayerDepth,
                            Int.MIN_VALUE,
                            Int.MAX_VALUE,
                            alphaBeta
                        )
                    }
                }, currentState)
                currentState = State.FIRST_AI_PLAYER
            }
        }
        val gameResult = historyWriter.prepareGameStats(State.FIRST_AI_PLAYER, State.SECOND_AI_PLAYER, board.winner)
        resultPerGame.add(gameResult)
        historyWriter.clearHistory()
    }

    private fun findWinner(results: Pair<StateResult, StateResult>): StateResult? {
        return when {
            results.first.isWinner -> {
                results.first
            }
            results.second.isWinner -> {
                results.second
            }
            else -> {
                null
            }
        }
    }

    private fun countAVGTime(res: Pair<Int, Long>): Double {
        return try {
            val (moves, time) = res
            (time.toDouble() / moves)
        } catch (e: Exception) {
            0.0
        }
    }

    private fun countAVGTime(res: Triple<Int, Long, Int>): Double {
        return try {
            val (moves, time, _) = res
            (time.toDouble() / moves)
        } catch (e: Exception) {
            0.0
        }
    }

    private fun countAVGMoves(res: Pair<Int, Long>, gamesNum: Int): Double {
        return try {
            val (moves, _) = res
            (moves.toDouble() / gamesNum)
        } catch (e: Exception) {
            0.0
        }
    }

    private fun countAVGMoves(res: Triple<Int, Long, Int>, gamesNum: Int): Double {
        return try {
            val (moves, _, _) = res
            (moves.toDouble() / gamesNum)
        } catch (e: Exception) {
            0.0
        }
    }

    private fun countGlobalStats(): Pair<Pair<Double, Double>, Pair<Double, Double>> {
        val playersMap = mutableMapOf(
            State.FIRST_AI_PLAYER to Pair(0, 0L),
            State.SECOND_AI_PLAYER to Pair(0, 0L)
        )

        resultPerGame.forEach {
            val (firstMoves, firstTime) = playersMap.getValue(it.first.state)
            val (secondMoves, secondTime) = playersMap.getValue(it.second.state)
            playersMap[it.first.state] = Pair(firstMoves + it.first.movesNumber, firstTime + it.first.movesTime)
            playersMap[it.second.state] = Pair(secondMoves + it.second.movesNumber, secondTime + it.second.movesTime)
        }

        val firstAVGMovesNumber = countAVGMoves(playersMap.getValue(State.FIRST_AI_PLAYER), resultPerGame.size)
        val secondAVGMovesNumber = countAVGMoves(playersMap.getValue(State.SECOND_AI_PLAYER), resultPerGame.size)

        val firstAVGTimePerMovesNumber = countAVGTime(playersMap.getValue(State.FIRST_AI_PLAYER))
        val secondAVGTimePerMovesNumber = countAVGTime(playersMap.getValue(State.SECOND_AI_PLAYER))

        return Pair(
            Pair(firstAVGMovesNumber, firstAVGTimePerMovesNumber),
            Pair(secondAVGMovesNumber, secondAVGTimePerMovesNumber)
        )
    }

    private fun countWinnerStats(): Pair<Triple<Double, Double, Int>, Triple<Double, Double, Int>> {
        val winnerResultsMap = mutableMapOf(
            State.FIRST_AI_PLAYER to Triple(0, 0L, 0),
            State.SECOND_AI_PLAYER to Triple(0, 0L, 0)
        )

        resultPerGame.forEach {
            val winner = findWinner(it)
            if (winner != null) {
                val (moves, time, wins) = winnerResultsMap.getValue(winner.state)
                winnerResultsMap[winner.state] = Triple(moves + winner.movesNumber, time + winner.movesTime, wins + 1)
            }
        }

        val firstAVGMovesNumber = countAVGMoves(
            winnerResultsMap.getValue(State.FIRST_AI_PLAYER),
            winnerResultsMap.getValue(State.FIRST_AI_PLAYER).third
        )
        val secondAVGMovesNumber = countAVGMoves(
            winnerResultsMap.getValue(State.SECOND_AI_PLAYER),
            winnerResultsMap.getValue(State.SECOND_AI_PLAYER).third
        )

        val firstAVGTimePerMovesNumber = countAVGTime(winnerResultsMap.getValue(State.FIRST_AI_PLAYER))
        val secondAVGTimePerMovesNumber = countAVGTime(winnerResultsMap.getValue(State.SECOND_AI_PLAYER))

        val firstWins = winnerResultsMap.getValue(State.FIRST_AI_PLAYER).third
        val secondWins = winnerResultsMap.getValue(State.SECOND_AI_PLAYER).third

        return Pair(
            Triple(firstAVGMovesNumber, firstAVGTimePerMovesNumber, firstWins),
            Triple(secondAVGMovesNumber, secondAVGTimePerMovesNumber, secondWins)
        )
    }
}


fun main() {
    SearchEngine(3, 3, true, 30).runGames()
    SearchEngine(3, 4, true, 30).runGames()
    SearchEngine(3, 5, true, 30).runGames()
    SearchEngine(3, 6, true, 30).runGames()
    SearchEngine(4, 3, true, 30).runGames()
    SearchEngine(5, 3, true, 30).runGames()
    SearchEngine(6, 3, true, 30).runGames()

    SearchEngine(6, 4, true, 30).runGames()
    SearchEngine(6, 5, true, 30).runGames()
    SearchEngine(4, 6, true, 30).runGames()
    SearchEngine(5, 6, true, 30).runGames()
    SearchEngine(6, 6, true, 30).runGames()

    SearchEngine(3, 3, false, 30).runGames()
    SearchEngine(3, 4, false, 30).runGames()
    SearchEngine(3, 5, false, 30).runGames()
    SearchEngine(3, 6, false, 30).runGames()
    SearchEngine(4, 3, false, 30).runGames()
    SearchEngine(5, 3, false, 30).runGames()
    SearchEngine(6, 3, false, 30).runGames()

    SearchEngine(6, 4, false, 30).runGames()
    SearchEngine(6, 5, false, 30).runGames()
    SearchEngine(4, 6, false, 30).runGames()
    SearchEngine(5, 6, false, 30).runGames()
    SearchEngine(6, 6, false, 30).runGames()
}