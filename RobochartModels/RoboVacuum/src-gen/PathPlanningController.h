#ifndef ROBOCALC_CONTROLLERS_PATHPLANNINGCONTROLLER_H_
#define ROBOCALC_CONTROLLERS_PATHPLANNINGCONTROLLER_H_

#include "Platform.h"
#include "RoboCalcAPI/Controller.h"
#include "DataTypes.h"

#include "PathPlanningSM.h"
#include "SpeedSM.h"
#include "SpeedSM.h"
#include "DisplacementSM.h"
#include "CleanSM.h"
#include "AngularSpeedSM.h"
#include "PID.h"
#include "PID.h"
#include "MidLevelSM.h"
#include "LinearSpeedSM.h"
#include "InputDuplicationSM.h"
#include "InputDuplicationSM.h"
#include "InputDuplicationSM.h"

class PathPlanningController: public robocalc::Controller 
{
public:
	PathPlanningController(Platform& _robot) : robot(&_robot){};
	PathPlanningController() : robot(nullptr){};
	
	~PathPlanningController() = default;
	
	void Execute()
	{
		pathPlanningSM.execute();
		speedSM.execute();
		speedSM.execute();
		displacementSM.execute();
		cleanSM.execute();
		angularSpeedSM.execute();
		pID.execute();
		pID.execute();
		midLevelSM.execute();
		linearSpeedSM.execute();
		inputDuplicationSM.execute();
		inputDuplicationSM.execute();
		inputDuplicationSM.execute();
	}
	
	struct Channels
	{
		PathPlanningController& instance;
		Channels(PathPlanningController& _instance) : instance(_instance) {}
		
		EventBuffer* tryEmitClean(void* sender, std::tuple<bool> args)
		{
			if(instance.cleanSM.canReceiveClean(args))
				instance.cleanSM.clean_in.trigger(sender, args);
				
			return &instance.cleanSM.clean_in;
		}
		
		EventBuffer* tryEmitDisplacement(void* sender, std::tuple<int> args)
		{
			if(instance.pathPlanningSM.canReceiveDisplacement(args))
				instance.pathPlanningSM.displacement_in.trigger(sender, args);
				
			return &instance.pathPlanningSM.displacement_in;
		}
		
		EventBuffer* tryEmitUltrasonic(void* sender, std::tuple<int> args)
		{
			if(instance.pathPlanningSM.canReceiveUltrasonic(args))
				instance.pathPlanningSM.ultrasonic_in.trigger(sender, args);
				
			return &instance.pathPlanningSM.ultrasonic_in;
		}
		
		EventBuffer* tryEmitBattery_level(void* sender, std::tuple<int> args)
		{
			if(instance.pathPlanningSM.canReceiveBattery_level(args))
				instance.pathPlanningSM.battery_level_in.trigger(sender, args);
				
			return &instance.pathPlanningSM.battery_level_in;
		}
		
		EventBuffer* tryEmitCharging(void* sender, std::tuple<> args)
		{
			if(instance.pathPlanningSM.canReceiveCharging(args))
				instance.pathPlanningSM.charging_in.trigger(sender, args);
				
			return &instance.pathPlanningSM.charging_in;
		}
		
		EventBuffer* tryEmitTarget_speed(void* sender, std::tuple<int> args)
		{
			if(instance.pID.canReceiveTarget(args))
				instance.pID.target_in.trigger(sender, args);
				
			return &instance.pID.target_in;
		}
		
		EventBuffer* tryEmitErr_output(void* sender, std::tuple<int> args)
		{
			if(instance.midLevelSM.canReceiveSpeed_adjustment(args))
				instance.midLevelSM.speed_adjustment_in.trigger(sender, args);
				
			return &instance.midLevelSM.speed_adjustment_in;
		}
		
		EventBuffer* tryEmitTarget_angle(void* sender, std::tuple<int> args)
		{
			if(instance.pID.canReceiveTarget(args))
				instance.pID.target_in.trigger(sender, args);
				
			return &instance.pID.target_in;
		}
		
		EventBuffer* tryEmitErr_output(void* sender, std::tuple<int> args)
		{
			if(instance.midLevelSM.canReceiveAngle_adjustment(args))
				instance.midLevelSM.angle_adjustment_in.trigger(sender, args);
				
			return &instance.midLevelSM.angle_adjustment_in;
		}
		
		EventBuffer* tryEmitAngle(void* sender, std::tuple<int> args)
		{
			if(instance.pID.canReceiveActual(args))
				instance.pID.actual_in.trigger(sender, args);
				
			return &instance.pID.actual_in;
		}
		
