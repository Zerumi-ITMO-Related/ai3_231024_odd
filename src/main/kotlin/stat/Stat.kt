package io.github.zerumi.stat

import kotlin.math.pow
import kotlin.math.sqrt

data class ListStat(
    val listName: String,
    val count: Int,
    val mean: Float,
    val std: Float,
    val min: Float,
    val q25: Float,
    val q50: Float,
    val q75: Float,
    val max: Float,
) {
    companion object {
        fun fromList(listName: String, list: List<Float>) = ListStat(
            listName = listName,
            count = list.size,
            mean = list.sum() / list.size,
            std = sqrt(1.0f / list.size *
                    list.fold(0.0f) { acc, e ->
                        acc + (e - (list.sum() / list.size)).pow(2)
                    }),
            min = list.min(),
            q25 = list.sorted()[list.size / 4],
            q50 = list.sorted()[list.size / 2],
            q75 = list.sorted()[list.size * 3 / 4],
            max = list.max(),
        )
    }

    override fun toString(): String = "----------------------------\n" +
            "${this.listName}: \n" +
            "Count = ${this.count}\n" +
            "Mean = ${this.mean}\n" +
            "Standard deviation = ${this.std}\n" +
            "Min = ${this.min}\n" +
            "25% = ${this.q25}\n" +
            "50% = ${this.q50}\n" +
            "75% = ${this.q75}\n" +
            "Max = ${this.max}"
}

fun List<Float>.normalized() = this.map { (it - this.min()) / (this.max() - this.min()) }
