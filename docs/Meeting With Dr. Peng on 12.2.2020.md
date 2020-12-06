# Meeting With Dr. Peng on 12/2/2020

I asked for Dr. Peng's advice on the Micro-processor part of the project. I asked for advice on power consumption, data-volatility, choosing a Microprocessor, and what tools to work with.

For **data-volatility**, Dr. Peng advised me to use **FRAM**. Initially, I was thinking of using EEPROM simply because that is the involatile memory in an esp32 (a microprocessor I worked with a little). But EEPROM is not designed to handle our use case. In order to write to EEPROM, you erase what is in a memory block and then write. This consumes too much power because it is a multi-step process and we need to update in whole blocks.

**FRAM is involatile** as well, but **consumes much less power**, is easier to work with, and was made for use cases like ours.

If we record timestamps, we need a **real-time clock (RTC)**.

To **choose a microprocessor**, Dr.Peng advised me to go to the website of Texas instruments (**TI**) and look through their products. He told me to search for wireless microprocessors. Search for BLE, FRAM, RTC, and everything else we need. Must be low power and supports interrupts while sleeping.

If we have the **microprocessor sleep at all times except during interrupts**, we can power the Microprocessor for a **week on cell battery** or a **month on a smart-watch battery**.

**TI uses their own TI operating system**. So the **OS and the IDE choices depend on the Microprocessor**. TI IDE can be used through the browser.

I asked Dr. Peng if we can use **PlatformIO instead of TI IDE**, he said that if the microprocessor is popular, **PlatformIO may support the Microprocessor**. But we can't be sure. I tried PlatformIO before. At the very least, the difference **between PlatformIO and Arduino IDE is like the difference between Heaven and Earth**.

Lastly, for the **App part**, Dr. Peng said a lot of his students use [Xamarin](https://dotnet.microsoft.com/apps/xamarin). He never used it before, though. Dr. Peng explained that Bluetooth is standard, so a cross-platform tool like Xamarin should provide APIs for that.

## TODO

I need to read up on Bluetooth. Initiating a connection. Apparently Bluetooth is broadcasting (multicasting). We are going to use Bluetooth Low-Energy and not just Bluetooth.

