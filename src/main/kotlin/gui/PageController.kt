package gui

import algorithm.MinMaxAlgorithm
import engine.Board
import engine.State
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.*
import kotlin.random.Random

class PageController : Controller() {
    private lateinit var board: Board
    private lateinit var difficultyLevel: DifficultyLevel
    private lateinit var minMaxAlgorithm: MinMaxAlgorithm

    var cells: ObservableList<Array<State>> = observableListOf()
    val disabled = SimpleBooleanProperty(true)
    val started = SimpleBooleanProperty(false)
    val mainButtonLabel = SimpleStringProperty("Start")
    val playerMove = SimpleStringProperty(PlayerMove.NOT_STARTED.message)
    val resultMessage = SimpleStringProperty("")

    private var currentPlayerState = SimpleObjectProperty<State>()
    private var futurePlayerState = SimpleObjectProperty<State>()

    private fun observeForPlayerStateChanged() {
        currentPlayerState.onChange {
            val winner = board.winner
            when {
                winner != null -> {
                    resultMessage.set(winner.winnerMessage)
                    started.set(false)
                }
                board.checkForDraw() -> {
                    resultMessage.set("DRAW!")
                    started.set(false)
                }
                else -> {
                    when (currentPlayerState.get()) {
                        State.AI_PLAYER -> onSecondPlayerMove()
                        State.FIRST_PLAYER -> onFirstPlayerMove()
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun chooseCurrentAndFuturePlayer(): Pair<State, State> {
        return if (Random.nextBoolean())
            Pair(State.FIRST_PLAYER, State.AI_PLAYER)
        else
            Pair(State.AI_PLAYER, State.FIRST_PLAYER)
    }

    private fun updateBoard(columnNum: Int, state: State): Boolean {
        val correctness = board.move(columnNum, state)
        if(correctness) {
            cells.setAll(board.cells.toList())
        }
        return correctness
    }

    fun initialize() {
        board = Board()
        observeForPlayerStateChanged()
        cells.setAll(board.cells.toList())
    }

    private fun onSecondPlayerMove() {
        disabled.set(true)
        playerMove.set(PlayerMove.OPPONENT_MOVE.message)
        tornadofx.runAsync { minMaxAlgorithm.minimax(board, difficultyLevel.depth).first } ui {
            updateBoard(it, State.AI_PLAYER)
            rewriteCurrentAndFuturePlayer()
        }
    }

    private fun onFirstPlayerMove() {
        disabled.set(false)
        playerMove.set(PlayerMove.YOUR_MOVE.message)
    }

    fun difficultyLevelOnChange(level: DifficultyLevel) {
        difficultyLevel = level
    }

    fun startOnClick() {
        if (this::difficultyLevel.isInitialized) {
            started.set(true)
            mainButtonLabel.set("Restart!")
            minMaxAlgorithm = MinMaxAlgorithm(State.AI_PLAYER, State.FIRST_PLAYER)
            val (currentPlayer, futurePlayer) = chooseCurrentAndFuturePlayer()
            currentPlayerState.set(currentPlayer)
            futurePlayerState.set(futurePlayer)
        }
    }

    fun restartOnClick() {
        resultMessage.set("")
        board = Board()
        cells.setAll(board.cells.toList())
        minMaxAlgorithm = MinMaxAlgorithm(State.AI_PLAYER, State.FIRST_PLAYER)
        val (currentPlayer, futurePlayer) = chooseCurrentAndFuturePlayer()
        currentPlayerState.set(currentPlayer)
        futurePlayerState.set(futurePlayer)
    }

    private fun rewriteCurrentAndFuturePlayer() {
        val currentPlayer = currentPlayerState.get()
        val futurePlayer = futurePlayerState.get()
        futurePlayerState.set(currentPlayer)
        currentPlayerState.set(futurePlayer)
    }

    fun moveOnClick(columnNum: Int) {
        if (updateBoard(columnNum, currentPlayerState.get())) {
            playerMove.set(PlayerMove.OPPONENT_MOVE.message)
            rewriteCurrentAndFuturePlayer()
        }
    }
}




