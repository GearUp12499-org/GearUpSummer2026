package io.github.gearup12499.taskshark.prefabs

import org.firstinspires.ftc.teamcode.taskshark.Task

/**
 * ## Are you sure this is what you want?
 * This waits for a fixed number of *scheduler ticks*, not a real duration; for that, use [org.firstinspires.ftc.teamcode.taskshark.prefabs.Wait].
 */
open class WaitTicks(val duration: Int) : Task() {
    private var endsAt: Int = 0

    override fun onStart() {
       endsAt = scheduler!!.tickCount + duration
    }

    override fun onTick(): Boolean {
        return scheduler!!.tickCount >= endsAt
    }

    override fun onFinish(completedNormally: Boolean) {

    }
}