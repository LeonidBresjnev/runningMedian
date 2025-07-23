package wasm.project.demo

import java.util.TreeMap
import kotlin.check

data class HeapNode<S: Comparable<S>,T: Comparable<T>>(val age: S, var priority: T, val id: Int)



class MultiDirectAccessMinHeap<S: Comparable<S>,T>(val minHeap: Boolean=true):
    MutableCollection<HeapNode<S,T>> where T : Comparable<T>, T : Number
{
    private val heap = mutableListOf<HeapNode<S,T>>()
    private val indexMap = mutableMapOf<Int, Int>()  //id to heapIndex
    private val ageToIds = TreeMap<S, MutableSet<Int>>() // Maps age to id
    private val idToAge = mutableMapOf<Int, S>() // id to age and location in the list

    private val comparator: Comparator<T> = if (minHeap) {
        Comparator.naturalOrder()
    } else {
        Comparator.reverseOrder()
    }

    fun printNodes() {
        println(heap.joinToString(separator = "\n") { it.toString() })
    }
    override val size: Int
        get() = heap.size

    override fun contains(element: HeapNode<S,T>): Boolean = ageToIds.containsKey(element.age)
    //  fun contains(id: String): Boolean = indexMap.containsKey(id)

    override fun containsAll(elements: Collection<HeapNode<S,T>>): Boolean = elements.all { ageToIds.containsKey(it.age) }
    // fun containsAll(ids: Collection<String>): Boolean = ids.all { indexMap.containsKey(it) }

    override fun isEmpty(): Boolean = heap.isEmpty()


    override fun iterator(): MutableIterator<HeapNode<S,T>> {
        val myiter: MutableIterator<HeapNode<S,T>> =  object : MutableIterator<HeapNode<S,T>> {

            override fun hasNext(): Boolean = !isEmpty()

            override fun next(): HeapNode<S,T> {
                check((heap.first().priority==(if (minHeap) heap.minOf {it.priority} else heap.maxOf{it.priority}) )) {
                    println("Heap failed")
                    println("min=${heap.minOf {it.priority}}, max=${heap.maxOf{it.priority}}, type=$minHeap")
                    println("firstvalue ${heap.first()}")
                }
                return heap.first()
            }

            override fun remove() {
                if (heap.isEmpty()) {
                    throw NoSuchElementException("Heap is empty, cannot remove element.")
                }

                val last = heap.removeAt(heap.lastIndex)
                val heapId = heap.first().id
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
                    indexMap[last.id] = 0
                    siftDown(index=0)
                }
            }
        }
        return myiter
    }


    override fun add(element: HeapNode<S,T>): Boolean {
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Badd, inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}

        heap.add(element)
        indexMap[element.id] = heap.lastIndex
        if (ageToIds.containsKey(element.age)) {
            ageToIds[element.age]!!.add(element.id)
        } else {
            ageToIds[element.age] = mutableSetOf(element.id)
        }
        idToAge[element.id] = element.age
        siftUp(heap.lastIndex)
        /*println("heap: ${heap.joinToString { it.toString() }}")
        println("indexMap: ${indexMap.entries.joinToString { "${it.key} -> ${it.value}" }}")
        println("ageToIds: ${ageToIds.entries.joinToString { "${it.key} -> ${it.value}" }}")
        println("idToAge: ${idToAge.entries.joinToString { "${it.key} -> ${it.value}" }}")*/
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Badd, inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}
        validate()
        return true
    }

    override fun remove(element: HeapNode<S,T>): Boolean {
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Bremove, inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}

        if (heap.isEmpty()) {
            return false
        }
        val min = heap.first()
        val last = heap.removeAt(heap.lastIndex)
        //println("A")
       // println("O")
        indexMap.remove(min.id)

        ageToIds[idToAge[min.id]!!]!!.remove(min.id)
        if (ageToIds[idToAge[min.id]!!]!!.isEmpty()) {
            ageToIds.remove(key=idToAge[min.id]!!)
            //println("B1")
        }
        idToAge.remove(key= min.id)
        //println("D")
        //recreate the heap structure
        if (heap.isNotEmpty()) {
            heap[0] = last
            indexMap[last.id] = 0
            siftDown(index=0)
        }
        check(indexMap.size==ageToIds.values.sumOf { it.size }) {"Aremove, inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}"}
        validate()
        return true
    }

    fun removeByAge(age: S): Boolean {
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
            val index = indexMap[id]!! /*?: return false*/
            val last = heap.removeAt(heap.lastIndex)
            //println("last= $last")
            indexMap.remove(key=id)
            if (index < heap.size) {
                //println("Removing id: $id with index: $index")
                heap[index] = last
                indexMap[last.id] = index
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

    fun removeById(id: Int): Boolean {
        //remove(T)
        val index = indexMap[id] ?: return false
        val last = heap.removeAt(heap.lastIndex)
        indexMap.remove(id)
        ageToIds[idToAge[id]!!]!!.remove(element=id)
        if (ageToIds[idToAge[id]!!]!!.isEmpty()) {
            idToAge.remove(key=id)
        } else {
            ageToIds.remove(key= idToAge[id]!!)
        }
        if (index < heap.size) {
            heap[index] = last
            indexMap[last.id] = index
            siftDown(index)
        }
        return true
    }


    override fun addAll(elements: Collection<HeapNode<S,T>>): Boolean {
        elements.forEach {
            if (!add(it)) {
                return false // If any element fails to add, return false
            }
        }
        validate()
        return true // All elements added successfully
    }

    override fun removeAll(elements: Collection<HeapNode<S,T>>): Boolean {
        elements.forEach { e ->
            if (heap.contains(e)) {
                if (!remove(element=e)) return false
            }
        }
        validate()
        return true // All elements removed successfully
    }

    fun removeAllByAge(ages: Collection<S>): Boolean {
        ages.forEach { age ->
            if (ageToIds.containsKey(age)) {
                if (!removeByAge(age)) return false
            }
        }
        return true // All elements removed successfully
    }

    fun removeSmallAges(age: S): Boolean {
        check(indexMap.size==ageToIds.values.sumOf { it.size } && indexMap.size==heap.size) {
            "Bremove inconsistent sizes: ${indexMap.size} ${ageToIds.values.sumOf { it.size }}, ${heap.size}"}

        //println("sizes: ${indexMap.size}, ${ageToIds.values.sumOf { it.size }}")
        if (heap.isEmpty() || indexMap.isEmpty()) {
            return true // Nothing to remove
        }
        //println("agetoids size: ${ageToIds.size}, age: $age")

        while (ageToIds.isNotEmpty() && ageToIds.firstKey() <= age) {
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

    override fun retainAll(elements: Collection<HeapNode<S,T>>): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        heap.clear()
        indexMap.clear()
    }


    fun poll(): HeapNode<S,T>? {
        if (heap.isEmpty()) return null
        val min = heap.first()
        remove(element=min)
        return min
    }

    //fun get(id: S): HeapNode<S,T>? = indexMap[id]?.let { heap[it] }
/*
    fun updatePriority(id: S, newPriority: T) {
        val index = indexMap[id] ?: return
        val oldPriority = heap[index].priority
        heap[index].priority = newPriority
        if (newPriority < oldPriority) siftUp(index) else siftDown(index)
    }*/

    private fun siftUp(index: Int) {
        var i = index
        while (i > 0) {
            val parent = (i - 1) / 2
            if (comparator.compare(heap[i].priority, heap[parent].priority) >= 0) break
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
                comparator.compare(heap[left].priority, heap[smallest].priority) < 0) {
                smallest = left
            }
            if (right < size &&
                comparator.compare(heap[right].priority, heap[smallest].priority) < 0) {
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

                check(comparator.compare(heap[i].priority, heap[left].priority) < 0) {
                    "Left child is greater than parent, parent=${heap[i]}, child=${heap[left]}"

                }
            }
            if (right<heap.size) {
                    check(comparator.compare(heap[i].priority, heap[right].priority) < 0) { "Right child is greater than parent" }
            }
        }
    }

    private fun swap(i: Int, j: Int) {
        val temp = heap[i]
        heap[i] = heap[j]
        heap[j] = temp
        indexMap[heap[i].id ] = i
        indexMap[heap[j].id ] = j
    }
}