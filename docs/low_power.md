# Inhaler Low Power Mode

This pages describes how to get the low power version of the inhaler code, how to make power measurements, the current power consumption of the inhaler, and how to optimize the inhaler power consumption even further.

## Making Measurements

There are multiple ways to measure the power consumption of the nucleo board. I will discuss two techniques. The first uses an ammeter and you can power the nucleo board with whatever power source you want. For the second technique, you need to power the nucleo board with the X-NUCLEO-LPM01A expansion board (a power supply with data logging capabilities) which I will refer to as power-board.

### The first technique

For this technique, you need an ammeter but you can power the nucleo board with any power source you want.

As described in the readme doc of the **PWR_STANDBY_RTC** example,
- remove all jumpers on connector JP5 (black in below image) to avoid leakages between ST-Link circuitry and STM32WB device.
- remove jumper JP2 (blue in below image) and connect an ammeter to measure current between the 2 connectors of JP2.

![Nucleo board JP5 and JP2](pics\nucleo_jp5_jp2.png)

Be careful with this technique. The current can be anywhere from around 20mA to around 0.3uA (or even less). This is problematic because most ammeter don't support this measurement range or support it under different modes (a mode for mAs and another for uAs).

If an ammeter measures currents in this range in multiple modes, be aware that you will not be able to measure fractions of uAs while the ammeter is in mA mode. Your readings will always be 0 or too inaccurate. But the nucleo board should work.

On the other hand, if you put the ammeter in the uA mode, once the nucleo board attempts to draw mAs, the ammeter will not be able to handle that high current. Weird things start happening. For my multimeter, it simply didn't allow that much current and the nucleo board was left in an invalid state.

When I to measure the power consumption using this technique, I made sure to switch between the mA and uA of the ammeter as the nucleo board entered/exited a low power mode.

If your ammeter can't measure mAs to uAs in the same mode, I don't recommend this technique.

### The second technique

For this technique, you must use the power-board to power the nucleo board. You can find more about the power-board from its [manual](stm32wb\recommended\Power Measuring Board.pdf).











To get the low power version of the code, you need to checkout 