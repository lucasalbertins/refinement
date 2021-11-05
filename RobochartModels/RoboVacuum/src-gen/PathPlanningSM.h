#ifndef ROBOCALC_STATEMACHINES_PATHPLANNINGSM_H_
#define ROBOCALC_STATEMACHINES_PATHPLANNINGSM_H_

#include "RoboCalcAPI/StateMachine.h"

#ifndef ROBOCALC_THREAD_SAFE
#define THREAD_SAFE_ONLY(x)
#else
#include <mutex>
#define THREAD_SAFE_ONLY(x) x
#endif

#include "Platform.h"
#include "RoboCalcAPI/Timer.h"
#include "Functions.h"
#include "DataTypes.h"
#include <assert.h>
#include <set>

using namespace robocalc;
using namespace robocalc::functions;

template<typename Channels>
class PathPlanningSM_StateMachine : public StateMachine<Platform, PathPlanningSM_StateMachine<Channels>>
{
	
	// Clocks and externals
	public:
		Platform& robot;
		Channels& controller;
		
					
	// Variables
	public:
		 int cycles = 0;

	// Channels/Event buffers
	public: 	
		class Go_up_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Go_up_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Go_up_State_t(stateMachine)"<<std::endl;
				}
				
				Go_up_State_t() = delete;
			
				void enter() override
				{
					this->machine.tryEmitMove_forward(std::tuple<>{});
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Go_right_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Go_right_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Go_right_State_t(stateMachine)"<<std::endl;
				}
				
				Go_right_State_t() = delete;
			
				void enter() override
				{
					this->machine.tryEmitTurn(std::tuple<Direction>{Direction::right});
					this->machine.tryEmitMove_forward(std::tuple<>{});
					this->machine.tryEmitDisplacement(std::tuple<int>{0});
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Go_down_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Go_down_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Go_down_State_t(stateMachine)"<<std::endl;
				}
				
				Go_down_State_t() = delete;
			
				void enter() override
				{
					this->machine.tryEmitTurn(std::tuple<Direction>{Direction::right});
					this->machine.tryEmitMove_forward(std::tuple<>{});
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Go_right_again_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Go_right_again_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Go_right_again_State_t(stateMachine)"<<std::endl;
				}
				
				Go_right_again_State_t() = delete;
			
				void enter() override
				{
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Return_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Return_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Return_State_t(stateMachine)"<<std::endl;
				}
				
				Return_State_t() = delete;
			
				void enter() override
				{
					this->machine.tryEmitTurn(std::tuple<Direction>{Direction::left});
					this->machine.tryEmitTurn(std::tuple<Direction>{Direction::left});
					this->machine.tryEmitMove_forward(std::tuple<>{});
					this->machine.tryEmitClean(std::tuple<bool>{false});
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Dock_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Dock_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Dock_State_t(stateMachine)"<<std::endl;
				}
				
				Dock_State_t() = delete;
			
				void enter() override
				{
					this->machine.tryEmitTurn(std::tuple<Direction>{Direction::left});
					this->machine.tryEmitMove_forward(std::tuple<>{});
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Sleep_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Sleep_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Sleep_State_t(stateMachine)"<<std::endl;
				}
				
				Sleep_State_t() = delete;
			
				void enter() override
				{
					this->machine.tryEmitStop(std::tuple<>{});
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Check_end_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Check_end_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Check_end_State_t(stateMachine)"<<std::endl;
				}
				
				Check_end_State_t() = delete;
			
				void enter() override
				{
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Check_battery_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Check_battery_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Check_battery_State_t(stateMachine)"<<std::endl;
				}
				
				Check_battery_State_t() = delete;
			
				void enter() override
				{
				}
				
				void exit() override
				{
				}
				
				void during() override
				{
				}
		};
		
		class Resume_State_t : public robocalc::State<PathPlanningSM_StateMachine<Channels>>
		{
			public:
				explicit Resume_State_t(PathPlanningSM_StateMachine<Channels>& _sm) 
					: robocalc::State<PathPlanningSM_StateMachine<Channels>>(_sm)
				{
					std::cout<<"Constructor for Resume_State_t(stateMachine)"<<std::endl;
				}
				
				Resume_State_t() = delete;
			
				void enter() override
				{
					this->machine.tryEmitTurn(std::tuple<Direction>{Direction::right});
					this->machine.tryEmitMove_forward(std::tuple<>{});
					this->machine.tryEmitDisplacement(std::tuple<int>{0});
				}
				
				void exit() override
				{
					this->machine.tryEmitTurn(std::tuple<Direction>{Direction::left});
				}
				
				void during() override
				{
				}
		};
		
		Go_up_State_t go_up_State{robot, *this};
		Go_right_State_t go_right_State{robot, *this};
		Go_down_State_t go_down_State{robot, *this};
		Go_right_again_State_t go_right_again_State{robot, *this};
		Return_State_t return_State{robot, *this};
		Dock_State_t dock_State{robot, *this};
		Sleep_State_t sleep_State{robot, *this};
		Check_end_State_t check_end_State{robot, *this};
		Check_battery_State_t check_battery_State{robot, *this};
		Resume_State_t resume_State{robot, *this};
		
		EventBuffer* blockedByMove_forward = nullptr;
		
		inline void tryEmitMove_forward(std::tuple<> args)
		{
			blockedByMove_forward = controller.tryEmitMove_forward(this, args);
		}
		
		bool canReceiveMove_forward(std::tuple<> args)
		{
			if(blockedByMove_forward != nullptr)
				if(blockedByMove_forward->getSender() == this)
					return false;
					
			blockedByMove_forward = nullptr;
			
			switch(currentState)
			{
				case s_Go_up:
				{
					
					
					
					return false;
				}
				case s_Go_right:
				{
					
					
					
					return false;
				}
				case s_Go_down:
				{
					
					
					
					return false;
				}
				case s_Go_right_again:
				{
					
					
					
					return false;
				}
				case s_Return:
				{
					
					
					
					return false;
				}
				case s_Dock:
				{
					
					
					
					return false;
				}
				case s_Sleep:
				{
					
					
					
					return false;
				}
				case s_Check_end:
				{
					
					
					
					return false;
				}
				case s_Check_battery:
				{
					
					
					
					return false;
				}
				case s_Resume:
				{
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Move_forward_t : public EventBuffer
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
		} move_forward_in;
		
		EventBuffer* blockedByTurn = nullptr;
		
		inline void tryEmitTurn(std::tuple<Direction> args)
		{
			blockedByTurn = controller.tryEmitTurn(this, args);
		}
		
		bool canReceiveTurn(std::tuple<Direction> args)
		{
			if(blockedByTurn != nullptr)
				if(blockedByTurn->getSender() == this)
					return false;
					
			blockedByTurn = nullptr;
			
			switch(currentState)
			{
				case s_Go_up:
				{
					
					
					
					return false;
				}
				case s_Go_right:
				{
					
					
					
					return false;
				}
				case s_Go_down:
				{
					
					
					
					return false;
				}
				case s_Go_right_again:
				{
					
					
					
					return false;
				}
				case s_Return:
				{
					
					
					
					return false;
				}
				case s_Dock:
				{
					
					
					
					return false;
				}
				case s_Sleep:
				{
					
					
					
					return false;
				}
				case s_Check_end:
				{
					
					
					
					return false;
				}
				case s_Check_battery:
				{
					
					
					
					return false;
				}
				case s_Resume:
				{
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Turn_t : public EventBuffer
		{
			THREAD_SAFE_ONLY(std::mutex _mutex;)
			std::tuple<Direction> _args;
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
			
			void trigger(void* sender, std::tuple<Direction> args)
			{
				THREAD_SAFE_ONLY(std::lock_guard<std::mutex> lock{_mutex};)
				_args = args;
				_sender = sender;
			}
		} turn_in;
		
		EventBuffer* blockedByStop = nullptr;
		
		inline void tryEmitStop(std::tuple<> args)
		{
			blockedByStop = controller.tryEmitStop(this, args);
		}
		
		bool canReceiveStop(std::tuple<> args)
		{
			if(blockedByStop != nullptr)
				if(blockedByStop->getSender() == this)
					return false;
					
			blockedByStop = nullptr;
			
			switch(currentState)
			{
				case s_Go_up:
				{
					
					
					
					return false;
				}
				case s_Go_right:
				{
					
					
					
					return false;
				}
				case s_Go_down:
				{
					
					
					
					return false;
				}
				case s_Go_right_again:
				{
					
					
					
					return false;
				}
				case s_Return:
				{
					
					
					
					return false;
				}
				case s_Dock:
				{
					
					
					
					return false;
				}
				case s_Sleep:
				{
					
					
					
					return false;
				}
				case s_Check_end:
				{
					
					
					
					return false;
				}
				case s_Check_battery:
				{
					
					
					
					return false;
				}
				case s_Resume:
				{
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Stop_t : public EventBuffer
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
		} stop_in;
		
		EventBuffer* blockedByUltrasonic = nullptr;
		
		inline void tryEmitUltrasonic(std::tuple<int> args)
		{
			blockedByUltrasonic = controller.tryEmitUltrasonic(this, args);
		}
		
		bool canReceiveUltrasonic(std::tuple<int> args)
		{
			if(blockedByUltrasonic != nullptr)
				if(blockedByUltrasonic->getSender() == this)
					return false;
					
			blockedByUltrasonic = nullptr;
			
			switch(currentState)
			{
				case s_Go_up:
				{
					{
						const auto& u = std::get<0>(args);
						if((u) >= (cliff))
						{
							this->u = u;
							return true;
						}
					}
					
					
					
					return false;
				}
				case s_Go_right:
				{
					{
						const auto& u = std::get<0>(args);
						if((u) >= (cliff))
						{
							this->u = u;
							return true;
						}
					}
					
					
					
					return false;
				}
				case s_Go_down:
				{
					{
						const auto& u = std::get<0>(args);
						if((u) >= (cliff))
						{
							this->u = u;
							return true;
						}
					}
					
					
					
					return false;
				}
				case s_Go_right_again:
				{
					{
						const auto& u = std::get<0>(args);
						if((u < (cliff))
						{
							this->u = u;
							return true;
						}
					}
					{
						const auto& u = std::get<0>(args);
						if((u) >= (cliff))
						{
							this->u = u;
							return true;
						}
					}
					
					
					
					return false;
				}
				case s_Return:
				{
					{
						const auto& u = std::get<0>(args);
						if((u) >= (cliff))
						{
							this->u = u;
							return true;
						}
					}
					
					
					
					return false;
				}
				case s_Dock:
				{
					
					
					
					return false;
				}
				case s_Sleep:
				{
					
					
					
					return false;
				}
				case s_Check_end:
				{
					
					
					
					return false;
				}
				case s_Check_battery:
				{
					
					
					
					return false;
				}
				case s_Resume:
				{
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Ultrasonic_t : public EventBuffer
		{
			THREAD_SAFE_ONLY(std::mutex _mutex;)
			std::tuple<int> _args;
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
			
			void trigger(void* sender, std::tuple<int> args)
			{
				THREAD_SAFE_ONLY(std::lock_guard<std::mutex> lock{_mutex};)
				_args = args;
				_sender = sender;
			}
		} ultrasonic_in;
		
		EventBuffer* blockedByBattery_level = nullptr;
		
		inline void tryEmitBattery_level(std::tuple<int> args)
		{
			blockedByBattery_level = controller.tryEmitBattery_level(this, args);
		}
		
		bool canReceiveBattery_level(std::tuple<int> args)
		{
			if(blockedByBattery_level != nullptr)
				if(blockedByBattery_level->getSender() == this)
					return false;
					
			blockedByBattery_level = nullptr;
			
			switch(currentState)
			{
				case s_Go_up:
				{
					
					
					
					return false;
				}
				case s_Go_right:
				{
					
					
					
					return false;
				}
				case s_Go_down:
				{
					
					
					
					return false;
				}
				case s_Go_right_again:
				{
					
					
					
					return false;
				}
				case s_Return:
				{
					
					
					
					return false;
				}
				case s_Dock:
				{
					
					
					
					return false;
				}
				case s_Sleep:
				{
					
					
					
					return false;
				}
				case s_Check_end:
				{
					
					
					
					return false;
				}
				case s_Check_battery:
				{
					{
						const auto& b = std::get<0>(args);
						if((b) > (battery_low))
						{
							this->b = b;
							return true;
						}
					}
					{
						const auto& b = std::get<0>(args);
						if((b) <= (battery_low))
						{
							this->b = b;
							return true;
						}
					}
					
					
					
					return false;
				}
				case s_Resume:
				{
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Battery_level_t : public EventBuffer
		{
			THREAD_SAFE_ONLY(std::mutex _mutex;)
			std::tuple<int> _args;
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
			
			void trigger(void* sender, std::tuple<int> args)
			{
				THREAD_SAFE_ONLY(std::lock_guard<std::mutex> lock{_mutex};)
				_args = args;
				_sender = sender;
			}
		} battery_level_in;
		
		EventBuffer* blockedByCharging = nullptr;
		
		inline void tryEmitCharging(std::tuple<> args)
		{
			blockedByCharging = controller.tryEmitCharging(this, args);
		}
		
		bool canReceiveCharging(std::tuple<> args)
		{
			if(blockedByCharging != nullptr)
				if(blockedByCharging->getSender() == this)
					return false;
					
			blockedByCharging = nullptr;
			
			switch(currentState)
			{
				case s_Go_up:
				{
					
					
					
					return false;
				}
				case s_Go_right:
				{
					
					
					
					return false;
				}
				case s_Go_down:
				{
					
					
					
					return false;
				}
				case s_Go_right_again:
				{
					
					
					
					return false;
				}
				case s_Return:
				{
					
					
					
					return false;
				}
				case s_Dock:
				{
					return true;
					
					
					
					return false;
				}
				case s_Sleep:
				{
					
					
					
					return false;
				}
				case s_Check_end:
				{
					
					
					
					return false;
				}
				case s_Check_battery:
				{
					
					
					
					return false;
				}
				case s_Resume:
				{
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Charging_t : public EventBuffer
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
		} charging_in;
		
		EventBuffer* blockedByClean = nullptr;
		
		inline void tryEmitClean(std::tuple<bool> args)
		{
			blockedByClean = controller.tryEmitClean(this, args);
		}
		
		bool canReceiveClean(std::tuple<bool> args)
		{
			if(blockedByClean != nullptr)
				if(blockedByClean->getSender() == this)
					return false;
					
			blockedByClean = nullptr;
			
			switch(currentState)
			{
				case s_Go_up:
				{
					
					
					
					return false;
				}
				case s_Go_right:
				{
					
					
					
					return false;
				}
				case s_Go_down:
				{
					
					
					
					return false;
				}
				case s_Go_right_again:
				{
					
					
					
					return false;
				}
				case s_Return:
				{
					
					
					
					return false;
				}
				case s_Dock:
				{
					
					
					
					return false;
				}
				case s_Sleep:
				{
					
					
					
					return false;
				}
				case s_Check_end:
				{
					
					
					
					return false;
				}
				case s_Check_battery:
				{
					
					
					
					return false;
				}
				case s_Resume:
				{
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Clean_t : public EventBuffer
		{
			THREAD_SAFE_ONLY(std::mutex _mutex;)
			std::tuple<bool> _args;
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
			
			void trigger(void* sender, std::tuple<bool> args)
			{
				THREAD_SAFE_ONLY(std::lock_guard<std::mutex> lock{_mutex};)
				_args = args;
				_sender = sender;
			}
		} clean_in;
		
		EventBuffer* blockedByDisplacement = nullptr;
		
		inline void tryEmitDisplacement(std::tuple<int> args)
		{
			blockedByDisplacement = controller.tryEmitDisplacement(this, args);
		}
		
		bool canReceiveDisplacement(std::tuple<int> args)
		{
			if(blockedByDisplacement != nullptr)
				if(blockedByDisplacement->getSender() == this)
					return false;
					
			blockedByDisplacement = nullptr;
			
			switch(currentState)
			{
				case s_Go_up:
				{
					
					
					
					return false;
				}
				case s_Go_right:
				{
					{
						const auto& d = std::get<0>(args);
						if((d) >= (nozzle))
						{
							this->d = d;
							return true;
						}
					}
					
					
					
					return false;
				}
				case s_Go_down:
				{
					
					
					
					return false;
				}
				case s_Go_right_again:
				{
					
					
					
					return false;
				}
				case s_Return:
				{
					
					
					
					return false;
				}
				case s_Dock:
				{
					
					
					
					return false;
				}
				case s_Sleep:
				{
					
					
					
					return false;
				}
				case s_Check_end:
				{
					{
						const auto& d = std::get<0>(args);
						if((d < (nozzle))
						{
							this->d = d;
							return true;
						}
					}
					{
						const auto& d = std::get<0>(args);
						if((d) >= (nozzle))
						{
							this->d = d;
							return true;
						}
					}
					
					
					
					return false;
				}
				case s_Check_battery:
				{
					
					
					
					return false;
				}
				case s_Resume:
				{
					{
						const auto& d = std::get<0>(args);
						if((d) >= (((cycles) * (2)) * (nozzle)))
						{
							this->d = d;
							return true;
						}
					}
					
					
					
					return false;
				}
				default:
				{
					return false;
				}
			}
		}
		
		struct Displacement_t : public EventBuffer
		{
			THREAD_SAFE_ONLY(std::mutex _mutex;)
			std::tuple<int> _args;
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
			
			void trigger(void* sender, std::tuple<int> args)
			{
				THREAD_SAFE_ONLY(std::lock_guard<std::mutex> lock{_mutex};)
				_args = args;
				_sender = sender;
			}
		} displacement_in;
		
	
		enum PossibleStates
		{
			s_Go_up,
			s_Go_right,
			s_Go_down,
			s_Go_right_again,
			s_Return,
			s_Dock,
			s_Sleep,
			s_Check_end,
			s_Check_battery,
			s_Resume,
			j_i0
		} currentState = j_i0; 

	
	// Constructor
	public:
		PathPlanningSM_StateMachine<Channels>(Platform& _robot, Channels& _controller) 
			: StateMachine<Platform, PathPlanningSM_StateMachine<Channels>>(_robot, *this), robot(_robot), controller(_controller), 
			cycles(0), 
			go_up_State{*this}, go_right_State{*this}, go_down_State{*this}, go_right_again_State{*this}, return_State{*this}, dock_State{*this}, sleep_State{*this}, check_end_State{*this}, check_battery_State{*this}, resume_State{*this}
			{};
		~PathPlanningSM_StateMachine<Channels>() = default;
		PathPlanningSM_StateMachine<Channels>() = delete;
		
		bool blockedOnEvent()
		{
			if (blockedByMove_forward != nullptr)
				if (blockedByMove_forward->getSender() == this)
					return true;
			if (blockedByTurn != nullptr)
				if (blockedByTurn->getSender() == this)
					return true;
			if (blockedByStop != nullptr)
				if (blockedByStop->getSender() == this)
					return true;
			if (blockedByUltrasonic != nullptr)
				if (blockedByUltrasonic->getSender() == this)
					return true;
			if (blockedByBattery_level != nullptr)
				if (blockedByBattery_level->getSender() == this)
					return true;
			if (blockedByCharging != nullptr)
				if (blockedByCharging->getSender() == this)
					return true;
			if (blockedByClean != nullptr)
				if (blockedByClean->getSender() == this)
					return true;
			if (blockedByDisplacement != nullptr)
				if (blockedByDisplacement->getSender() == this)
					return true;
			
			return false;
		}
		
		void execute() override
		{
			
			while(tryTransitions());
			if(blockedOnEvent()) return;
						
			switch(currentState)
			{
			case s_Go_up:
				go_up_State.during();
				break;
			case s_Go_right:
				go_right_State.during();
				break;
			case s_Go_down:
				go_down_State.during();
				break;
			case s_Go_right_again:
				go_right_again_State.during();
				break;
			case s_Return:
				return_State.during();
				break;
			case s_Dock:
				dock_State.during();
				break;
			case s_Sleep:
				sleep_State.during();
				break;
			case s_Check_end:
				check_end_State.during();
				break;
			case s_Check_battery:
				check_battery_State.during();
				break;
			case s_Resume:
				resume_State.during();
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
				case s_Go_up:
				{
					if((u) >= (cliff))
					if(ultrasonic_in.getSender() != nullptr)
					{
						go_up_State.exit();
						ultrasonic_in.reset();
						go_right_State.enter();
						currentState = s_Go_right;
						return true;
					}
					
					
					break;
				}
				case s_Go_right:
				{
					if((d) >= (nozzle))
					if(displacement_in.getSender() != nullptr)
					{
						go_right_State.exit();
						displacement_in.reset();
						go_down_State.enter();
						currentState = s_Go_down;
						return true;
					}
					
					if((u) >= (cliff))
					if(ultrasonic_in.getSender() != nullptr)
					{
						go_right_State.exit();
						ultrasonic_in.reset();
						go_down_State.enter();
						currentState = s_Go_down;
						return true;
					}
					
					
					break;
				}
				case s_Go_down:
				{
					if((u) >= (cliff))
					if(ultrasonic_in.getSender() != nullptr)
					{
						go_down_State.exit();
						ultrasonic_in.reset();
						tryEmitTurn(std::tuple<Direction>{Direction::left});
						tryEmitMove_forward(std::tuple<>{});
						tryEmitDisplacement(std::tuple<int>{0});
						go_right_again_State.enter();
						currentState = s_Go_right_again;
						return true;
					}
					
					
					break;
				}
				case s_Go_right_again:
				{
					if((u < (cliff))
					if(ultrasonic_in.getSender() != nullptr)
					{
						go_right_again_State.exit();
						ultrasonic_in.reset();
						check_end_State.enter();
						currentState = s_Check_end;
						return true;
					}
					
					if((u) >= (cliff))
					if(ultrasonic_in.getSender() != nullptr)
					{
						go_right_again_State.exit();
						ultrasonic_in.reset();
						cycles = 0;
						return_State.enter();
						currentState = s_Return;
						return true;
					}
					
					
					break;
				}
				case s_Return:
				{
					if((u) >= (cliff))
					if(ultrasonic_in.getSender() != nullptr)
					{
						return_State.exit();
						ultrasonic_in.reset();
						dock_State.enter();
						currentState = s_Dock;
						return true;
					}
					
					
					break;
				}
				case s_Dock:
				{
					if(charging_in.getSender() != nullptr)
					{
						dock_State.exit();
						charging_in.reset();
						sleep_State.enter();
						currentState = s_Sleep;
						return true;
					}
					
					
					break;
				}
				case s_Sleep:
				{
					{
						sleep_State.exit();
						tryEmitTurn(std::tuple<Direction>{Direction::left});
						tryEmitTurn(std::tuple<Direction>{Direction::left});
						tryEmitMove_forward(std::tuple<>{});
						resume_State.enter();
						currentState = s_Resume;
						return true;
					}
					
					
					break;
				}
				case s_Check_end:
				{
					if((d < (nozzle))
					if(displacement_in.getSender() != nullptr)
					{
						check_end_State.exit();
						displacement_in.reset();
						go_right_again_State.enter();
						currentState = s_Go_right_again;
						return true;
					}
					
					if((d) >= (nozzle))
					if(displacement_in.getSender() != nullptr)
					{
						check_end_State.exit();
						displacement_in.reset();
						cycles = (cycles) + (1);
						check_battery_State.enter();
						currentState = s_Check_battery;
						return true;
					}
					
					
					break;
				}
				case s_Check_battery:
				{
					if((b) > (battery_low))
					if(battery_level_in.getSender() != nullptr)
					{
						check_battery_State.exit();
						battery_level_in.reset();
						tryEmitTurn(std::tuple<Direction>{Direction::left});
						go_up_State.enter();
						currentState = s_Go_up;
						return true;
					}
					
					if((b) <= (battery_low))
					if(battery_level_in.getSender() != nullptr)
					{
						check_battery_State.exit();
						battery_level_in.reset();
						return_State.enter();
						currentState = s_Return;
						return true;
					}
					
					
					break;
				}
				case s_Resume:
				{
					if((d) >= (((cycles) * (2)) * (nozzle)))
					if(displacement_in.getSender() != nullptr)
					{
						resume_State.exit();
						displacement_in.reset();
						tryEmitClean(std::tuple<bool>{true});
						go_up_State.enter();
						currentState = s_Go_up;
						return true;
					}
					
					
					break;
				}
				case j_i0:
				{
					cycles = 0;
					currentState = s_Resume;
					resume_State.enter();
					return true;
				}
				default:
					break;
			}
			
			return false;
		}
};

#endif
