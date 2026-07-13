package org.firstinspires.ftc.teamcode.taskshark.prefabs

import org.firstinspires.ftc.teamcode.taskshark.Task

open class OneShot(val action: Action): Task() {

    fun interface Action{
        fun run()
    }

    override fun onStart() {
        action.run()
    }

    override fun onTick(): Boolean {
        return true
    }

    override fun onFinish(completedNormally: Boolean) {

    }
}