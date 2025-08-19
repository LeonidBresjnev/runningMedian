package wasm.project.demo.runningmedian

interface MultiDirectAccessMinHeap: MutableCollection<Triple<Double,Double, Long>> {
    fun printNodes()
    fun removeSmallAges(age: Double): Boolean
    fun poll(): Triple<Double,Double,Long>?
}