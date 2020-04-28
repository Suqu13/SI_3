package gui

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val page by cssclass()

        val firstPlayerButton by cssclass()
        val secondPlayerButton by cssclass()
        val firstAIButton by cssclass()
        val secondAIButton by cssclass()
        val emptyButton by cssclass()

        val generalButton by cssclass()
        val generalButtonBackground by cssclass()

        val board by cssclass()


        val customButton by cssclass()
        val customText by cssclass()
        val customHighlightedText by cssclass()
        val customTitleText by cssclass()
        val customComboBox by cssclass()

        val ORANGE: Color = c("#ffd31d")
        val DARK_GRAY: Color = c("#323232")
    }

    init {
        s(firstPlayerButton) {
            backgroundColor = multi(Color.RED)
        }

        s(secondPlayerButton) {
            backgroundColor = multi(Color.BLUE)
        }

        s(firstAIButton) {
            backgroundColor = multi(Color.GREEN)
        }

        s(secondAIButton) {
            backgroundColor = multi(Color.PINK)
        }

        s(emptyButton) {
            backgroundColor = multi(Color.WHITE)
        }

        s(generalButton) {
            minHeight = 80.px
            minWidth = 80.px
            backgroundRadius = multi(box(100.px))
            and(disabled) {
                opacity = 100.0
            }
            and(hover) {
                opacity = 30.0
                borderRadius = multi(box(100.px))
                borderWidth = multi(box(10.px))
                borderColor = multi(
                    box(
                        ORANGE
                    )
                )
            }
        }

        s(generalButtonBackground) {
            padding = box(10.px, 10.px)
            backgroundColor = multi(Color.BLACK)
            backgroundRadius = multi(box(100.px))
        }

        s(board) {
            alignment = Pos.CENTER
            padding = box(50.0.px, 20.0.px, 50.px, 50.0.px)
        }

        s(page) {
            backgroundColor = multi(DARK_GRAY)
        }

        s(customButton) {
            minWidth = 300.px
            backgroundColor = multi(Color.WHITE)
            borderColor = multi(box(Color.WHITE))
            borderRadius = multi(box(5.px))
            padding = box(10.px, 20.px)
            fontWeight = FontWeight.findByWeight(700)
            and(hover) {
                backgroundColor = multi(ORANGE)
                borderColor = multi(
                    box(
                        ORANGE
                    )
                )
            }
        }

        s(customText) {
            fill = Color.WHITE
            fontWeight = FontWeight.findByWeight(700)

        }

        s(customHighlightedText) {
            fill = ORANGE
            fontWeight = FontWeight.findByWeight(700)
            fontSize = 20.px
        }

        s(customTitleText) {
            fill = ORANGE
            fontWeight = FontWeight.findByWeight(700)
            fontSize = 40.px
        }

        s(customComboBox) {
            alignment = Pos.TOP_CENTER
            label {
                minWidth = 300.px
                textFill = Color.WHITE
                textAlignment = TextAlignment.LEFT
                fontWeight = FontWeight.findByWeight(700)
            }
            comboBox {
                minWidth = 300.px
                backgroundColor = multi(Color.WHITE)
                borderColor = multi(box(Color.WHITE))
                borderRadius = multi(box(5.px))
                padding = box(10.px, 20.px)
                fontWeight = FontWeight.findByWeight(700)
                and(hover) {
                    backgroundColor = multi(ORANGE)
                    borderColor = multi(
                        box(
                            ORANGE
                        )
                    )
                }
            }
        }
    }
}