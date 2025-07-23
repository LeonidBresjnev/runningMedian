package wasm.project.demo

import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.letsPlot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val random=Random(seed = 42)
    //val gaussian0 = random.gaussian0()
    val gaussian1 = random.gaussian1()
    //val classgaussian = Classgaussian()
    val v = List(100) {
        val t = it.toDouble() / 10
        //gaussian0()*4.0 -(t-5).pow(2)
        gaussian1.nextval() * 4.0 -(t-5).pow(2)
        //classgaussian.next()*4.0 -(t-5).pow(2)
    }
    val myHeap = DirectAccessMinHeap<Int, Double>(minHeap = true)
    //val mySortedMap =  sortedM sortedMapOf<Int, Double>()


    //mySortedMap.putAll( v.mapIndexed { idx,it -> idx to it } )
    v.forEachIndexed { index, it ->
        myHeap.add(Task(id = index, priority = it))
    }
    //myHeap.remove()
    /*
    repeat(100) {
        val firstkey=mySortedMap.firstKey()
        val e= mySortedMap[firstkey]
        println("$firstkey, $e")
        mySortedMap.remove(firstkey)
    }
*/
    /*
    //myHeap.removeAllById((25..75).toList())
    myHeap.removeSmallId(id = 50)


    repeat(myHeap.size + 1) {
        try {
            val minTask = myHeap.iterator().next()
            myHeap.remove(minTask)
            println("Min task: ${minTask.id} with priority ${minTask.priority}")
        } catch (e: Exception) {
            println(e.message)
        }
    }


    val lowerHeap = DirectAccessMinHeap<Int, Double>(minHeap = true)
    val upperHeap = DirectAccessMinHeap<Int, Double>(minHeap = false)


    val w = 10
    val runningMedian = List(size = 100) { i ->
        lowerHeap.removeSmallId(id = i - w)
        upperHeap.removeSmallId(id = i - w)
        if (i + w < v.lastIndex) lowerHeap.add(Task(id = i + w, priority = v[i + w]))
        while (lowerHeap.size > upperHeap.size) {
            val task = lowerHeap.poll()
            task?.let {
                lowerHeap.removeById(task.id)
                upperHeap.add(it)
            }
        }
        while (lowerHeap.size < upperHeap.size) {
            val task = upperHeap.poll()
            task?.let {
                upperHeap.removeById(task.id)
                lowerHeap.add(it)
            }
        }

        println(
            "Lower heap size: ${lowerHeap.size}, ${lowerHeap.first()}, Upper heap size: ${upperHeap.size}, ${
                try {
                    upperHeap.first()
                } catch (e: Exception) {
                    ""
                }
            }"
        )
        var x = 0.0
        var denominator = 0
        try {
            x += lowerHeap.first().priority
            denominator++
        } catch (e: Exception) {
            0.0
        }
        try {
            x += upperHeap.first().priority
            denominator++
        } catch (e: Exception) {
            0.0
        }
        x /= denominator
        return@List x
    }


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
    }
    plot.show()
*/

    val myMultiHeap = MultiDirectAccessMinHeap<Int, Double>(minHeap = true)
    //val mySortedMap =  sortedM sortedMapOf<Int, Double>()


    //mySortedMap.putAll( v.mapIndexed { idx,it -> idx to it } )
//val ages=listOf(44,39,47,45,45,29,90,43,68,41)
    val ages=List(v.size) { random.nextInt(0, 100) }

    val heapNodes = v.mapIndexed { index,it ->
        HeapNode(id = index, priority = it, age= ages[index])
    }
    heapNodes.forEach {
        println(it)
    }
    heapNodes.forEach {
        myMultiHeap.add(it)
    }

    myMultiHeap.removeSmallAges(45)
   // val first=myMultiHeap.iterator().next()
//println("First element: ${first.id}, priority: ${first.priority}, age: ${first.age}")
println("MultiHeap size: ${myMultiHeap.size}")
    repeat(myMultiHeap.size) {

            val minTask = myMultiHeap.iterator().next()
            myMultiHeap.remove(myMultiHeap.first())
           // println("Min task: $minTask")

    }



    val lowerHeap = MultiDirectAccessMinHeap<Int, Double>(minHeap = true)
    val upperHeap = MultiDirectAccessMinHeap<Int, Double>(minHeap = false)


    val w = 10
    val runningMedian = List(size = 100) { i ->
        println("i: $i")
        lowerHeap.removeSmallAges(age = i - w)
        upperHeap.removeSmallAges(age = i - w)
        if (i + w < v.lastIndex) lowerHeap.add(HeapNode(id=i, age= i + w, priority = v[i]))


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
            val task = upperHeap.poll()
            //upperHeap.remove(task)
            //println("Task: $task")
            task?.let {
                lowerHeap.add(it)
            }
        }

        var x = 0.0
        var denominator = 0
        try {
            x += lowerHeap.first().priority
            denominator++
        } catch (e: Exception) {
            0.0
            println("Lower heap is empty")
        }
        try {
            x += upperHeap.first().priority
            denominator++
        } catch (e: Exception) {
            0.0
            println("Upper heap is empty")
        }
        x /= denominator
        return@List x
    }



    val runningMedian2 = List(100) { it ->
        val sublist = v.slice(max(0,it-w)..min(99,it+w))
            .sorted()
        return@List sublist[sublist.size / 2] // median
    }
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
}