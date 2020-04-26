package engine

class Board(val cells: Array<Array<State>> = Array(6) { Array(7) { State.EMPTY } }) {

    var winner: State? = null

    fun copy(): Board {
        return Board(cells.map { it.clone() }.toTypedArray())
    }

    fun move(columnNum: Int, state: State): Boolean {
        val possibleCellRowIndex = findCellRowIndex(columnNum)
        if (possibleCellRowIndex == -1) return false
        cells[possibleCellRowIndex][columnNum] = state
        if (checkIfWinner(state)) winner = state
        return true
    }

    fun findPossibleMoves(): IntArray {
        val possibleMoves = arrayListOf<Int>()
        cells.forEach { arrayOfStates ->
            arrayOfStates.forEachIndexed { index, state ->
                if (state == State.EMPTY) possibleMoves.add(
                    index
                )
            }
        }
        return possibleMoves.distinct().toIntArray()
    }

    private fun findCellRowIndex(columnNum: Int): Int {
        var possibleCellRowIndex = -1
        cells.forEachIndexed { index, arrayOfCells ->
            if (arrayOfCells[columnNum] == State.EMPTY) possibleCellRowIndex = index
        }
        return possibleCellRowIndex
    }

//    private fun obtainArraysOfFourHorizontal(): Array<Array<State>> {
//        val windowsNum = cells[0].size % 4 + 1
//        val arraysOfFour = arrayListOf(arrayOf<State>())
//        cells.forEach { row ->
//            (0 until windowsNum).forEach { arraysOfFour.add(row.sliceArray(it until it + 4)) }
//        }
//        return arraysOfFour.toTypedArray()
//    }

//    private fun obtainArraysOfFourVertical(): Array<Array<State>> {
//        val windowsNum = cells.size % 4 + 1
//        val arraysOfFour = arrayListOf(arrayOf<State>())
//        (cells[0].indices).forEach { columnIndex ->
//            (0 until windowsNum).forEach { arraysOfFour.add(arrayOf(cells[it][columnIndex], cells[it + 1][columnIndex], cells[it + 2][columnIndex], cells[it + 3][columnIndex])) }
//        }
//        return arraysOfFour.toTypedArray()
//    }

    //    private fun obtainArraysOfFourInDiagonal(): Array<Array<State>> {
    fun obtainArraysOfFour(): Array<Array<State>> {
        val windowColumnNum = cells[0].size % 4 + 1
        val windowRowNum = cells.size % 4 + 1

        val arraysOfFour = arrayListOf(arrayOf<State>())
        cells.forEachIndexed { rowIndex, row ->

            (0 until windowColumnNum)
                .forEach { arraysOfFour.add(row.sliceArray(it until it + 4)) }

            if (rowIndex < windowRowNum)
                (cells[0].indices).forEach { columnIndex ->
                    arraysOfFour.add(
                        arrayOf(
                            cells[rowIndex][columnIndex],
                            cells[rowIndex + 1][columnIndex],
                            cells[rowIndex + 2][columnIndex],
                            cells[rowIndex + 3][columnIndex]
                        )
                    )
                }

            if (rowIndex + 3 < cells.size)
                row.forEachIndexed { columnIndex, _ ->
                    if (columnIndex + 3 < row.size)
                        arraysOfFour.add(
                            arrayOf(
                                cells[rowIndex][columnIndex],
                                cells[rowIndex + 1][columnIndex + 1],
                                cells[rowIndex + 2][columnIndex + 2],
                                cells[rowIndex + 3][columnIndex + 3]
                            )
                        )
                    if (columnIndex - 3 >= 0)
                        arraysOfFour.add(
                            arrayOf(
                                cells[rowIndex][columnIndex],
                                cells[rowIndex + 1][columnIndex - 1],
                                cells[rowIndex + 2][columnIndex - 2],
                                cells[rowIndex + 3][columnIndex - 3]
                            )
                        )
                }

            if (rowIndex - 3 >= 0)
                row.forEachIndexed { columnIndex, _ ->
                    if (columnIndex + 3 < row.size)
                        arraysOfFour.add(
                            arrayOf(
                                cells[rowIndex][columnIndex],
                                cells[rowIndex + -1][columnIndex + 1],
                                cells[rowIndex - 2][columnIndex + 2],
                                cells[rowIndex - 3][columnIndex + 3]
                            )
                        )
                    if (columnIndex - 3 >= 0)
                        arraysOfFour.add(
                            arrayOf(
                                cells[rowIndex][columnIndex],
                                cells[rowIndex - 1][columnIndex - 1],
                                cells[rowIndex - 2][columnIndex - 2],
                                cells[rowIndex - 3][columnIndex - 3]
                            )
                        )
                }
        }
        return arraysOfFour.toTypedArray()
    }

    //  fun obtainArraysOfFour(): Array<Array<State>> = (obtainArraysOfFourHorizontal() + obtainArraysOfFourVertical() + obtainArraysOfFourInDiagonal())
    private fun checkIfWinner(state: State): Boolean {
        return obtainArraysOfFour().any { arrayOfStates -> arrayOfStates.count { it == state } == 4 }
    }

    fun printBoard() {
        cells.forEach { row ->
            print("[ ")
            row.forEach { print(" ${it.token} ") }
            println(" ]")
        }
    }

}