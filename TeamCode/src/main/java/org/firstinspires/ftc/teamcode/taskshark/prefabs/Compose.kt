package org.firstinspires.ftc.teamcode.taskshark.prefabs

import org.firstinspires.ftc.teamcode.taskshark.Task
import org.firstinspires.ftc.teamcode.taskshark.prefabs.OneShot.Action

class Compose(val action: Action): Task() {
    fun interface Action{
        fun run()
    }

    override fun onStart() {

    }

    override fun onTick(): Boolean {
        action.run()
        return false
    }

    override fun onFinish(completedNormally: Boolean) {
    }
}