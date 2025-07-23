package wasm.project.demo
/*
class Elements<T: Comparable<T>>: Comparable<T> {
    override fun compareTo(other: T): Int {
        return this-other
    }

}*/
/*
class ComparableWrapper<T : Comparable<T>>(
    val value: T,
    private val reverse: Boolean = false
) : Comparable<ComparableWrapper<T>> {

    override fun compareTo(other: ComparableWrapper<T>): Int {
        val comparison = value.compareTo(other.value)
        return if (reverse) -comparison else comparison
    }
}


class MyList<T: Comparable<T>>(vararg elements: T, reverse: Boolean=false) :  Collection<ComparableWrapper<T>>
{
    private val items = mutableListOf<ComparableWrapper<T>>()

    init {
        for (element in elements) {
            items.add(ComparableWrapper(element))
        }
        if (reverse) {
            override fun T.compareTo(other: ComparableWrapper<T>): Int {
                return -value.compareTo(other.value)
            }
        }
    }
    // Collection interface implementation
    override val size: Int get() = items.size
    override fun isEmpty(): Boolean = items.isEmpty()
    override fun iterator(): Iterator<ComparableWrapper<T>> = items.iterator()
    override fun containsAll(elements: Collection<ComparableWrapper<T>>): Boolean =
        items.containsAll(elements)
    override fun contains(element: ComparableWrapper<T>): Boolean = items.contains(element)

    fun smallest(): ComparableWrapper<T>? {
        return items.minOrNull()
    }

}

fun main() {

    val myList = MyList(1, 2, 3, 4, 5)
    println("Smallest element: ${myList.smallest()?.value}")
}*/

