cmake_minimum_required (VERSION 2.8)
project (RoboCalc_Autogen)

add_library(Movement Movement.h FootBot.h)

add_executable(MainExe main.cpp Movement.h FootBot.h)
add_executable(MainExeInteractive main.cpp Movement.h FootBot.h)
target_compile_definitions(MainExeInteractive PUBLIC -DROBOCALC_INTERACTIVE)

include(CMakeCustom.cmake OPTIONAL)
