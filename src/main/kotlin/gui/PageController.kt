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
    private lateinit var firstMinMaxAlgorithm: MinMaxAlgorithm
    private lateinit var secondinMaxAlgorithm: MinMaxAlgorithm

    lateinit var firstPlayer: State
    lateinit var secondPlayer: State


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
                        State.FIRST_AI_PLAYER -> onAIMove(PlayerMove.FIRST_AI_MOVE, State.FIRST_AI_PLAYER, firstMinMaxAlgorithm)
                        State.SECOND_AI_PLAYER -> onAIMove(PlayerMove.SECOND_AI_MOVE, State.SECOND_AI_PLAYER, secondinMaxAlgorithm)
                        State.FIRST_PLAYER -> onPlayerMove(PlayerMove.FIRST_PLAYER_MOVE)
                        State.SECOND_PLAYER -> onPlayerMove(PlayerMove.SECOND_PLAYER_MOVE)
                        else -> {
                        }
                    }
                }
            }
        }
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
        board = Board()
        observeForPlayerStateChanged()
        cells.setAll(board.cells.toList())
    }

    private fun onAIMove(move: PlayerMove, state: State, minMaxAlgorithm: MinMaxAlgorithm) {
        disabled.set(true)
        playerMove.set(move.message)
        tornadofx.runAsync { minMaxAlgorithm.minimax(board, difficultyLevel.depth).first } ui {
            updateBoard(it, state)
            rewriteCurrentAndFuturePlayer()
        }
    }

    private fun onPlayerMove(move: PlayerMove) {
        disabled.set(false)
        playerMove.set(move.message)
    }

    fun difficultyLevelOnChange(level: DifficultyLevel) {
        difficultyLevel = level
    }

    fun playerOnChange(name: String, playerState: State, aiState: State, setPlayer: (state: State) -> Unit) {
        setPlayer(
            when (name) {
                "AI" -> aiState
                else -> playerState
            }
        )
    }

    fun startOnClick() {
        if (this::difficultyLevel.isInitialized && this::firstPlayer.isInitialized && this::secondPlayer.isInitialized) {
            started.set(true)
            mainButtonLabel.set("Restart!")
            prepareAIPlayers()
            val (currentPlayer, futurePlayer) = chooseCurrentAndFuturePlayer()
            futurePlayerState.set(futurePlayer)
            currentPlayerState.set(currentPlayer)
        }
    }

    private fun prepareAIPlayers() {
        if (firstPlayer == State.FIRST_AI_PLAYER)
            firstMinMaxAlgorithm = MinMaxAlgorithm(State.FIRST_AI_PLAYER, State.SECOND_AI_PLAYER)
        if (secondPlayer == State.SECOND_AI_PLAYER)
            secondinMaxAlgorithm = MinMaxAlgorithm(State.SECOND_AI_PLAYER, State.FIRST_AI_PLAYER)
    }

    fun restartOnClick() {
        resultMessage.set("")
        board = Board()
        cells.setAll(board.cells.toList())
        prepareAIPlayers()
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
            rewriteCurrentAndFuturePlayer()
        }
    }
}




