package wasm.project.demo

import java.util.TreeMap

class DirectAccessMinHeap<S: Comparable<S>,T>(minHeap: Boolean=true):
    MutableCollection<Task<S,T>> where T : Comparable<T>, T : Number
{
    private val heap = mutableListOf<Task<S,T>>()
    private val indexMap = TreeMap<S, Int>()
    private val comparator: Comparator<T> = if (minHeap) {
        Comparator.naturalOrder()
    } else {
        Comparator.reverseOrder()
    }

    override val size: Int
        get() = heap.size

    override fun contains(element: Task<S,T>): Boolean = indexMap.containsKey(element.id)
    //  fun contains(id: String): Boolean = indexMap.containsKey(id)

    override fun containsAll(elements: Collection<Task<S,T>>): Boolean = elements.all { indexMap.containsKey(it.id) }
    // fun containsAll(ids: Collection<String>): Boolean = ids.all { indexMap.containsKey(it) }

    override fun isEmpty(): Boolean = heap.isEmpty()


    override fun iterator(): MutableIterator<Task<S,T>> {
        val myiter: MutableIterator<Task<S,T>> =  object : MutableIterator<Task<S,T>> {

            override fun hasNext(): Boolean = !isEmpty()

            override fun next(): Task<S,T> = heap.first()

            override fun remove() {
                if (heap.isEmpty()) {
                    throw NoSuchElementException("Heap is empty, cannot remove element.")
                }

                val last = heap.removeAt(heap.lastIndex)
                indexMap.remove(heap.first().id)
                //recreate the heap structure
                if (heap.isNotEmpty()) {
                    heap[0] = last
                    indexMap[last.id] = 0
                    siftDown(0)
                }
            }
        }
        return myiter
    }


    override fun add(element: Task<S,T>): Boolean {
        heap.add(element)
        indexMap[element.id] = heap.lastIndex
        siftUp(heap.lastIndex)
        return true
    }

    override fun remove(element: Task<S,T>): Boolean {
        if (heap.isEmpty()) {
            return false
        }
        val min = heap.first()
        val last = heap.removeAt(heap.lastIndex)
        indexMap.remove(min.id)
        if (heap.isNotEmpty()) {
            heap[0] = last
            indexMap[last.id] = 0
            siftDown(0)
        }
        return true
    }

    fun removeById(id: S): Boolean {
        //remove(T)
        val index = indexMap[id] ?: return false
        val last = heap.removeAt(heap.lastIndex)
        indexMap.remove(id)
        if (index < heap.size) {
            heap[index] = last
            indexMap[last.id] = index
            siftDown(index)
        }
        return true
    }


    override fun addAll(elements: Collection<Task<S,T>>): Boolean {
        elements.forEach {
            if (!add(it)) {
                return false // If any element fails to add, return false
            }
        }
        return true // All elements added successfully
    }

    override fun removeAll(elements: Collection<Task<S,T>>): Boolean {
        elements.forEach { e ->
            if (heap.contains(e)) {
                if (!remove(e)) return false
            }
        }
        return true // All elements removed successfully
    }

    fun removeAllById(ids: Collection<S>): Boolean {
        ids.forEach { id ->
            if (indexMap.containsKey(id)) {
                if (!removeById(id)) return false
            }
        }
        return true // All elements removed successfully
    }

    fun removeSmallId(id: S): Boolean {
        if (heap.isEmpty() || indexMap.isEmpty()) {
            return true // Nothing to remove
        }
        while (indexMap.firstKey() <= id) {
            val minId = indexMap.firstKey()
             // No more elements to remove
            if (!removeById(minId)) return false
        }
        return true
    }

    override fun retainAll(elements: Collection<Task<S,T>>): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        heap.clear()
        indexMap.clear()
    }


    fun poll(): Task<S,T>? {
        if (heap.isEmpty()) return null
        val min = heap.first()
        val last = heap.removeAt(heap.lastIndex)
        indexMap.remove(min.id)
        if (heap.isNotEmpty()) {
            heap[0] = last
            indexMap[last.id] = 0
            siftDown(0)
        }
        return min
    }

    fun get(id: S): Task<S,T>? = indexMap[id]?.let { heap[it] }

    fun updatePriority(id: S, newPriority: T) {
        val index = indexMap[id] ?: return
        val oldPriority = heap[index].priority
        heap[index].priority = newPriority
        if (newPriority < oldPriority) siftUp(index) else siftDown(index)
    }

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

            if (left < size &&
                comparator.compare(heap[left].priority, heap[smallest].priority) < 0) {
                smallest = left
            }
            if (right < size &&
                comparator.compare(heap[right].priority, heap[smallest].priority) < 0) {
                smallest = right
            }

            if (smallest == i) break
            swap(i, smallest)
            i = smallest
        }
    }

    private fun swap(i: Int, j: Int) {
        val temp = heap[i]
        heap[i] = heap[j]
        heap[j] = temp
        indexMap[heap[i].id] = i
        indexMap[heap[j].id] = j
    }
}