#ifndef __ROBOT_IMPL_H__
#define __ROBOT_IMPL_H__

#include "FootBot.h"

#include <set>
#include <algorithm>
#include <iostream>

class RobotImpl : public FootBot
{
public:
		
	bool shouldTriggerObstacle() override
	{
		std::cout<<"Should trigger event 'Obstacle'? (y/n)";
		char trigger;
		
		do { std::cin >> trigger; } while((trigger != 'y') && (trigger != 'n'));
		
		return trigger == 'y';
	}
	
	std::tuple<> getObstacleArgs() override
	{
		return std::tuple<>{};
	};
	
	void move(double lv, double av) override
	{
		std::cout<<"Operation move invoked on robot platform"<<std::endl;
	}
};

#endif
