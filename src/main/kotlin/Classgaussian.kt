package wasm.project.demo

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class Classgaussian: Iterator<Double> {
    val random = Random(seed=42)
    private var ready = false
    private var r =0.0
    private var theta =0.0

    override fun next(): Double {
        return if (ready) {
            ready = !ready
            return r * sin(theta)
        } else {
            ready = !ready
            theta = 2.0 * Math.PI * random.nextDouble()
            r = sqrt(-2.0 * ln(x = random.nextDouble()))
            return r * cos(theta)
        }
    }

    override fun hasNext(): Boolean = true
}