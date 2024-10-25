package io.github.zerumi

import io.github.zerumi.csv.readCsv
import io.github.zerumi.model.*
import io.github.zerumi.stat.ListStat
import io.github.zerumi.stat.normalized
import io.github.zerumi.util.head
import kotlin.math.pow
import kotlin.math.roundToInt

fun main() {
    val dataset = readCsv(
        {}::class.java.classLoader.getResourceAsStream("Student_Performance.csv")
            ?: throw IllegalArgumentException("Resource not found")
    )

    val hoursStudiedStat = ListStat.fromList(dataset.labels[0], dataset.values.extractFloatHoursStudied())
    val previousScoresStat = ListStat.fromList(dataset.labels[1], dataset.values.extractFloatPreviousScores())
    val extracurricularActivitiesStat =
        ListStat.fromList(dataset.labels[2], dataset.values.extractFloatExtracurricularActivities())
    val sleepHoursStat = ListStat.fromList(dataset.labels[3], dataset.values.extractFloatSleepHours())
    val sampleQuestionPapersPracticedStat =
        ListStat.fromList(dataset.labels[4], dataset.values.extractFloatSampleQuestionPapersPracticed())
    val performanceIndexStat = ListStat.fromList(dataset.labels[5], dataset.values.extractFloatPerformanceIndex())

    println(hoursStudiedStat)
    println(previousScoresStat)
    println(extracurricularActivitiesStat)
    println(sleepHoursStat)
    println(sampleQuestionPapersPracticedStat)
    println(performanceIndexStat)

    println("----------------------------")

    val shuffledData = dataset.values.shuffled()
    val learningDataSize = 6000
    val learningData = shuffledData.take(learningDataSize)
    val testingData = shuffledData.takeLast(10000 - learningDataSize).normalized()

    val normalizedHoursStudied = learningData.extractFloatHoursStudied().normalized()
    val normalizedPreviousScoresStat = learningData.extractFloatPreviousScores().normalized()
    val normalizedExtracurricularActivitiesStat = learningData.extractFloatExtracurricularActivities().normalized()
    val normalizedSleepHoursStat = learningData.extractFloatSleepHours().normalized()
    val normalizedSampleQuestionPapersPracticedStat =
        learningData.extractFloatSampleQuestionPapersPracticed().normalized()

    println("${dataset.labels[0]} normalized: ${normalizedHoursStudied.head()}")
    println("${dataset.labels[1]} normalized: ${normalizedPreviousScoresStat.head()}")
    println("${dataset.labels[2]} normalized: ${normalizedExtracurricularActivitiesStat.head()}")
    println("${dataset.labels[3]} normalized: ${normalizedSleepHoursStat.head()}")
    println("${dataset.labels[4]} normalized: ${normalizedSampleQuestionPapersPracticedStat.head()}")

    println("----------------------------")

    val x = Matrix(listOf(normalizedHoursStudied,
        normalizedPreviousScoresStat,
        normalizedExtracurricularActivitiesStat,
        normalizedSleepHoursStat,
        normalizedSampleQuestionPapersPracticedStat,
        List(learningDataSize) { 1.0f })) // 6 x 10000

    val y = Matrix(
        listOf(
            learningData.extractFloatPerformanceIndex()
        )
    ) // 1 x 10000

    val beta = ((x * x.transpose()).inverse() * x * (y.transpose())).matrix.flatten()
    println("Optimal coefficients: $beta")
    println("Check: ${beta.sum()}")

    println("----------------------------")

    val function = StudentPerformanceFunction(
        hoursStudiedCoefficient = beta[0],
        previousScoresCoefficient = beta[1],
        extracurricularActivitiesCoefficient = beta[2],
        sleepHoursCoefficient = beta[3],
        sampleQuestionPapersPracticedCoefficient = beta[4],
        freeCoefficient = beta[5],
    )

    println("Calculate on test data:")

    val testResult = testingData.map { function.calculate(it) }
    val performanceResult = testResult.extractPerformanceIndex()
    val testRealPerformance = testingData.extractPerformanceIndex()

    println("Function result: ${performanceResult.take(15).map { it.roundToInt().toFloat() }}")
    println("Real test data : ${testRealPerformance.take(15)}")

    println("Average function result : ${performanceResult.average().roundToInt()}")
    println("Average test data result: ${testRealPerformance.average().roundToInt()}")

    val correlation = 1.0f - ((testRealPerformance.foldIndexed(0.0f) { index, acc, fl ->
        acc + (fl - performanceResult[index]).pow(2)
    }) / (testRealPerformance.fold(0.0f) { acc, fl ->
        acc + (fl - testRealPerformance.average().toFloat()).pow(2)
    }))

    println("Determination: $correlation")
}
