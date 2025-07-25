package wasm.project.demo.runningmedian

import java.util.TreeMap
import kotlin.check
/*
class HeapNode<S: Comparable<S>,T: Comparable<T>>(val age: S, var priority: T, val id: Int)
*/


class MultiDirectAccessMinHeap(val minHeap: Boolean=true):
    MutableCollection<Triple<Double,Double, Long>>
{
    private val heap = mutableListOf<Triple<Double,Double,Long>>()
    private val indexMap = mutableMapOf<Long, Long>()  //id to heapIndex
    private val ageToIds = TreeMap<Double, MutableSet<Long>>() // Maps age to id
    private val idToAge = mutableMapOf<Long, Double>() // id to age and location in the list

    private val comparator: Comparator<Double> = if (minHeap) {
        Comparator.naturalOrder()
    } else {
        Comparator.reverseOrder()
    }

    fun printNodes() {
        println(heap.joinToString(separator = "\n") { it.toString() })
    }
    override val size: Int
        get() = heap.size

    override fun contains(element: Triple<Double,Double,Long>): Boolean = ageToIds.containsKey(element.first)
    //  fun contains(id: String): Boolean = indexMap.containsKey(id)

    override fun containsAll(elements: Collection<Triple<Double,Double,Long>>): Boolean = elements.all { ageToIds.containsKey(it.first) }
    // fun containsAll(ids: Collection<String>): Boolean = ids.all { indexMap.containsKey(it) }

    override fun isEmpty(): Boolean = heap.isEmpty()


    override fun iterator(): MutableIterator<Triple<Double,Double,Long>> {
        val myiter: MutableIterator<Triple<Double,Double,Long>> =  object : MutableIterator<Triple<Double,Double,Long>> {

            override fun hasNext(): Boolean = !isEmpty()

            override fun next(): Triple<Double,Double,Long> {
                check((heap.first().second==(if (minHeap) heap.minOf {it.second} else heap.maxOf{it.second}) )) {
                    println("Heap failed")
                    println("min=${heap.minOf {it.second}}, max=${heap.maxOf{it.second}}, type=$minHeap")
                    println("firstvalue ${heap.first()}")
                }
                return heap.first()
            }

            override fun remove() {
                if (heap.isEmpty()) {
                    throw NoSuchElementException("Heap is empty, cannot remove element.")
                }

                val last = heap.removeAt(heap.lastIndex)
                val heapId = heap.first().third
                indexMap.remove(key=heapId)
                ageToIds[idToAge[heapId]!!]!!.remove(element=heapId)
                if (ageToIds[idToAge[heapId]!!]!!.isEmpty()) {
                    idToAge.remove(key= heapId)
                } else {
                    ageToIds.remove(key= idToAge[heapId]!!)
                }
                //recreate the heap structure
                if (heap.isNotEmpty()) {
                    heap[0] = last
                    indexMap[last.third] = 0
                    siftDown(index=0)
                }
            }
        }
        return myiter
    }


    override fun add(element: Triple<Double,Double,Long>): Boolean {
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Badd, inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}

        heap.add(element)
        indexMap[element.third] = heap.lastIndex.toLong()
        if (ageToIds.containsKey(element.first)) {
            ageToIds[element.first]!!.add(element.third)
        } else {
            ageToIds[element.first] = mutableSetOf(element.third)
        }
        idToAge[element.third] = element.first
        siftUp(heap.lastIndex)
        /*println("heap: ${heap.joinToString { it.toString() }}")
        println("indexMap: ${indexMap.entries.joinToString { "${it.key} -> ${it.value}" }}")
        println("ageToIds: ${ageToIds.entries.joinToString { "${it.key} -> ${it.value}" }}")
        println("idToAge: ${idToAge.entries.joinToString { "${it.key} -> ${it.value}" }}")*/
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Badd, inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}
        validate()
        return true
    }

    override fun remove(element: Triple<Double,Double,Long>): Boolean {
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Bremove, inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}

        if (heap.isEmpty()) {
            return false
        }
        val min = heap.first()
        val last = heap.removeAt(heap.lastIndex)
        //println("A")
       // println("O")
        indexMap.remove(min.third)

        ageToIds[idToAge[min.third]!!]!!.remove(min.third)
        if (ageToIds[idToAge[min.third]!!]!!.isEmpty()) {
            ageToIds.remove(key=idToAge[min.third]!!)
            //println("B1")
        }
        idToAge.remove(key= min.third)
        //println("D")
        //recreate the heap structure
        if (heap.isNotEmpty()) {
            heap[0] = last
            indexMap[last.third] = 0
            siftDown(index=0)
        }
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Aremove, inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}
        validate()
        return true
    }

    fun removeByAge(age: Double): Boolean {
        //remove(T)
        validate()
        val ids = ageToIds[age] ?: return false
        //println("ids= ${ids.joinToString()}")

        ids.forEach { id ->
            validate()
            //println("heap:")
            //println(heap.joinToString(separator = "\n"))
            //println("Removing id: $id with age: $age")
            //println("Removing id: $id, indexmap: ${indexMap.values.joinToString(", ")})")
            val index = indexMap[id]!!.toInt() /*?: return false*/
            val last = heap.removeAt(heap.lastIndex)
            //println("last= $last")
            indexMap.remove(key=id)
            if (index < heap.size) {
                //println("Removing id: $id with index: $index")
                heap[index] = last
                indexMap[last.third] = index.toLong()
                siftUp(index)
                siftDown(index)
            }
            idToAge.remove(key=id)
            //println("id $id")
            validate()
        }
        ageToIds.remove(key=age)
        validate()
        return true
    }

    override fun addAll(elements: Collection<Triple<Double,Double,Long>>): Boolean {
        elements.forEach {
            if (!add(it)) {
                return false // If any element fails to add, return false
            }
        }
        validate()
        return true // All elements added successfully
    }

    override fun removeAll(elements: Collection<Triple<Double,Double,Long>>): Boolean {
        elements.forEach { e ->
            if (heap.contains(e)) {
                if (!remove(element=e)) return false
            }
        }
        validate()
        return true // All elements removed successfully
    }

    fun removeSmallAges(age: Double): Boolean {
        check(indexMap.size==ageToIds.values.sumOf { it.size } && indexMap.size==heap.size) {
            "Bremove inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}, ${heap.size}"}

        //println("sizes: ${indexMap.size}, ${ageToIds.values.sumOf { it.size }}")
        if (heap.isEmpty() || indexMap.isEmpty()) {
            return true // Nothing to remove
        }
        //println("agetoids size: ${ageToIds.size}, age: $age")

        while (ageToIds.isNotEmpty() && ageToIds.firstKey() < age) {
            // No more elements to remove
            //println("${ageToIds.firstKey()}, $age")
            //println("Removing age: ${ageToIds.firstKey()}, agetoIds size: ${ageToIds.size}")
            removeByAge(age=ageToIds.firstKey())
            validate()
            //println("ok - ${ageToIds.firstKey()}")
        }
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Aremove inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}
        validate()
        return true

    }

    override fun retainAll(elements: Collection<Triple<Double,Double,Long>>): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        heap.clear()
        indexMap.clear()
    }


    fun poll(): Triple<Double,Double,Long>? {
        if (heap.isEmpty()) return null
        val min = heap.first()
        remove(element=min)
        return min
    }

    private fun siftUp(index: Int) {
        var i = index
        while (i > 0) {
            val parent = (i - 1) / 2
            if (comparator.compare(heap[i].second, heap[parent].second) >= 0) break
            swap(i, parent)
            i = parent
        }

    }

    private fun siftDown(index: Int) {
        var i = index
        val size = heap.size
        while (true) {
            val left = 2 * i + 1
            val right = 2 * i + 2
            var smallest = i
/*
            if (left < size && heap[left].priority < heap[smallest].priority) {
                smallest = left
            }
            if (right < size && heap[right].priority < heap[smallest].priority) {
                smallest = right
            }*/

            if (left < size &&
                comparator.compare(heap[left].second, heap[smallest].second) < 0) {
                smallest = left
            }
            if (right < size &&
                comparator.compare(heap[right].second, heap[smallest].second) < 0) {
                smallest = right
            }
            //println("i=$i, left=$left, right=$right size=${heap.size}")
            //println("i=$i smallest=${heap[smallest]}")
            //if (left<heap.size) println("left=$left + smallest=${heap[left]}")
            //if (right<heap.size) println("right=$right smallest=${heap[right]}")
            if (smallest == i) break
            swap(i, smallest)
            i = smallest
        }
    }

    private fun validate() {
        for (i in 0 until heap.size) {
            val left = 2 * i + 1
            val right = 2 * i + 2
            if (left<heap.size) {

                check(comparator.compare(heap[i].second, heap[left].second) <= 0) {
                    "Left child is greater than parent, parent=${heap[i]}, child=${heap[left]}"

                }
            }
            if (right<heap.size) {
                    check(comparator.compare(heap[i].second, heap[right].second) <= 0) { "Right child is greater than parent" }
            }
        }
    }

    private fun swap(i: Int, j: Int) {
        val temp = heap[i]
        heap[i] = heap[j]
        heap[j] = temp
        indexMap[heap[i].third ] = i.toLong()
        indexMap[heap[j].third ] = j.toLong()
    }
}