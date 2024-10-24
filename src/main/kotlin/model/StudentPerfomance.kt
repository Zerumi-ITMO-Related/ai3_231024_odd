package io.github.zerumi.model

import io.github.zerumi.stat.normalized

data class StudentPerformance(
    val hoursStudied: Int,
    val previousScores: Int,
    val extracurricularActivities: Boolean,
    val sleepHours: Int,
    val sampleQuestionPapersPracticed: Int,
    val performanceIndex: Float
)

data class FloatStudentPerformance(
    val hoursStudied: Float,
    val previousScores: Float,
    val extracurricularActivities: Float,
    val sleepHours: Float,
    val sampleQuestionPapersPracticed: Float,
    val performanceIndex: Float
)

data class StudentPerformanceFunction(
    val hoursStudiedCoefficient: Float,
    val previousScoresCoefficient: Float,
    val extracurricularActivitiesCoefficient: Float,
    val sleepHoursCoefficient: Float,
    val sampleQuestionPapersPracticedCoefficient: Float,
) {
    fun calculate(testStudentPerformance: FloatStudentPerformance) = testStudentPerformance.copy(
        performanceIndex = hoursStudiedCoefficient * testStudentPerformance.hoursStudied +
                previousScoresCoefficient * testStudentPerformance.previousScores +
                extracurricularActivitiesCoefficient * testStudentPerformance.extracurricularActivities +
                sleepHoursCoefficient * testStudentPerformance.sleepHours +
                sampleQuestionPapersPracticedCoefficient * testStudentPerformance.sampleQuestionPapersPracticed
    )
}

fun List<StudentPerformance>.normalized(): List<FloatStudentPerformance> {
    val normalizedHoursStudied = this.extractFloatHoursStudied().normalized()
    val normalizedPreviousScoresStat = this.extractFloatPreviousScores().normalized()
    val normalizedExtracurricularActivitiesStat = this.extractFloatExtracurricularActivities().normalized()
    val normalizedSleepHoursStat = this.extractFloatSleepHours().normalized()
    val normalizedSampleQuestionPapersPracticedStat =
        this.extractFloatSampleQuestionPapersPracticed().normalized()
    val performanceIndex = this.extractFloatPerformanceIndex()

    return List(normalizedHoursStudied.size) { index ->
        FloatStudentPerformance(
            normalizedHoursStudied[index],
            normalizedPreviousScoresStat[index],
            normalizedExtracurricularActivitiesStat[index],
            normalizedSleepHoursStat[index],
            normalizedSampleQuestionPapersPracticedStat[index],
            performanceIndex[index]
        )
    }
}

fun List<StudentPerformance>.extractFloatHoursStudied(): List<Float> =
    this.map { it.hoursStudied.toFloat() }

fun List<StudentPerformance>.extractFloatPreviousScores(): List<Float> =
    this.map { it.previousScores.toFloat() }

fun List<StudentPerformance>.extractFloatExtracurricularActivities(): List<Float> =
    this.map { if (it.extracurricularActivities) 1.0f else 0.0f }

fun List<StudentPerformance>.extractFloatSleepHours(): List<Float> =
    this.map { it.sleepHours.toFloat() }

fun List<StudentPerformance>.extractFloatSampleQuestionPapersPracticed(): List<Float> =
    this.map { it.sampleQuestionPapersPracticed.toFloat() }

fun List<StudentPerformance>.extractFloatPerformanceIndex(): List<Float> =
    this.map { it.performanceIndex }

fun List<FloatStudentPerformance>.extractPerformanceIndex(): List<Float> =
    this.map { it.performanceIndex }
