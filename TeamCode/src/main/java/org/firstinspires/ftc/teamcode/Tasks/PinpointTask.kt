package org.firstinspires.ftc.teamcode.Tasks

import org.firstinspires.ftc.teamcode.drivers.GoBildaPinpoint2Driver
import org.firstinspires.ftc.teamcode.taskshark.Task

class PinpointTask(private val pinpoint: GoBildaPinpoint2Driver): Task() {
    override fun onStart() {

    }

    override fun onTick(): Boolean {
        pinpoint.update()
        return false
    }

    override fun onFinish(completedNormally: Boolean) {
    }
}