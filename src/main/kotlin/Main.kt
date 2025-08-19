package wasm.project.demo

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.letsPlot
import wasm.project.demo.runningmedian.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val random = Random    //val gaussian0 = random.gaussian0()
    val gaussian1 = random.gaussian1()
    //val classgaussian = Classgaussian()
    val size = 100
    val v = List(size = size) {
        val t = it.toDouble() / 10
        //gaussian0()*4.0 -(t-5).pow(2)
        gaussian1.nextval() * 4.0 - (t - 5).pow(2)
        //classgaussian.next()*4.0 -(t-5).pow(2)
    }
    val myHeap = DirectAccessMinHeap<Int, Double>(minHeap = true)
    //val mySortedMap =  sortedM sortedMapOf<Int, Double>()


    //mySortedMap.putAll( v.mapIndexed { idx,it -> idx to it } )
    v.forEachIndexed { index, it ->
        myHeap.add(Task(id = index, priority = it))
    }
/*
    val rawDataFile = File("longitude.csv")
    val realData = DataFrame.readCSV(
        fileOrUrl = rawDataFile.path,
        delimiter = ',',
        colTypes = mapOf(
            "COHORT" to ColType.Int,
            "PERSON_ID" to ColType.String,
            "VALUE_AS_NUMBER" to ColType.Double,
            "AGE" to ColType.Double
        )
    ).filter { "COHORT"<Int>() == 0}
        .select("VALUE_AS_NUMBER", "AGE")
        .rename("VALUE_AS_NUMBER").into("VALUE")
         .add("ID") { index() }
         as DataFrame<RunningMedianInput>
    realData.print (10)

    val realRunningMedian: DataFrame<RunningMedianOutput>
            = runningMedian(df = realData, w = 2*365.0) as DataFrame<RunningMedianOutput>

    val myMultiHeap = MultiDirectAccessMinHeap(minHeap = true)
    //val mySortedMap =  sortedM sortedMapOf<Int, Double>()
    val plot0 = letsPlot(
        data = mapOf<String, List<Number>>(

            "x" to realRunningMedian.AGE.toList(),
            "y" to realRunningMedian.VALUE.toList(),
        )
    ) + geomPoint(data = mapOf(
        "x" to realData.AGE.toList(),
        "y" to realData.VALUE.toList())) {
        x = "x"
        y = "y"
    } + geomLine(color = "red") {
        x = "x"
        y = "y"
    }
    plot0.show()*/

    //mySortedMap.putAll( v.mapIndexed { idx,it -> idx to it } )
//val ages=listOf(44,39,47,45,45,29,90,43,68,41)
    val ages = List(v.size) { random.nextInt(0, size).toDouble() }

    /*
    heapNodes.forEach {
        println(it)
    }*/
/*
    for (age in 0..<1) {
        myMultiHeap.removeByAge(age.toDouble())
        println("Age: $age")
    }
    myMultiHeap.removeByAge(45.0)*/

    println("--------------------------------")
  //  myMultiHeap.printNodes()
    // val first=myMultiHeap.iterator().next()
//println("First element: ${first.id}, priority: ${first.priority}, age: ${first.age}")



    val myDf: DataFrame<RunningMedianInput> = dataFrameOf(
        "ID" to List(v.size) { it.toDouble() },
        "VALUE" to v,
        "AGE" to List(v.size) { it.toDouble() }) as DataFrame<RunningMedianInput>

    val runningMedian: DataFrame<RunningMedianOutput>
    = runningMedian(df = myDf, w = 10.0) as DataFrame<RunningMedianOutput>



    //val runningMedianDf = runningMedian(sortedDf)
    //println("Running median DataFrame: $runningMedianDf")
/*



    val w = 10
    val runningMedian = List(size = size+2*w) { i ->
        //println("i: $i")
        lowerHeap.removeSmallAges(age = i - 2*w-1)
        upperHeap.removeSmallAges(age = i - 2*w-1)

        if (i>=0 && (i < v.lastIndex)) lowerHeap.add(Triple(third = i, first = i, second = v[i]))


        while (lowerHeap.size > upperHeap.size) {
            val task = lowerHeap.poll()
            //lowerHeap.remove(task)

            task?.let {
                //println("Task: $it, size: ${lowerHeap.size} ${upperHeap.size}")
                upperHeap.add(it)
                //println("Task: $it, size: ${lowerHeap.size} ${upperHeap.size}")
            }
        }

        while (lowerHeap.size < upperHeap.size) {
            upperHeap.poll()?.also {
                lowerHeap.add(it)
            }

        }

        var x = if (lowerHeap.size + upperHeap.size %2 == 1) { lowerHeap.first().second }
        else {
            var temp = 0.0

            var denominator = 0
            try {
                temp += lowerHeap.first().second
                println("lower heap: ${lowerHeap.first().second}")
                denominator++
            } catch (e: Exception) {
                println("Lower heap is empty")
            }
            try {
                temp += upperHeap.first().second
                println("upper heap: ${upperHeap.first().second}")
                denominator++
            } catch (e: Exception) {
                println("Upper heap is empty")
            }
             temp /= denominator
            temp
        }
        println("size= ${lowerHeap.size+upperHeap.size}, i= $i, x= $x}")
        return@List x
    }.slice(w..(size+w-1))




    val plot = letsPlot(
        data = mapOf<String, List<Number>>(
            "x" to (0..99).toList(),
            "y" to v
        )
    ) + geomPoint {
        x = "x"
        y = "y"
    } + geomLine(color = "red") {
        x = 0..99
        y = runningMedian
    } + geomLine(color = "green") {
        x = 0..99
        y = runningMedian2
    }
    plot.show()
*/

    val runningMedian2 = List(100) { it ->
        val sublist = v.slice(max(0,it-10)..min(99,it+10))
            .sorted()
        println(sublist.size)
        return@List sublist[sublist.size / 2] // median
    }
    val plot = letsPlot(
        data = mapOf<String, List<Number>>(

            "x" to runningMedian.map { AGE }.toList(),
            "y" to runningMedian.map { VALUE }.toList(),
        )
    ) + geomPoint(data = mapOf(
        "x" to myDf.map { AGE }.toList(),
        "y" to myDf.map { VALUE }.toList())) {
        x = "x"
        y = "y"
    } + geomLine(color = "red") {
        x = "x"
        y = "y"
    } + geomLine(color = "green") {
        x = 0..99
        y = runningMedian2
    }
    plot.show()
}
