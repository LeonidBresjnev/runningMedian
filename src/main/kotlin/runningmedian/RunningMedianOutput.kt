package wasm.project.demo.runningmedian

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
interface RunningMedianOutput{
    val VALUE: Double
    val AGE: Double
}