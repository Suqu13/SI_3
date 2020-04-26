package gui

import engine.State
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import tornadofx.*

class Page : View() {
    private val pageController: PageController by inject()
    private val pageContext = PageContext()

    private fun moveOnClick(columnNum: Int) {
        pageController.moveOnClick(columnNum)
    }

    override fun onDock() {
        super.onDock()
        setWindowMaxSize(1150.0, 750.0)
        setWindowMinSize(1150.0, 750.0)
        pageController.initialize()
    }

    override val root = borderpane {
        setPrefSize(1150.0, 750.0)
        addClass(Styles.page)
        left = board(pageController)
        center = contextBuilder()
    }

    private fun getCssRuleForUser(state: State): CssRule {
        return when (state) {
            State.FIRST_PLAYER -> Styles.firstPlayerButton
            State.AI_PLAYER -> Styles.secondPlayerButton
            else -> Styles.emptyButton
        }
    }

    private fun EventTarget.board(pageController: PageController): GridPane = gridpane {
        addClass(Styles.board)
        pageController.cells.onChange {
            pageController.cells.forEachIndexed { i, r ->
                row {
                    r.forEachIndexed { j, c ->
                        val cell = vbox {
                            addClass(Styles.generalButtonBackground)
                            button {
                                disableProperty().bind(pageController.disabled.or(!pageController.started))
                                addClass(getCssRuleForUser(c), Styles.generalButton)
                                action { moveOnClick(j) }
                            }
                        }
                        add(cell, j, i)
                    }
                }
            }
        }
    }


    private fun <T> EventTarget.customComboBoxBuilder(
        label: String,
        options: ObservableList<T>,
        onChange: (value: String) -> Unit
    ): VBox =
        vbox {
            addClass(Styles.customComboBox)
            label(label)
            combobox(values = options) {
                setOnAction {
                    onChange(value.toString())

                }
            }
        }

    private fun EventTarget.customButtonBuilder(label: StringProperty, onClick: () -> Unit) =
        button(label) {
            addClass(Styles.customButton)
            action { onClick() }
        }

    private fun EventTarget.contextBuilder(): VBox {
        return vbox {
            spacing = 20.0
            alignment = Pos.TOP_CENTER
            padding = Insets(40.0, 0.0, 40.0, 0.0)
            tittle("Connect Four")
            customComboBoxBuilder(
                "Difficulty Level",
                pageContext.difficultyLevels
            ) { value -> pageController.difficultyLevelOnChange(DifficultyLevel.valueOf(value)) }
            customButtonBuilder(pageController.mainButtonLabel) { if (pageController.started.get()) pageController.restartOnClick() else pageController.startOnClick() }
            moveStatus(pageController.playerMove)
            winnerStatus(pageController.winner)
        }
    }

    private fun EventTarget.moveStatus(label: SimpleStringProperty) =
        text(label) {
            addClass(Styles.customText)
        }

    private fun EventTarget.winnerStatus(label: SimpleStringProperty) =
        text(label) {
            addClass(Styles.customHighlightedText)
        }

    private fun EventTarget.tittle(label: String) =
        text(label) {
            addClass(Styles.customTitleText)
        }
}



