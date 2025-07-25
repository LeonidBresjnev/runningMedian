import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wasm.project.demo.runningmedian.MultiDirectAccessMinHeap
import wasm.project.demo.gaussian1
import kotlin.math.pow
import kotlin.random.Random

class MultiDirectAccessMinHeapTest {
    val random = Random(seed = 42)
    val gaussian1 = random.gaussian1()
    val myMultiHeap = MultiDirectAccessMinHeap(minHeap = true)
    val heapNodes = List(10) {
        val t = it.toDouble() / 10
        gaussian1.nextval() * 4.0 -(t-5).pow(2)
    }.mapIndexed { index,it ->
        Triple(
            third = index.toLong(),
            second = it,
            first= random.nextInt(0, 100).toDouble())
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
        val expected = heapNodes.sortedBy { it.second }
        val actual = mutableListOf<Triple<Double, Double,Long>>()
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
        val expected = heapNodes.filter { it.first>=45 }.sortedBy { it.second }

        myMultiHeap.removeSmallAges(45.0)
        val actual = mutableListOf<Triple<Double, Double,Long>>()
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