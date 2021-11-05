
#ifndef ROBOCALC_CONTROLLER_H_
#define ROBOCALC_CONTROLLER_H_

#include "StateMachine.h"

namespace robocalc
{
class Controller {
public:
	Controller() {}
	virtual ~Controller() {}
	virtual void Execute() {};
	virtual void Initialise() {}
};

}

#endif
