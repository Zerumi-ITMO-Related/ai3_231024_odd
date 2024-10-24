package io.github.zerumi.csv

import io.github.zerumi.model.StudentPerformance
import java.io.InputStream

data class Dataset<T>(
    val labels: List<String>,
    val values: List<StudentPerformance>,
)

fun readCsv(inputStream: InputStream): Dataset<StudentPerformance> {
    val reader = inputStream.bufferedReader()
    val header = reader.readLine().split(',')

    return Dataset(header, reader.lineSequence().filter { it.isNotBlank() }.map {
            val (hoursStudied, prevScore, excActivities, sleepHours, sqPapersPracticed, performanceIndex) = it.split(
                ','
            )
            StudentPerformance(
                hoursStudied.toInt(),
                prevScore.toInt(),
                excActivities.equals("yes", ignoreCase = true),
                sleepHours.toInt(),
                sqPapersPracticed.toInt(),
                performanceIndex.toFloat()
            )
        }.toList()
    )
}

private operator fun <E> List<E>.component6(): E = this[5]