		EventBuffer* tryEmitMove_forward(void* sender, std::tuple<> args)
		{
			if(instance.midLevelSM.canReceiveMove_forward(args))
				instance.midLevelSM.move_forward_in.trigger(sender, args);
				
			return &instance.midLevelSM.move_forward_in;
		}
		
		EventBuffer* tryEmitTurn(void* sender, std::tuple<Direction> args)
		{
			if(instance.midLevelSM.canReceiveTurn(args))
				instance.midLevelSM.turn_in.trigger(sender, args);
				
			return &instance.midLevelSM.turn_in;
		}
		
		EventBuffer* tryEmitSp(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.linearSpeedSM.canReceiveSpeed_l(args))
				instance.linearSpeedSM.speed_l_in.trigger(sender, args);
				
			return &instance.linearSpeedSM.speed_l_in;
		}
		
		EventBuffer* tryEmitLinear_speed(void* sender, std::tuple<int> args)
		{
			if(instance.pID.canReceiveActual(args))
				instance.pID.actual_in.trigger(sender, args);
				
			return &instance.pID.actual_in;
		}
		
		EventBuffer* tryEmitAcc_l(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.inputDuplicationSM.canReceiveInput(args))
				instance.inputDuplicationSM.input_in.trigger(sender, args);
				
			return &instance.inputDuplicationSM.input_in;
		}
		
		EventBuffer* tryEmitOutput1(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.speedSM.canReceiveAcc(args))
				instance.speedSM.acc_in.trigger(sender, args);
				
			return &instance.speedSM.acc_in;
		}
		
		EventBuffer* tryEmitOutput2(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.angularSpeedSM.canReceiveAcc_l(args))
				instance.angularSpeedSM.acc_l_in.trigger(sender, args);
				
			return &instance.angularSpeedSM.acc_l_in;
		}
		
		EventBuffer* tryEmitAcc_r(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.inputDuplicationSM.canReceiveInput(args))
				instance.inputDuplicationSM.input_in.trigger(sender, args);
				
			return &instance.inputDuplicationSM.input_in;
		}
		
		EventBuffer* tryEmitOutput1(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.speedSM.canReceiveAcc(args))
				instance.speedSM.acc_in.trigger(sender, args);
				
			return &instance.speedSM.acc_in;
		}
		
		EventBuffer* tryEmitOutput2(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.angularSpeedSM.canReceiveAcc_r(args))
				instance.angularSpeedSM.acc_r_in.trigger(sender, args);
				
			return &instance.angularSpeedSM.acc_r_in;
		}
		
		EventBuffer* tryEmitSp(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.inputDuplicationSM.canReceiveInput(args))
				instance.inputDuplicationSM.input_in.trigger(sender, args);
				
			return &instance.inputDuplicationSM.input_in;
		}
		
		EventBuffer* tryEmitOutput1(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.displacementSM.canReceiveSp(args))
				instance.displacementSM.sp_in.trigger(sender, args);
				
			return &instance.displacementSM.sp_in;
		}
		
		EventBuffer* tryEmitOutput2(void* sender, std::tuple<TripleAxis> args)
		{
			if(instance.linearSpeedSM.canReceiveSpeed_r(args))
				instance.linearSpeedSM.speed_r_in.trigger(sender, args);
				
			return &instance.linearSpeedSM.speed_r_in;
		}
		
		EventBuffer* tryEmitStop(void* sender, std::tuple<> args)
		{
			if(instance.midLevelSM.canReceiveStop(args))
				instance.midLevelSM.stop_in.trigger(sender, args);
				
			return &instance.midLevelSM.stop_in;
		}
		
	};
	
	Channels channels{*this};
	
private:
	Platform* robot;
	PathPlanningSM_StateMachine<Channels> pathPlanningSM{*robot, channels};
	SpeedSM_StateMachine<Channels> speedSM{*robot, channels};
	SpeedSM_StateMachine<Channels> speedSM{*robot, channels};
	DisplacementSM_StateMachine<Channels> displacementSM{*robot, channels};
	CleanSM_StateMachine<Channels> cleanSM{*robot, channels};
	AngularSpeedSM_StateMachine<Channels> angularSpeedSM{*robot, channels};
	PID_StateMachine<Channels> pID{*robot, channels};
	PID_StateMachine<Channels> pID{*robot, channels};
	MidLevelSM_StateMachine<Channels> midLevelSM{*robot, channels};
	LinearSpeedSM_StateMachine<Channels> linearSpeedSM{*robot, channels};
	InputDuplicationSM_StateMachine<Channels> inputDuplicationSM{*robot, channels};
	InputDuplicationSM_StateMachine<Channels> inputDuplicationSM{*robot, channels};
	InputDuplicationSM_StateMachine<Channels> inputDuplicationSM{*robot, channels};
};

#endif
