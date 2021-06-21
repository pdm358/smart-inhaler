/*
 * rtc.c
 *
 *  Created on: Jun 14, 2021
 *      Author: youss
 */

#include "rtc.h"
#include "inhaler_utilities.h"
#include "main.h"

//TODO: Organize this later:
// RTC is initialized in main
// Timeserver is initialized in app_entry and uses the rtc
// Prescaler is defined in app_conf.h

extern RTC_HandleTypeDef hrtc; //TODO: Consider passing this as a parameter rather than using extern.

/**
* This was an attempt to automatically load the compile time into the rtc.
*
* However, as it turns out, DATE and TIME don't represent UNIX time in this environment.
*
* But simply  the system time (computer time).
*
* As such, this function alone is insufficient.
*/
void get_compile_time(RTC_DateTypeDef * sDate, RTC_TimeTypeDef * sTime){
	char * date = __DATE__;
	switch(date[0]){
	case 'J':
		if(date[1] == 'a'){// Jan
			sDate->Month = 1;
		}else if (date[2] == 'n'){ // Jun
			sDate->Month = 6;
		}else{
			sDate->Month = 7; // Jul
		}
		break;
	case 'F':
		sDate->Month = 2; // Feb
		break;
	case 'M':
		if(date[2] == 'r'){ // Mar
			sDate->Month = 3;
		}else{
			sDate->Month = 5; // May
		}
		break;
	case 'A':
		if(date[2] == 'g'){ // Aug
			sDate->Month = 8;
		}else{
			sDate->Month = 4; // Apr
		}
		break;
	case 'S':
		sDate->Month = 9; // Sep
		break;
	case 'O':
		sDate->Month = 10; //Oct
		break;
	case 'N':
		sDate->Month = 11; // Nov
		break;
	case 'D':
		sDate->Month = 12; //Dec
		break;
	default:
	Error_Handler();
	}

	int i = 4;
	sDate->Date = charToInt(date[i]);
	i++;
	if(date[i] != ' '){
		sDate->Date <<= 4;
		sDate->Date += charToInt(date[i]);
		i++;
	}
	i += 3;
	sDate->Year = charToInt(date[i]) << 4;
	i++;
	sDate->Year += charToInt(date[i]);
	i++;


	// Time
	char * time = __TIME__;
	i = 0;
	sTime->Hours = charToInt(time[i]) << 4;
	i++;
	sTime->Hours += charToInt(time[i]);
	i++;

	i++; // colon

	sTime->Minutes = charToInt(time[i]) << 4;
	i++;
	sTime->Minutes += charToInt(time[i]);
	i++;

	i++; // colon

	sTime->Seconds = charToInt(time[i]) << 4;
	i++;
	sTime->Seconds += charToInt(time[i]);
	i++;

}


/**
  * @brief  This function creates a timestamp using the RTC
  * @param  None
  * @retval 64 bit integer (decimal) timestamp in epoch time
  */
int64_t get_timestamp( void )
{

	//TODO: Confirm the sleep modes don't ruin the shadow registers.
	// that when this function is called when the MCU wakes from sleep, it returns the time
	// right now rather than when it went to sleep.
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


