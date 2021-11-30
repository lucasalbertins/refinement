#ifndef ROBOCALC_ROBOT_FOOTBOT_H_
#define ROBOCALC_ROBOT_FOOTBOT_H_

#include "DataTypes.h"

class FootBot {
public:
	FootBot() = default; 
	virtual ~FootBot() = default;
	virtual void Sense() {};
	virtual void Actuate() {};
	
	virtual void move(double lv, double av) = 0;

	virtual bool shouldTriggerObstacle() = 0;
	virtual std::tuple<> getObstacleArgs() = 0;
};

#endif
