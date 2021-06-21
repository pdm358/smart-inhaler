/*
 * rtc.h
 *
 *  Created on: Jun 14, 2021
 *      Author: youss
 */

#ifndef CORE_INC_RTC_H_
#define CORE_INC_RTC_H_

#include <time.h>
#include "stm32wbxx_hal.h"

int64_t get_timestamp();

/**
* This was an attempt to automatically load the compile time into the rtc.
*
* However, as it turns out, DATE and TIME don't represent UNIX time in this environment.
*
* But simply  the system time (computer time).
*
* As such, this function alone is insufficient.
*/
void get_compile_time(RTC_DateTypeDef * sDate, RTC_TimeTypeDef * sTime);

#endif /* CORE_INC_RTC_H_ */
