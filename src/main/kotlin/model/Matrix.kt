package io.github.zerumi.model

data class Matrix(
    val matrix: List<List<Float>>
) {
    fun transposed(): Matrix {
        val rows = matrix.size
        val columns = matrix[0].size
        val transposedMatrix = MutableList(columns) { MutableList(rows) { 0.0f } }

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                transposedMatrix[j][i] = matrix[i][j]
            }
        }

        return Matrix(transposedMatrix)
    }

    operator fun times(matrix: Matrix): Matrix {
        val row1 = this.matrix.size
        val col1 = this.matrix[0].size
        val col2 = matrix.matrix[0].size
        val product = MutableList(row1) { MutableList(col2) { 0.0f } }

        for (i in 0 until row1) {
            for (j in 0 until col2) {
                for (k in 0 until col1) {
                    product[i][j] += this.matrix[i][k] * matrix.matrix[k][j]
                }
            }
        }

        return Matrix(product)
    }

    private fun minor(row: Int, col: Int): Matrix =
        Matrix(matrix.filterIndexed { i, _ -> i != row }.map { it.filterIndexed { j, _ -> j != col } })

    private fun determinant(): Float {
        if (matrix.size == 1) return matrix[0][0]
        if (matrix.size == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]
        }

        var det = 0.0f
        for (col in matrix[0].indices) {
            det += (if (col % 2 == 0) 1 else -1) * matrix[0][col] * minor(0, col).determinant()
        }

        return det
    }

    private fun cofactorMatrix(): Matrix {
        val size = matrix.size
        val cofactorMatrix = MutableList(size) { MutableList(size) { 0.0f } }

        for (i in 0 until size) {
            for (j in 0 until size) {
                cofactorMatrix[i][j] = (if ((i + j) % 2 == 0) 1 else -1) * minor(i, j).determinant()
            }
        }

        return Matrix(cofactorMatrix)
    }

    fun inverse(): Matrix {
        val det = determinant()
        require(det != 0.0f) { "Matrix is not invertible" }

        val cofactorMatrix = cofactorMatrix()
        val adjugateMatrix = cofactorMatrix.transposed()

        val inverseMatrix = adjugateMatrix.matrix.map { row ->
            row.map { element -> element / det }
        }

        return Matrix(inverseMatrix)
    }
}