# STM32WB55 Microcontroller

We built the inhaler and the second version of the wearable sensor using the STM32WB55 MCU. This page explains why we chose this microcontroller and provides direction on how to get started with programming the microcontroller.

## Why we chose this Microcontroller

We chose the STM32WB55 MCU because of the following reasons:
- Integrated BLE module
- An accurate Realtime clock with a calendar feature
- Low power
- Accessible (free) development tools
- Documentation
- A development board (with an integrated debugger which saves tons of time)

You can find more details about this choice in the microcontroller research document. TODO:

## Getting Started with the STM32WB

Please follow the steps in the [Getting Started](getting_started) page to setup your development environment.

By now, you should have downloaded STM32CubeIDE. We encourage you to download STM32CubeProgrammer because it proves handy when things go wrong or while updating the firmware.

The inhaler is programmed in C, so you must be comfortable with C programming. If you came from a C++ background or want a refresher on C, you can checkout [tutorial's point C tutorial](https://www.tutorialspoint.com/cprogramming/index.htm).

The STM32WB55 Microcontroller has a lot of resources, but we found them hard to get started with. Instead, we recommend this [tutorial series](https://youtube.com/playlist?list=PLEBQazB0HUyRYuzfi4clXsKUSgorErmBv) on generic STM32 Microcontrollers from digikey. Each video has a written tutorial which you can access from the video's description. We recommend you checkout all of the series and practice setting up a new project and programming it rather than jumping directly into the inhaler/wearable code.

The STM32WB55 Microcontroller has a lot of resources. This [video series](https://youtube.com/playlist?list=PLnMKNibPkDnG9JRe2fbOOpVpWY7E4WbJ-) from ST Directs you to future resources specific to the STM32WB.

This [video](https://www.youtube.com/watch?v=zNfIGh30kSs&ab_channel=STMicroelectronics) from ST explains the **P2P_server** example both the inhaler and the wearable sensor were based on.



TODO: Split the getting started page.


 - Deep Blue tutorials: https://deepbluembedded.com/stm32-arm-programming-tutorials/
 - What people think: 
   - https://www.reddit.com/r/embedded/comments/dg66j9/anyone_working_on_stm32wb_microcontroller/
   - https://www.reddit.com/r/embedded/comments/jlrq1m/stm32wb_development_where_to_start/



