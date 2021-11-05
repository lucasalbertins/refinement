
#ifndef ROBOCALC_TIMER_H_
#define ROBOCALC_TIMER_H_

#include <string>

namespace robocalc {

class Timer {
public:

	Timer(std::string name) : name(name), counter(0)
	{}

	int get() 
	{
		return counter;
	}

	void set(int i) 
	{
		counter = i;
	}

	int tick() 
	{
		return ++counter;
	}

	std::string getName() 
	{
		return name;
	}
	
private:
	std::string name;
	unsigned long counter;
};

}

#endif
