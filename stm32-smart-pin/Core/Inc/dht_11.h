/*
 * DHT.h
 *
 *  Created on: Jun 28, 2020
 *      Author: Controllerstech.com
 *
 *      Modified on May 30, 2021 for CSS 427
 */

#ifndef DHT_11_H_
#define DHT_11_H_

#include "stm32wbxx_hal.h"

typedef struct
{
	float Temperature;
	float Humidity;
}DHT_11_Data;

/**
 * To get the DHT11 data, you only need to call this function.
 *
 * Modifies the value pointed to by DHT_Data and returns a success/fail code.
 */
uint8_t get_dht11_data (DHT_11_Data *DHT_Data);

#endif /* INC_DHT_H_ */
