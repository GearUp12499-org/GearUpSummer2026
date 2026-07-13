package org.firstinspires.ftc.teamcode.taskshark.prefabs

import org.firstinspires.ftc.teamcode.taskshark.Task

/**
 * Simple task that repeatedly checks if the [condition] returns `true`.
 */
class WaitUntil(val condition: Condition) : Task() {
    override fun onStart() {

    }

    override fun onTick(): Boolean = condition.check()
    fun interface Condition {
        fun check(): Boolean
    }

    override fun onFinish(completedNormally: Boolean) {
    }

}