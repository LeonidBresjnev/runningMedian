package wasm.project.demo

data class Task<S: Comparable<S>,T: Comparable<T>>(val id: S, var priority: T): Comparable<Task<S,T>> {

    override fun compareTo(other: Task<S,T>): Int {
        return this.priority.compareTo(other.priority)
    }

}