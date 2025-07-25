package wasm.project.demo.runningmedian

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*

fun runningMedian(df: DataFrame<RunningMedianInput>, w: Double): DataFrame<RunningMedianOutput> {



    val lowerHeap = MultiDirectAccessMinHeap(minHeap = false)
    val upperHeap = MultiDirectAccessMinHeap(minHeap = true)

    val sortedDf:  DataFrame<RunningMedianInput> = df.sortBy { AGE and ID } /*as DataFrame<RunningMedianInput>*/

    //println("DataFrame: $sortedDf")

    val groupedByAge = sortedDf.groupBy { AGE }
    //println("DataFrame: $groupedByAge")
    val runningMedianCol : MutableList<Pair<Double,Double>> = mutableListOf()
    /*
    var output = dataFrameOf(
         "VALUE" to listOf<Double>(),
        "AGE" to listOf<Double>()
    ).convertTo<RunningMedianOutput>() as DataFrame<RunningMedianOutput>*/

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
        val age: Double = it.first().AGE

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

        runningMedianCol.add(Pair(age-w,x))
            /*
            is not not used because append is not inplace
                    output.append(
                        x,age
                    )*/
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
                denominator++
            } catch (e: Exception) {
                println("Lower heap is empty")
            }
            try {
                temp += upperHeap.first().second
                denominator++
            } catch (e: Exception) {
                println("Upper heap is empty")
            }
            temp /= denominator
            temp
        }
        runningMedianCol.add(Pair(age,x))
/*
is not not used because append is not inplace
        output.append(
            x,age
        )*/
    }
    runningMedianCol.removeIf { it.first<minage || it.first>maxage }
    val runningMedianDF: DataFrame<RunningMedianOutput> = dataFrameOf(
        "AGE" to runningMedianCol.map { it.first }.toList(),
        "VALUE" to runningMedianCol.map { it.second }.toList()
    ) as DataFrame<RunningMedianOutput>

    return runningMedianDF

}