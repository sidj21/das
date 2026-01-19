import java.lang.Math;

public class DASController implements Observer {
    
    // Add implementation of the DASController, hint: the DASController should be an observer
    // Assume the status of features is managed by the FeatureStatus
    // Ensure that the DAS is only works when its status is activate.
    private int currentSpeed;
    private int targetSpeed;

    private int nextVehicleDistance;
    private FeatureStatus featureStatus;
    private Accelerator accelerator;
    private boolean failedState;

    // DAS can only activate when the SET button is pressed and speed is >= 45KPH.
    private static final int ENABLE_CUTOFF = 45;
    // DAS can only accelerate if it is active and at least 55m away from the vehicle in front.
    private static final int SAFE_DISTANCE_CUTOFF = 55;

    // Target speed cannot be greater than 100KM/H.
    private static final int MAX_TARGET_SPEED = 100;
    // Acceleration/Deceleration & Setting Target Speed happens in this increment.
    private static final int SPEED_INCREMENTS = 5;

    public DASController() {

        // Set initial speed.
        // Assumption: DAS is initialized when the vehicle starts.
        this.currentSpeed = 0;
        this.targetSpeed = 0;
        this.failedState = false;

        // Setting this to a constant because this simplified program does not get distance data.
        // If DAS is active and needs to accelerate, it will always accelerate.
        // Because the next vehicle is 70m away.
        this.nextVehicleDistance = 70; 

        // Initialize the other classes.
        this.featureStatus = new FeatureStatus();
        this.accelerator = new Accelerator();

        // Upon startup, DAS is inactive.
        this.featureStatus.setDASStatus(CarControlSystemTypes.FeatureStatus.INACTIVE);
    }

    /**
     * The DASController has received an update from the SpeedMonitor.
     * Update the current speed within DASController.
     * Plus, if DAS is active, adjust the speed based on the current target speed.
     * 
     * @param speed The new speed of the vehicle.
     */
    @Override
    public void update(int speed) {
        currentSpeed = speed;
        
        // This should prevent speed adjustments while Inactive or Overriden.
        CarControlSystemTypes.FeatureStatus currentStatus = featureStatus.getDASStatus();
        if (currentStatus == CarControlSystemTypes.FeatureStatus.ACTIVE) {
            adjustSpeed();
        }
    }

    /**
     * The accelerate button is pressed.
     * If the system is enabled, increment the target speed.
     * Then, have it accelerate if need be through adjustSpeed. 
     */
    public void accelerateButtonPressed() {
        CarControlSystemTypes.FeatureStatus currentStatus = featureStatus.getDASStatus();

        // This should prevent speed adjustments while Inactive or Overriden.
        if (currentStatus != CarControlSystemTypes.FeatureStatus.ACTIVE) {
            System.out.println("DAS: DAS is not active, target speed cannot be incremented.");
            return;
        }

        targetSpeed += SPEED_INCREMENTS;
        targetSpeed = Math.min(targetSpeed, MAX_TARGET_SPEED);
        System.out.println("DAS: Acceleration Button Pressed. Target speed set to: " + targetSpeed);
        adjustSpeed();
    }

    /**
     * The accelerate button is pressed.
     * If the system is enabled, decrement the target speed.
     * Then, have it decelerate if need be through adjustSpeed. 
     */
    public void decelerateButtonPressed() {
        CarControlSystemTypes.FeatureStatus currentStatus = featureStatus.getDASStatus();

        // This should prevent speed adjustments while Inactive or Overriden.
        if (currentStatus != CarControlSystemTypes.FeatureStatus.ACTIVE) {
            System.out.println("DAS: DAS is not active, target speed cannot be decremented.");
            return;
        }

        // The system description does not mention automatic deactivation due to a low speed.
        // So just preventing it from hitting a negative target speed.
        targetSpeed -= SPEED_INCREMENTS;
        targetSpeed = Math.max(targetSpeed, 0);
        System.out.println("DAS: Deceleration Button Pressed. Target speed set to " + targetSpeed);
        adjustSpeed();
    }

    /**
     * If an error is received, DAS is in a failed mode and deactivates.
     */
    public void onError() {
        this.failedState = true;
        featureStatus.setDASStatus(CarControlSystemTypes.FeatureStatus.INACTIVE);
    }

    /**
     * The SET button has been pressed.
     * If DAS is in a failed state or Overridden, return early.
     * 
     * If it is inactive:
     *  Activate it and set the target speed to match the current speed.
     * If it is active:
     *  Deactivate it and reset the target speed (there is no target to match).
     * 
     */
    public void setPressed() {
        if (failedState) {
            System.out.println("DAS: System encountered an error. It is disabled.");
            return;
        }

        CarControlSystemTypes.FeatureStatus currentStatus = featureStatus.getDASStatus();

        if (currentStatus == CarControlSystemTypes.FeatureStatus.OVERRIDEN) {
            // DAS is currently overriden and cannot be toggled.
            System.out.println("DAS: Set Button Pressed, DAS is Overriden. No changes made.");
            return;
        }
        
        if (currentStatus == CarControlSystemTypes.FeatureStatus.INACTIVE) {
            if (currentSpeed < ENABLE_CUTOFF) {
                System.out.println("DAS: Set Button Pressed. Speed is Below " + ENABLE_CUTOFF + ". No changes made.");
                return;
            }

            // SET Button Pressed
            // DAS is currently inactive and speed is greater than 45KPH
            featureStatus.setDASStatus(CarControlSystemTypes.FeatureStatus.ACTIVE);
            System.out.println("DAS: Set Button Pressed, DAS is Active.");
            
            // Maintain the current speed.
            targetSpeed = currentSpeed;
        } else {
            featureStatus.setDASStatus(CarControlSystemTypes.FeatureStatus.INACTIVE);
            System.out.println("DAS: Set Button Pressed, DAS Disabled.");
            targetSpeed = 0; // No target speed to maintain.
        }        
    }

    /**
     * The requirements don't mention at what angle it must be pressed to disable DAS.
     * The assumption is that if this is called, it is enough to deactivate DAS.
     */
    public void brakePressed() {
        featureStatus.setDASStatus(CarControlSystemTypes.FeatureStatus.INACTIVE);
        System.out.println("DAS: Brake pressed. DAS Disabled.");
    }

    /**
     * Adjusts the speed of the vehicle.
     * Precondition: DAS is activated.
     * 
     * If the current speed is less than the target, and the next vehicle is 55m away,
     * increase acceleration by 5.
     * If the current speed is greater than the target, decrease acceleration by 5.
     * Otherwise, maintain the current (target) speed.
     * 
     * If at any point, the increment or decrement amount is within 5KPH,
     * only increment or decrement as much is needed (implemented with min).
     */
    private void adjustSpeed() {
        if (currentSpeed < targetSpeed && nextVehicleDistance > SAFE_DISTANCE_CUTOFF) {
            int accelerateAmount = Math.min(targetSpeed - currentSpeed, SPEED_INCREMENTS);
            accelerator.accelerate(accelerateAmount);
            System.out.println("DAS: Accelerating to reach target speed " + targetSpeed); 
        } else if (currentSpeed > targetSpeed) {
            int decelerateAmount = Math.min(currentSpeed - targetSpeed, SPEED_INCREMENTS);
            accelerator.decelerate(decelerateAmount);
            System.out.println("DAS: Decelerating to reach target speed " + targetSpeed);  
        } else {
            System.out.println("DAS: Maintaining target speed: " + targetSpeed);
        }
    }
}

