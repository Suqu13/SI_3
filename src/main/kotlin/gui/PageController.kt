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
    val winner = SimpleStringProperty("")

    private var currentPlayerState = SimpleObjectProperty<State>()

    private fun observeForPlayerStateChanged() {
        currentPlayerState.onChange {
            if (board.winner != null){
                winner.set(board.winner!!.winnerMessage)
            } else {
                when (currentPlayerState.get()) {
                    State.AI_PLAYER -> onSecondPlayerMove()
                    State.FIRST_PLAYER -> onFirstPlayerMove()
                    else -> {
                    }
                }
            }
        }
    }

    private fun chooseFirstPlayer(): State {
        return if (Random.nextBoolean())
            State.FIRST_PLAYER
        else
            State.AI_PLAYER
    }

    private fun updateBoard(columnNum: Int, state: State) {
        board.move(columnNum, state)
        cells.setAll(board.cells.toList())
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
            currentPlayerState.set(State.FIRST_PLAYER)
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
            currentPlayerState.set(chooseFirstPlayer())
        }
    }

    fun restartOnClick() {
        winner.set("")
        board = Board()
        cells.setAll(board.cells.toList())
        minMaxAlgorithm = MinMaxAlgorithm(State.AI_PLAYER, State.FIRST_PLAYER)
        currentPlayerState.set(chooseFirstPlayer())
    }

    fun moveOnClick(columnNum: Int) {
        updateBoard(columnNum, State.FIRST_PLAYER)
        playerMove.set(PlayerMove.OPPONENT_MOVE.message)
        currentPlayerState.set(State.AI_PLAYER)
    }
}




