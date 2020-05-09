package gui

import algorithm.MinMaxAlgorithm
import engine.Board
import engine.State
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.*
import utils.HistoryWriter
import kotlin.random.Random

class PageController : Controller() {
    private lateinit var board: Board

    private lateinit var firstMinMaxAlgorithm: MinMaxAlgorithm
    private lateinit var secondMinMaxAlgorithm: MinMaxAlgorithm

    lateinit var firstPlayer: State
    lateinit var secondPlayer: State
    lateinit var firstDifficultyLevel: DifficultyLevel
    lateinit var secondDifficultyLevel: DifficultyLevel

    var cells: ObservableList<Array<State>> = observableListOf()
    val disabled = SimpleBooleanProperty(true)
    val started = SimpleBooleanProperty(false)
    val mainButtonLabel = SimpleStringProperty("Start")
    val playerMove = SimpleStringProperty(PlayerMove.NOT_STARTED.message)
    val resultMessage = SimpleStringProperty("")

    private var currentPlayerState = SimpleObjectProperty<State>()
    private var futurePlayerState = SimpleObjectProperty<State>()

    private val historyWriter = HistoryWriter()

    private var randomMovesCounter = 0
    private var maxRandomMovesCounter = 0

    private fun observeForPlayerStateChanged() {
        currentPlayerState.onChange {
            if (!checkIfGameFinished()) {
                when (currentPlayerState.get()) {
                    State.FIRST_AI_PLAYER -> onAIMove(
                        PlayerMove.FIRST_AI_MOVE,
                        State.FIRST_AI_PLAYER,
                        firstMinMaxAlgorithm,
                        firstDifficultyLevel.depth
                    )
                    State.SECOND_AI_PLAYER -> onAIMove(
                        PlayerMove.SECOND_AI_MOVE,
                        State.SECOND_AI_PLAYER,
                        secondMinMaxAlgorithm,
                        secondDifficultyLevel.depth
                    )
                    State.FIRST_PLAYER -> onPlayerMove(PlayerMove.FIRST_PLAYER_MOVE)
                    State.SECOND_PLAYER -> onPlayerMove(PlayerMove.SECOND_PLAYER_MOVE)
                    else -> {
                    }
                }
            } else {
                historyWriter.writeHistoryToFile(
                    board.winner,
                    board.cells,
                    firstDifficultyLevel,
                    secondDifficultyLevel,
                    firstPlayer,
                    secondPlayer
                )
                historyWriter.clearHistory()
            }
        }
    }

    private fun checkIfGameFinished(): Boolean {
        val winner = board.winner != null
        val draw = board.checkForDraw()
        if (winner) {
            resultMessage.set(board.winner!!.winnerMessage)
            started.set(false)
        } else if (draw) {
            resultMessage.set("DRAW!")
            started.set(false)
        }
        return winner || draw
    }

    private fun chooseCurrentAndFuturePlayer(): Pair<State, State> {
        return if (Random.nextBoolean())
            Pair(firstPlayer, secondPlayer)
        else
            Pair(secondPlayer, firstPlayer)
    }

    private fun updateBoard(columnNum: Int, state: State): Boolean {
        val correctness = board.move(columnNum, state)
        if (correctness) {
            cells.setAll(board.cells.toList())
        }
        return correctness
    }

    fun initialize() {
        observeForPlayerStateChanged()
        cells.setAll(*Array(6) { Array(7) { State.EMPTY } })
    }


    private fun onAIMove(move: PlayerMove, state: State, minMaxAlgorithm: MinMaxAlgorithm, depth: Int) {
        disabled.set(true)
        playerMove.set(move.message)
        tornadofx.runAsync {
            historyWriter.addToHistory(state) {
                if (randomMovesCounter < maxRandomMovesCounter) {
                    randomMovesCounter++
                    Pair(Random.nextInt(7), 0)
                } else {
                    minMaxAlgorithm.minimax(
                        board,
                        depth,
                        Int.MIN_VALUE,
                        Int.MAX_VALUE
                    )
                }
            }
        } ui {
            updateBoard(it, state)
            rewriteCurrentAndFuturePlayer()
        }
    }

    private fun onPlayerMove(move: PlayerMove) {
        disabled.set(false)
        playerMove.set(move.message)
    }

    fun playerOnChange(name: String, playerState: State, aiState: State, setPlayer: (state: State) -> Unit) {
        setPlayer(
            when (name) {
                "AI" -> aiState
                else -> playerState
            }
        )
    }

    private fun createEnvironment() {
        if (this::firstPlayer.isInitialized && this::secondPlayer.isInitialized) {
            if (this::firstDifficultyLevel.isInitialized && this::secondDifficultyLevel.isInitialized && this::firstPlayer.isInitialized && this::secondPlayer.isInitialized) {
                firstMinMaxAlgorithm = MinMaxAlgorithm(State.FIRST_AI_PLAYER, State.SECOND_AI_PLAYER)
                secondMinMaxAlgorithm = MinMaxAlgorithm(State.SECOND_AI_PLAYER, State.FIRST_AI_PLAYER)
                start()
            } else if (firstPlayer == State.FIRST_AI_PLAYER && this::firstDifficultyLevel.isInitialized && secondPlayer != State.SECOND_AI_PLAYER) {
                firstMinMaxAlgorithm = MinMaxAlgorithm(State.FIRST_AI_PLAYER, State.SECOND_AI_PLAYER)
                start()
            } else if (firstPlayer != State.FIRST_AI_PLAYER && secondPlayer == State.SECOND_AI_PLAYER && this::secondDifficultyLevel.isInitialized) {
                secondMinMaxAlgorithm = MinMaxAlgorithm(State.SECOND_AI_PLAYER, State.FIRST_AI_PLAYER)
                start()
            } else if (firstPlayer == State.FIRST_PLAYER && secondPlayer == State.SECOND_PLAYER) {
                start()
            }
        }
    }

    private fun getMaxRandomMovesCounter(firstState: State, secondState: State): Int {
        var counter = 0
        if (firstState == State.FIRST_AI_PLAYER || firstState == State.SECOND_AI_PLAYER)
            counter++
        if (secondState == State.FIRST_AI_PLAYER || secondState == State.SECOND_AI_PLAYER)
            counter++
        return counter
    }

    private fun start() {
        currentPlayerState.set(null)
        futurePlayerState.set(null)
        started.set(true)
        mainButtonLabel.set("Restart!")
        val (currentPlayer, futurePlayer) = chooseCurrentAndFuturePlayer()
        maxRandomMovesCounter = getMaxRandomMovesCounter(currentPlayer, futurePlayer)
        randomMovesCounter = 0
        futurePlayerState.set(futurePlayer)
        currentPlayerState.set(currentPlayer)
    }

    fun restartOnClick() {
        historyWriter.clearHistory()
        resultMessage.set("")
        board = Board()
        cells.setAll(board.cells.toList())
        createEnvironment()
    }

    private fun rewriteCurrentAndFuturePlayer() {
        val currentPlayer = currentPlayerState.get()
        val futurePlayer = futurePlayerState.get()
        futurePlayerState.set(currentPlayer)
        currentPlayerState.set(futurePlayer)
    }

    fun moveOnClick(columnNum: Int) {
        if (updateBoard(columnNum, currentPlayerState.get())) {
            rewriteCurrentAndFuturePlayer()
        }
    }
}




