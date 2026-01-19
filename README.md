Simulating a Distance Assist System for a vehicle. 
It follows these rules:

> By default, the DAS should be disabled when the vehicle starts. Pressing the SET button, while driving
forward at a speed greater than 45 KPH, should activate DAS. DAS should maintain or decrease the vehicle
speed by requesting no throttle instead of braking. DAS should accelerate when the current speed is less
than the target speed, and the target vehicle is farther than 55 meters from the vehicle. Depressing the
brake or pressing the SET button again shall cause DAS to be disabled. An error event at any time,
when DAS is on, should cause the DAS to fail, and it should remain in the failed state until the vehicle is
restarted. If the Accel/Decs button is held while the target speed is less than 100 KPH, the target speed is
incremented/decreased for every second that the button is being held. Note that FOW and DAS also need
to interact with each other, and when both features are activated, the DAS decision should be overridden
by the FOW decision, if there is a conflict between two features.

The DAS is an observer listening to inputs from the vehicle's Speedometer.
