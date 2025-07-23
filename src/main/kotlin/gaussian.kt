package wasm.project.demo

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

fun Random.gaussian0(): ()->Double {
    var ready = false
    var r = 0.0
    var theta =0.0

    val next: ( )-> Double = {
        if (ready) {
            ready = !ready
            r * sin(theta)
        } else {
            ready   = !ready
            theta = 2.0 * Math.PI * this.nextDouble()
            r = sqrt(-2.0 * ln(x = this.nextDouble()))
            r * cos(theta)
        }
    }
    return next
}

fun interface Next {
    fun nextval(): Double
}

fun Random.gaussian1(): Next {
    var ready = false
    var r = 0.0
    var theta =0.0

    val next = Next {
        (if (ready) {
            ready = !ready
            r * sin(theta)
        } else {
            ready   = !ready
            theta = 2.0 * Math.PI * this.nextDouble()
            r = sqrt(-2.0 * ln(x = this.nextDouble()))
            r * cos(theta)
        })
    }
    return next
}