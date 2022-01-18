# Wearable Sensor


The Inhaler should record IUEs and send them to the app through BLE. If the phone is not nearby, the inhaler records the IUE in non-volatile memory so that it is not lost. The inhaler blinks when it is used. Depending on the input voltage (battery state), the inhaler can blink green, yellow, or red.

The inhaler is built with the STM32WB55 MCU. The non-volatile memory is 

## Schematics and Hardware

## Inhaler Behavior

The software for the MCU is interrupt-driven; for the vast majority of the time, the MCU is doing nothing in stop mode (a low power mode). But when an event happens, the MCU is interrupted out of stop mode to process the event.

llustrates the flow of the code for the inhaler. When the MCU receives an interrupt, it exits stop mode. Then, the MCU identifies the source of the interrupt.
If the cause of the interrupt was the multi-purpose button, the MCU attempts to pair (if necessary) and bond by advertising its presence. Advertising is power consuming, so we want the MCU to do it as few times as possible. 
If the cause of the interrupt was an IUE, the MCU records the IUE, pairs and bonds (if necessary), and then transfers it to the application.

## FRAM Stack

## RTC setup

## Low Power

## Gatt Server Interface

## Microcontroller choice

We chose STM32WB55 MCU because of the following criteria:
- Integrated BLE module
- An accurate Realtime clock with a calendar feature
- Low power
- Accessible (free) development tools
- Documentation
- A development board (with an integrated debugger)

You can find more details in the microcontroller research document.

## Notes and Advice