#ifndef ROBOCALC_STATEMACHINES_SMOVEMENT_H_
#define ROBOCALC_STATEMACHINES_SMOVEMENT_H_

#include "RoboCalcAPI/StateMachine.h"

#ifndef ROBOCALC_THREAD_SAFE
#define THREAD_SAFE_ONLY(x)
#else
#include <mutex>
#define THREAD_SAFE_ONLY(x) x
#endif

#include "FootBot.h"
#include "RoboCalcAPI/Timer.h"
#include "Functions.h"
#include "DataTypes.h"
#include <assert.h>
#include <set>

using namespace robocalc;
using namespace robocalc::functions;

template<typename Channels>
class SMovement_StateMachine : public StateMachine<FootBot, SMovement_StateMachine<Channels>>
{
	
	// Clocks and externals
	public:
		FootBot& robot;
		Channels& controller;
		
		robocalc::Timer MBC{"MBC"};
					
	// Variables
	public:
		const  double lv = 0;
		const  double PI = 0;
		 unsigned int x = 0;
		const  double av = 0;

	// Channels/Event buffers
	public: 	
		class Moving_State_t : public robocalc::State<SMovement_StateMachine<Channels>>
		{
			public:
				explicit Moving_State_t(SMovement_StateMachine<Channels>& _sm) 
					: robocalc::State<SMovement_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Moving_State_t(stateMachine)"<<std::endl;
				}
				
				Moving_State_t() = delete;
			
				void enter() override
				{
					this->machine.robot.move(this->machine.lv, 0);
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Turning_State_t : public robocalc::State<SMovement_StateMachine<Channels>>
		{
			public:
				explicit Turning_State_t(SMovement_StateMachine<Channels>& _sm) 
					: robocalc::State<SMovement_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Turning_State_t(stateMachine)"<<std::endl;
				}
				
				Turning_State_t() = delete;
			
				void enter() override
				{
					this->machine.robot.move(0, this->machine.av);
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		Moving_State_t moving_State{robot, *this};
		Turning_State_t turning_State{robot, *this};
		
		EventBuffer* blockedByObstacle = nullptr;
		
		inline void tryEmitObstacle(std::tuple<> args)
		{
			blockedByObstacle = controller.tryEmitObstacle(this, args);
		}
		
		bool canReceiveObstacle(std::tuple<> args)
		{
			if(blockedByObstacle != nullptr)
				if(blockedByObstacle->getSender() == this)
					return false;
					
			blockedByObstacle = nullptr;
			
			switch(currentState)
			{
				case s_Moving:
				{
					return true;
					
					
					
					return false;
				}
				case s_Turning:
				{
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Obstacle_t : public EventBuffer
		{
			THREAD_SAFE_ONLY(std::mutex _mutex;)
			std::tuple<> _args;
			void* _sender = nullptr;
			void* getSender() override
			{
				THREAD_SAFE_ONLY(std::lock_guard<std::mutex> lock{_mutex};)
				return _sender;
			}
			
			void reset() override
			{
				THREAD_SAFE_ONLY(std::lock_guard<std::mutex> lock{_mutex};)
				_sender = nullptr;
			}
			
			void trigger(void* sender, std::tuple<> args)
			{
				THREAD_SAFE_ONLY(std::lock_guard<std::mutex> lock{_mutex};)
				_sender = sender;
			}
		} obstacle_in;
		
	
		enum PossibleStates
		{
			s_Moving,
			s_Turning,
			j_i1
		} currentState = j_i1; 

	
	// Constructor
	public:
		SMovement_StateMachine<Channels>(FootBot& _robot, Channels& _controller) 
			: StateMachine<FootBot, SMovement_StateMachine<Channels>>(_robot, *this), robot(_robot), controller(_controller), 
			lv(0), PI(0), x(0), av(0), 
			moving_State{*this}, turning_State{*this}
			{};
		~SMovement_StateMachine<Channels>() = default;
		SMovement_StateMachine<Channels>() = delete;
		
		bool blockedOnEvent()
		{
			if (blockedByObstacle != nullptr)
				if (blockedByObstacle->getSender() == this)
					return true;
			
			return false;
		}
		
		void execute() override
		{
			MBC.tick();
			
			while(tryTransitions());
			if(blockedOnEvent()) return;
						
			switch(currentState)
			{
			case s_Moving:
				moving_State.during();
				break;
			case s_Turning:
				turning_State.during();
				break;
			default:
				break;
			}
		}
		
		bool tryTransitions() override
		{
			if(blockedOnEvent())
				return false;
			
			switch(currentState)
			{
				case s_Moving:
				{
					if(obstacle_in.getSender() != nullptr)
					{
						moving_State.exit();
						obstacle_in.reset();
						MBC.set(0);
						turning_State.enter();
						currentState = s_Turning;
						return true;
					}
					
					
					break;
				}
				case s_Turning:
				{
					if((MBC.get()) >= ((PI) / (av)))
					{
						turning_State.exit();
						moving_State.enter();
						currentState = s_Moving;
						return true;
					}
					
					
					break;
				}
				case j_i1:
				{
					currentState = s_Moving;
					moving_State.enter();
					return true;
				}
				default:
					break;
			}
			
			return false;
		}
};

#endif
