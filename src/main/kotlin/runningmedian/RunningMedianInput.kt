package wasm.project.demo.runningmedian

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema


@DataSchema
interface RunningMedianInput {
    val VALUE: Double
    val AGE: Double
    val ID: Long
}