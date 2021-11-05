#include <iostream>
#include <unistd.h>
#include "RobotImpl.h"
#include "Movement.h"

RobotImpl robot;

static constexpr long cycleDuration = 100e3;
			
int main(int argc, char** argv)
{
	Movement movement{robot};
	
	while(true)
	{
		robot.Sense();
		
		// Signals to robot platform
		
		// Signals from robot platform
		if(robot.shouldTriggerObstacle())
			movement.channels.tryEmitObstacle(&robot, robot.getObstacleArgs());
			
		// Signals between controllers
				
		movement.Execute();
		
		robot.Actuate();
		
		#ifdef ROBOCALC_INTERACTIVE
		
		std::cout<<"Looped. Press enter for next iteration or q to quit."<<std::endl;
		char c;
		bool processingInput = true;
		while(processingInput)
		{
			std::cin.get(c);
			switch(c)
			{
				case '\n':
				{
					processingInput = false;
					break;
				}
				case 'q':
				case 'Q':
				{
					std::cout<<"Exiting"<<std::endl;
					return 0;
					break;
				}
				default:
					break;
			}
		}
		
		#else
			usleep(cycleDuration);				
		#endif
	}
	
	return 0;
}
