#ifndef ROBOCALC_API_STATE_H_
#define ROBOCALC_API_STATE_H_

#include <string>

namespace robocalc
{
	class EventBuffer
	{
	public:
		virtual void* getSender() = 0;
		virtual void reset() = 0;
	};
	
	template<class StateMachineType>
	class State;
	
	template<class RobotType, class ParentClass>
	class StateMachine
	{
	public:
		StateMachine(RobotType& _robot, ParentClass& _parent): robot(_robot), parent(_parent) {}
		StateMachine() = delete;
		
		virtual bool tryTransitions() = 0;
		virtual void execute() = 0;
		RobotType& robot;
		ParentClass& parent;
	};
	
	template<class StateMachineType>
	class State
	{
	public:
		StateMachineType& machine;
			
		State(StateMachineType& _sm) : machine(_sm) {}
		State() = delete;
		virtual void enter() = 0;
		virtual void exit() = 0;
		virtual void during() = 0;
	};
				
	template<class StateMachineType>
	class InitialState : public State<StateMachineType>
	{
	public:
		InitialState() {}
		virtual void enter() {};
		virtual void exit() {};
		virtual void during() {};
	};
	
	template<typename StateMachineType, typename RobotType, typename ParentMachineTypeName>
	class CompositeState : public State<ParentMachineTypeName>
	{
	public:
		CompositeState(RobotType& robot, ParentMachineTypeName& parent) 
			: State<ParentMachineTypeName>(parent), sm{robot, parent} {}
			
		CompositeState() = delete;
		
		virtual void enter() 
		{
		}
		
		virtual void during() 
		{
			while(sm.tryTransitions());
			sm.execute();
		}
		
		virtual void exit() 
		{
		}
		
		StateMachineType sm;
	};
}

#endif

