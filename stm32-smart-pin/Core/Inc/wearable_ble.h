
#ifndef __WEARABLE_BLE_H
#define __WEARABLE_BLE_H

#include "app_common.h"

#include <stdint.h>

/**
 * Intializes the wearable sensor.
 */
void Wearable_Sensor_Init(void);

/**
 * This function is called when the device establishes a BLE connection.
 * It is not currently used, but future teams may want it.
 */
void Wearable_On_Connect(void);

/**
 * This function is called when the device loses a BLE connection.
 * It is not currently used, but future teams may want it.
 */
void Wearable_On_Disconnect(void);

/**
 * This function loads the 16 byte service uuid into the uuidPtr
 */
uint8_t Get_Wearable_Service_UUID(uint8_t* uuidPtr);

#endif /*__WEARABLE_BLE_H */

