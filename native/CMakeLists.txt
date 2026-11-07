# Builds layout-probe, which prints the size and field offsets of every SDRplay
# API structure, and the values of its enums and constants, as compiled by this
# platform's C compiler. LayoutProbeTest compares them with Layouts.java.
#
# Used by probe-layouts.sh; not meant to be run by hand.
cmake_minimum_required(VERSION 3.16)
project(jsdrplay_probe C)

set(SDRPLAY_INCLUDE_DIR "" CACHE PATH "Directory containing sdrplay_api.h")
if(NOT SDRPLAY_INCLUDE_DIR)
    message(FATAL_ERROR "SDRPLAY_INCLUDE_DIR is required")
endif()

add_executable(layout-probe layout-probe.c)
target_include_directories(layout-probe PRIVATE "${SDRPLAY_INCLUDE_DIR}")
if(MSVC)
    target_compile_definitions(layout-probe PRIVATE WIN32_LEAN_AND_MEAN)
else()
    target_compile_options(layout-probe PRIVATE -Wall)
endif()
