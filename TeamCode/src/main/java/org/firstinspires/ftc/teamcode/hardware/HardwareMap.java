package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drivers.GoBildaPinpoint2Driver;
import org.firstinspires.ftc.teamcode.taskshark.Lock;

public class HardwareMap extends HardwareMapper{

    public static final double DROP_DOWN_SWEET_SPOT = 0.49;
    public static final double DROP_DOWN_BOTTOM = 0.44;
    public static final double DROP_DOWN_TOP = 0.64;

    public static final double LEFT_KICKSTAND_NEUTRAL = 0.73;
    public static final double RIGHT_KICKSTAND_NEUTRAL = 0.41;
    public static final double LEFT_KICKSTAND_UP = 0.39;
    public static final double RIGHT_KICKSTAND_UP = 0.77;
    public static final double LEFT_KICKSTAND_BRAKE = 0.60;
    public static final double RIGHT_KICKSTAND_BRAKE = 0.54;

    public static final double SLIDER_OUT = 0.10;
    public static final double SLIDER_MIDDLE = 0.25;
    public static final double SLIDER_IN = 0.95;

    public static final double TICKS_PER_DEG = 159.5;
    public static final int TURRET_CW_STOP = 9400;
    public static final int TURRET_CCW_STOP = -9400;

    public static final double BALL_STOP_STOWED = 0.37;
    public static final double BALL_STOP_MIDDLE = 0.47;

    public static final double FLIPPER_DOWN = 0.30;
    public static final double FLIPPER_MID = 0.50;
    public static final double FLIPPER_UP = 0.70;

    public static final double HOOD_UP = 0.5578;
    public static final double HOOD_50 = 0.3700;
    public static final double HOOD_25 = 0.2756;
    public static final double HOOD_DOWN = 0.1817;

    public static final double BOTTOM_BALL_STOP = 0.54;
    public static final double BOTTOM_STOP_STOWED = 0.40;
    public static final double BOTTOM_STOP_OUT = 0.15;

    public static final double INTAKE_POWER = 1.0;
    public static final double OUTTAKE_POWER = -0.60;

    public static final double SHOOT_CLOSE_RANGE = 1260.0;
    public static final double SHOOT_MID_RANGE = 1380.0;
    public static final double SHOOT_MID_RANGE2 = 1360.0;
    public static final double SHOOT_FAR_RANGE = 1940.0;

    public static final double SHOOT_FAR_RANGE_AUTO = 1880.0;// x < 24

    public static final double SHOOT_MAX_DIST = 108.0;
    public static final double SHOOT_HOOD_UP_DIST = 32.0;
    public static final double SHOOT_MIN_DIST = 21.26; // based on 42, -42

    // UP 0.13 DOWN 0.42
    public static final double SHOOTER_STOP_UP = 0.63;
    public static final double SHOOTER_STOP_DOWN = 1;

    public static final double ACTIVE_TRACK_P = 0.000_10;
    public static final double ACTIVE_TRACK_I = 0.088 / (220.0 * 2 * 2.33);
    public static final double ACTIVE_TRACK_D = 0.000_005;

    @HardwareName("frontRight")
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx frontRight;

    @HardwareName("frontLeft")
    @Reversed
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx frontLeft;

    @HardwareName("backRight")
    @Reversed
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx backRight;

    @HardwareName("backLeft")
    @Reversed
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx backLeft;

    @HardwareName("pinpoint")
    public GoBildaPinpoint2Driver pinpoint;

    @HardwareName("shoot1")
    @Reversed
    public DcMotorEx shoot1;

    @HardwareName("shoot2")
    public DcMotorEx shoot2;

    @HardwareName("intake1")
    public DcMotorEx intake1;

    @HardwareName("intake2")
    @Reversed
    private DcMotorEx intake2;

    @HardwareName("flipper")
    public ServoImplEx flipper;

    @HardwareName("bottomBallStop")
    public ServoImplEx bottomBallStop;

    @HardwareName("shooterBallStop")
    public ServoImplEx shooterBallStop;

    @HardwareName("frontRamp")
    @DigitalMode(DigitalChannel.Mode.INPUT)
    public DigitalChannel frontRamp;

    @HardwareName("middleRamp")
    @DigitalMode(DigitalChannel.Mode.INPUT)
    public DigitalChannel middleRamp;

    @HardwareName("colorTopRight")
    public RevColorSensorV3 colorTopRight;

    @HardwareName("colorBottomRight")
    public RevColorSensorV3 colorBottomRight;

    @HardwareName("colorTopLeft")
    public RevColorSensorV3 colorTopLeft;

    @HardwareName("colorBottomLeft")
    public RevColorSensorV3 colorBottomLeft;

    @EncoderFor("intake2")
    @Reversed
    public Encoder turretEncoder;

    @HardwareName("turret1")
    public CRServo servoTurret1;

    @HardwareName("turret2")
    public CRServo servoTurret2;

    @HardwareName("limelight")
    public Limelight3A limelight;

    public HardwareMap(com.qualcomm.robotcore.hardware.HardwareMap map) {
        super(map);
        pinpoint.setOffsets(-2.933, -5.020, DistanceUnit.INCH);
        pinpoint.setEncoderResolution(GoBildaPinpoint2Driver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpoint2Driver.EncoderDirection.REVERSED, GoBildaPinpoint2Driver.EncoderDirection.FORWARD);
    }

    private boolean shooterMode = false;

    private void setupShooterPow() {
        shoot1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterMode = false;
    }

    public void setShooterPower(double power) {
        if (shooterMode) setupShooterPow();
        shoot1.setPower(power);
        shoot2.setPower(power);
    }

    public double getShoot1Vel() {
        return shoot1.getVelocity();
    }

    private void setupShooterVel1() {
        shoot1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMode = true;
    }

    public void setupShooterVel() {
        setupShooterVel1();
        shoot1.setVelocity(0);
    }

    public void setShoot1Vel(double vel) {
        if (!shooterMode) setupShooterVel1();
        shoot1.setVelocity(vel);
    }

    public void copyShooterPower() {
        if (!shooterMode) setupShooterVel1();
        shoot2.setPower(shoot1.getPower());
    }

    public void setIntakePower(double power) {
        intake1.setPower(power);
        intake2.setPower(power);
    }

    public double getTurretPower() {
        double pow1 = servoTurret1.getPower();
        double pow2 = servoTurret2.getPower();
        return (pow1 + pow2) / 2;
    }
    public void setTurretPower(double power) {
        // TODO: negate?
//        Log.i("Hardware", String.format("set the power to %.2f", -power));
        servoTurret1.setPower(-power);
        servoTurret2.setPower(-power);
    }

    public static class Locks{
        public static final Lock.StrLock DRIVE_MOTORS = new Lock.StrLock("drive_motors");

        public static final Lock.StrLock INTAKE = new Lock.StrLock("intake");

    }
}

