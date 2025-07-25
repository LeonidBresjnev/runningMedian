package wasm.project.demo.runningmedian

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.maxOfOrNull
import org.jetbrains.kotlinx.dataframe.api.minOfOrNull
import org.jetbrains.kotlinx.dataframe.api.sortBy

fun runningMedian(df: DataFrame<RunningMedianInput>, w: Double): DataFrame<RunningMedianOutput> {



    val lowerHeap = MultiDirectAccessMinHeap(minHeap = false)
    val upperHeap = MultiDirectAccessMinHeap(minHeap = true)

    val sortedDf:  DataFrame<RunningMedianInput> = df.sortBy { "AGE" and "ID" } /*as DataFrame<RunningMedianInput>*/

    println("DataFrame: $sortedDf")

    val groupedByAge = sortedDf.groupBy { "AGE"<Double>() }
    println("DataFrame: $groupedByAge")
    val runningMedianCol : MutableList<Pair<Double,Double>> = mutableListOf()
    groupedByAge.groups.forEach { it ->
        it.forEach { row ->
            val myTriple = Triple(
                third = "ID"<Long>(),
                first = "AGE"<Double>(),
                second = "VALUE"<Double>())
            if (lowerHeap.isEmpty()||upperHeap.isEmpty()|| myTriple.second<= lowerHeap.first().second)
                lowerHeap.add(myTriple)
            else {
                upperHeap.add(myTriple)
            }
        }
        val age: Double = (it.first()["AGE"] as Double)

        lowerHeap.removeSmallAges(age = age - 2*w)
        upperHeap.removeSmallAges(age = age - 2*w)
/*
        if (lowerHeap.size >0 && upperHeap.size >0) {
            check(lowerHeap.first().second<= upperHeap.first().second) {
                "Lower heap first: ${lowerHeap.first().second}, Upper heap first: ${upperHeap.first().second}"
            }
        }*/

        while (lowerHeap.size > upperHeap.size) {
            lowerHeap.poll()?.also {it2 ->
                upperHeap.add(it2)
                //println("Task: $it, size: ${lowerHeap.size} ${upperHeap.size}")
            } ?: break
        }

        while (lowerHeap.size < upperHeap.size) {
            upperHeap.poll()?.also { it2 ->
                lowerHeap.add(it2)
            }?: break
        }

        val x = if ((lowerHeap.size + upperHeap.size) %2 == 1) { lowerHeap.first().second }
        else {
            var temp = 0.0

            var denominator = 0
            try {
                temp += lowerHeap.first().second
                //println("lower heap: ${lowerHeap.first().second}")
                denominator++
            } catch (e: Exception) {
                println("Lower heap is empty")
            }
            try {
                temp += upperHeap.first().second
                //println("upper heap: ${upperHeap.first().second}")
                denominator++
            } catch (e: Exception) {
                println("Upper heap is empty")
            }
            temp /= denominator
            temp
        }

        if (age==4531+w) {
            println("Task: $it, size: ${lowerHeap.size} ${upperHeap.size}")
            lowerHeap.printNodes()
            println("........")
            upperHeap.printNodes()
            println("age= ${age-w} Lower heap: ${lowerHeap.first().second}, Upper heap: ${upperHeap.first().second}")
        }
        runningMedianCol.add(Pair(age-w,x))
    }
    val maxage= groupedByAge.groups.maxOfOrNull { it.first()["AGE"] as Double } ?: 0.0
    val minage= groupedByAge.groups.minOfOrNull { it.first()["AGE"] as Double } ?: 0.0

    groupedByAge.groups.filter { it.first()["AGE"] as Double >  maxage-w}.forEach { it ->
        val age: Double = (it.first()["AGE"] as Double)

        lowerHeap.removeSmallAges(age = age - w)
        upperHeap.removeSmallAges(age = age - w)
        while (lowerHeap.size > upperHeap.size) {
            lowerHeap.poll()?.also {it2 ->
                upperHeap.add(it2)
                //println("Task: $it, size: ${lowerHeap.size} ${upperHeap.size}")
            } ?: break
        }

        while (lowerHeap.size < upperHeap.size) {
            upperHeap.poll()?.also { it2 ->
                lowerHeap.add(it2)
            }?: break
        }

        val x = if ((lowerHeap.size + upperHeap.size) %2 == 1) { lowerHeap.first().second }
        else {

            var temp = 0.0

            var denominator = 0
            try {
                temp += lowerHeap.first().second
                //println("lower heap: ${lowerHeap.first().second}")
                denominator++
            } catch (e: Exception) {
                println("Lower heap is empty")
            }
            try {
                temp += upperHeap.first().second
                //println("upper heap: ${upperHeap.first().second}")
                denominator++
            } catch (e: Exception) {
                println("Upper heap is empty")
            }
            temp /= denominator
            temp
        }
        runningMedianCol.add(Pair(age,x))
    }
    runningMedianCol.removeIf { it.first<minage || it.first>maxage }
    val runningMedianDF: DataFrame<RunningMedianOutput> = dataFrameOf(
        "AGE" to runningMedianCol.map { it.first }.toList(),
        "VALUE" to runningMedianCol.map { it.second }.toList()
    ) as DataFrame<RunningMedianOutput>

    return runningMedianDF

}