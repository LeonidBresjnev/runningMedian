import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wasm.project.demo.HeapNode
import wasm.project.demo.MultiDirectAccessMinHeap
import wasm.project.demo.gaussian1
import kotlin.math.pow
import kotlin.random.Random

class MultiDirectAccessMinHeapTest {
    val random = Random(seed = 42)
    val gaussian1 = random.gaussian1()
    val myMultiHeap = MultiDirectAccessMinHeap<Int, Double>(minHeap = true)
    val heapNodes = List(10) {
        val t = it.toDouble() / 10
        gaussian1.nextval() * 4.0 -(t-5).pow(2)
    }.mapIndexed { index,it ->
        HeapNode(id = index, priority = it, age= random.nextInt(0, 100))
    }


    @BeforeEach
    fun setUp() {
        myMultiHeap.clear()

    }

    @Test
    fun add() {
        heapNodes.forEach {
                myMultiHeap.add(it)
            }
        assertEquals(myMultiHeap.size, heapNodes.size)
    }
    
    @Test
    fun `remove`() {
        heapNodes.forEach {
            myMultiHeap.add(it)
        }
        val expected = heapNodes.sortedBy { it.priority }
        val actual = mutableListOf<HeapNode<Int, Double>>()
        while (myMultiHeap.isNotEmpty()) {

            val minTask = myMultiHeap.iterator().next()
            actual.add(minTask)
            myMultiHeap.remove(minTask)
        }
        val allEqual = expected.zip(actual).all { (e, a) -> e == a }
        assert(allEqual)


    }

    @Test
    fun `remove young`() {
        heapNodes.forEach {
            myMultiHeap.add(it)
        }
        val expected = heapNodes.filter { it.age>45 }.sortedBy { it.priority }

        myMultiHeap.removeSmallAges(45)
        val actual = mutableListOf<HeapNode<Int, Double>>()
        while (myMultiHeap.isNotEmpty()) {

            val minTask = myMultiHeap.iterator().next()
            actual.add(minTask)
            myMultiHeap.remove(minTask)
        }
        val allEqual = expected.zip(actual).all { (e, a) -> e == a }

        //println("Expected: ${expected.joinToString("\n")}")
        //println("Actual: ${actual.joinToString("\n")}")
        assert(allEqual)

    }

}