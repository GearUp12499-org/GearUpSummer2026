package org.firstinspires.ftc.teamcode.Tasks

import android.util.Log
import io.github.gearup12499.taskshark.systemPackages
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.taskshark.Lock
import org.firstinspires.ftc.teamcode.taskshark.Task
import org.firstinspires.ftc.teamcode.taskshark.prefabs.OneShot
import kotlin.math.abs

class ShooterImpl(private val hw: HardwareMap) : Task() {
    companion object {
        private val LOCK_ROOT = Lock.StrLock("shooter_impl")

        /**
         * Encoder ticks per second.
         */
        private const val ACCEPTABLE_VELOCITY_DIFF = 30.0

        init {
            systemPackages.add(ShooterImpl::class.qualifiedName!!)
        }
    }

    val lock = LOCK_ROOT.derive()
    private var target = 0.0

    val defaultPushThreshold = 200
    var pushThreshold = defaultPushThreshold

    var mode = false

    override fun onStart() {
        hw.setupShooterVel()
    }

    override fun onTick(): Boolean {
        val targetMode = target - hw.shoot1Vel <= pushThreshold
        if (targetMode != mode) if (targetMode) hw.shoot1Vel = target
        mode = targetMode
        if (!mode)
            hw.setShooterPower(1.0)
        hw.copyShooterPower()
        return false
    }

    override fun onFinish(completedNormally: Boolean) {

    }

    fun setTarget(vel: Double) {
        target = vel
        mode = false
    }

    fun setTargetAsync(vel: Double) = setTargetAsync { vel }
    inline fun setTargetAsync(crossinline vel: () -> Double) = OneShot { setTarget(vel()) }

    @JvmOverloads
    fun awaitTarget(minimumDuration: Double = 0.5, maximumDuration: Double = -1.0) =
        object : Task() {
            init {
                require(lock)
            }

            private val targetDuration = (minimumDuration * 1e9).toLong()
            private var lastMetAt = 0L
            private var start = 0L

            override fun onStart() {
                val now = System.nanoTime()
                lastMetAt = now
                start = now
            }

            override fun onTick(): Boolean {
                val now = System.nanoTime()
                if (maximumDuration > 0 && now - start > maximumDuration * 1e9) return true
                val currentVelocity = hw.shoot1Vel
                Log.i("Shooter", "$currentVelocity -> $target = ${abs(currentVelocity - target)}")
                if (!(abs(currentVelocity - target) < ACCEPTABLE_VELOCITY_DIFF)) {
                    lastMetAt = now
                    return false
                }
                return (now - lastMetAt) >= targetDuration
            }

            override fun onFinish(completedNormally: Boolean) {

            }
        }
}

//    @JvmOverloads
//    fun setTargetAndWait(
//        velocity: Double,
//        minDuration: Double = 0.5,
//        maxDuration: Double = -1.0
//    ) = setTargetAndWait(minDuration, maxDuration) { velocity }

//    @JvmOverloads
//    inline fun setTargetAndWait(
//        minDuration: Double = 0.5,
//        maxDuration: Double = -1.0,
//        crossinline velocity: () -> Double
//    ) = VirtualGroup {
//        add(OneShot { setTarget(velocity()) })
//            .then(awaitTarget(minDuration, maxDuration))
//    }.require(lock)
//}