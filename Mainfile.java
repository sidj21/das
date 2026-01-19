



public class Mainfile {
    public static void main(String[] args) {
        SpeedMonitor speedMonitor = new SpeedMonitor();
        DASController dasController = new DASController();  // Set target speed to 70 
        // speedMonitor is a simple mock to simulate the speed monitor behaviour
       // assuming you have implemented DASController as an observer, complete the following code

       // 1. Ensure that DASController will get notified of the speed changes
       speedMonitor.attach(dasController);
       speedMonitor.setSpeed(50);

       // 2. Activate the DAS and set the target speed
       dasController.setPressed(); // DAS should not activate right now since speed < 45KPH.

       // 3. Define a few test cases (at least two to show your implementation works), that is DAS does acceleration/deceleration based on the target speed when it is active.

       // NOTE: The "Accelerator:" outputs are causing a lot of confusion since 
       // it keeps its only copy of acceleration but the output mentions it as "new speed:". 
       // "DAS: Accelerating to reach target speed X" from the system is a better output. 

       /* Test Case TC01
        * Description: Ensure the DAS controller responds to the acceleration button when it is active.
        * Rationale: To test if the controller properly listens to acceleration buttons
        *            and continues to accelerate until the target is matched.
        * Dependencies/Assumptions: DAS is enabled. Speed monitor is attached.
        *                           The next vehicle is > 55m away.
        * Test Data: Initial speed: 50 KPH. DAS acceleration button pressed twice.
        * Procedure: 1. Press the DAS acceleration button twice. 2. Simulate the 
        *            vehicle behaviour by setting the speed to 55, then 60.
        * Expected Results: On the first button press, it should start accelerating to 55 KPH.
        *                   On the second press, the target becomes 60 KPH.
        *                   On speed update 55KPH, it should still be accelerating to 60KPH.
        *                   On speed update 60KPH, it should be maintaining its speed.
        * Acceptance Criteria: If the vehicle accelerates until currentSpeed < targetSpeed
        *                      and maintains it when they are equal. 
        */
       System.out.println("----------------------------");
       System.out.println("--- Test Case #1 --- ");
        
       System.out.println("Current speed: "+speedMonitor.getSpeed());
       System.out.println("Pressing DAS accelerate button twice.");
       System.out.println("============================");
       dasController.accelerateButtonPressed(); // Target speed is now 50
       dasController.accelerateButtonPressed();

       // First acceleration command processed. 
       // It still has 5 more KPH to accelerate to.
       // this is only to simulate the vehicle "responding" to the acceleration command sent by DAS.
       // it is explained in the report PDF (assumption 2)
       System.out.println("-- simulation info: Vehicle has reached 55KPH. -- ");
        speedMonitor.setSpeed(55); 

        // Now the vehicle has reached 60 KPH and can simply maintain the speed.
       System.out.println("-- simulation info: Vehicle accelerated 60KPH. --");
       speedMonitor.setSpeed(60);

       System.out.println("----------------------------");


       /* Test Case: TC02
        * Description: Set the vehicle speed to beyond the targetSpeed.
        * Rationale: Ensure the vehicle will naturally decelerate back to the target speed.
        * Dependencies/Assumptions: DAS is enabled. Speed monitor is attached.
        * Test Data: Initial speed: 60 KPH. Target speed: 60 KPH.
        * Procedure: 1. Increase the vehicle's speed to 75 KPH.
        * Expected Results: 1. DAS gets notified as an observer.
        *                   2. DAS should attempt to decelerate to reach target speed 60 KPH.
        * Acceptance Criteria: If the current speed is 80 KPH and DAS decelerates to achieve the target 60 KPH. 
        */
       System.out.println("--- Test Case #2 --- ");
        
       System.out.println("Current speed: "+speedMonitor.getSpeed());
       System.out.println("--Simulation info: Setting the speed to 80 KPH. -- ");
       System.out.println("============================");
       speedMonitor.setSpeed(80);

       
        // Assume that vehicle has reached 60 KPH and can simply maintain the speed.
       //System.out.println("-- simulation info: Vehicle accelerated 60KPH. --");
       speedMonitor.setSpeed(60);

       System.out.println("----------------------------");
        
        /* Test Case TC03
        * Description: Verify the acceleration button does not let drivers exceed the maximum target speed of 100KPH.
        * Rationale: To ensure the upper bounds of DAS' target speed function correctly.
        * Dependencies/Assumptions: DAS is enabled. Speed monitor is attached.
        * Test Data: Initial Speed: 60 KPH.
        * Procedure: 1. Press the acceleration button until speed is 100KPH.
        *            2. Press the acceleration button again.
        * Expected Results: 1. For the first few presses, the system should keep accelerating
        *                   in increments of 5 KPH, as the button presses come in.
        *                   2. On the button press that makes the target exceed 100KPH,
        *                   the target should stay at 100KPH (i.e. speed maintained).
        * Acceptance Criteria: If the vehicle refuses to set the target speed beyond 100 KPH.
        * */ 
       System.out.println("--- Test Case #3 --- ");
       
       System.out.println("==============");

       //to go from 60KPH to 100KPH, we need to press it 8 times.
       //the loop can also be thought of as holding the button down.
       for (int i = 0; i < 8; i++) {
        dasController.accelerateButtonPressed();
       }
       
       //simualte the vehicle reaching 100KPH
       speedMonitor.setSpeed(100);
       dasController.accelerateButtonPressed(); //target should stay at 100

       System.out.println("----------------------------");

       /* Test Case TC04
        * Description: Ensure the DAS controller responds to the deceleration button when it is active.
        * Rationale: To test if the controller properly listens to deceleration buttons
        *            and continues to decelerate until the target is matched.
        * Dependencies/Assumptions: DAS is enabled. Speed monitor is attached.
        * Test Data: Initial speed: 100 KPH. DAS deceleration button pressed four times.
        * Procedure: 1. Press the DAS deceleration button four times.
        *            2. Simulate the speed of the vehicle decreasing by setting it in the speedMonitor. 
        * Expected Results: 1. On each consecutive press, the target speed should decrement by 5KPH.
        *                   2. On the fourth press, the target speed should be 80KPH.
        *                   3. When the vehicle has decelerated to 60KPH, the speed should be maintained.
        * Acceptance Criteria: If the vehicle decelerates until currentSpeed > targetSpeed
        *                      and maintains it when they are equal. 
        */
       System.out.println("--- Test Case #4 --- ");
        
       System.out.println("Current speed: "+speedMonitor.getSpeed());
       System.out.println("Pressing DAS decelerate button four times.");
       System.out.println("============================");
       
       for (int i = 0; i < 4; i++) {
        dasController.decelerateButtonPressed();
       }

       //Simulate vehicle getting decelerated
       speedMonitor.setSpeed(80); // Speed should be getting manitained.
       System.out.println("----------------------------");
    }
}

