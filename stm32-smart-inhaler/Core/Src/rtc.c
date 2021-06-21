/*
 * rtc.c
 *
 *  Created on: Jun 14, 2021
 *      Author: youss
 */

#include "rtc.h"

//TODO: Organize this later:
// RTC is initialized in main
// Timeserver is initialized in app_entry and uses the rtc
// Prescaler is defined in app_conf.h

extern RTC_HandleTypeDef hrtc; //TODO: Consider passing this as a parameter rather than using extern.


/**
  * @brief  This function creates a timestamp using the RTC
  * @param  None
  * @retval 64 bit integer (decimal) timestamp in epoch time
  */
int64_t get_timestamp( void )
{
	struct tm currTime;
	RTC_TimeTypeDef currentTime;
	RTC_DateTypeDef currentDate;


	HAL_RTC_GetTime(&hrtc, &currentTime, RTC_FORMAT_BIN);
	HAL_RTC_GetDate(&hrtc, &currentDate, RTC_FORMAT_BIN);

	//[(SecondFraction-SubSeconds)/(SecondFraction+1)] * time_unit

	currTime.tm_year = currentDate.Year + 100;  // In fact: 2000 + 18 - 1900
	currTime.tm_mday = currentDate.Date;
	currTime.tm_mon  = currentDate.Month - 1;

	currTime.tm_hour = currentTime.Hours;
	currTime.tm_min  = currentTime.Minutes;
	currTime.tm_sec  = currentTime.Seconds;

	int64_t theTimestamp = (int64_t) mktime(&currTime);

	// convert to epoch milliseconds
	theTimestamp *= 1000;
	theTimestamp += (currentTime.SecondFraction - currentTime.SubSeconds) * 1000 / (currentTime.SecondFraction + 1);

	return (theTimestamp);
}


