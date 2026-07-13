package org.firstinspires.ftc.teamcode.taskshark.prefabs

import org.firstinspires.ftc.teamcode.taskshark.Task

open class SentinelTask: Task() {

    var canStart: Boolean = false

    override fun onStart() {

    }

    override fun onTick(): Boolean {
        return canStart
    }

    override fun onFinish(completedNormally: Boolean) {

    }

    open fun requestStart(){
        canStart = true
    }
}