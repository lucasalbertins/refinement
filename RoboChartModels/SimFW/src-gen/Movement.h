#ifndef ROBOCALC_CONTROLLERS_MOVEMENT_H_
#define ROBOCALC_CONTROLLERS_MOVEMENT_H_

#include "FootBot.h"
#include "RoboCalcAPI/Controller.h"
#include "DataTypes.h"

#include "SMovement.h"

class Movement: public robocalc::Controller 
{
public:
	Movement(FootBot& _platform) : platform(&_platform){};
	Movement() : platform(nullptr){};
	
	~Movement() = default;
	
	void Execute()
	{
		sMovement.execute();
	}
	
	struct Channels
	{
		Movement& instance;
		Channels(Movement& _instance) : instance(_instance) {}
		
		EventBuffer* tryEmitObstacle(void* sender, std::tuple<> args)
		{
			if(instance.sMovement.canReceiveObstacle(args))
				instance.sMovement.obstacle_in.trigger(sender, args);
				
			return &instance.sMovement.obstacle_in;
		}
		
	};
	
	Channels channels{*this};
	
	FootBot* platform;
	SMovement_StateMachine<Movement> sMovement{*platform, *this, &sMovement};
};

#endif
