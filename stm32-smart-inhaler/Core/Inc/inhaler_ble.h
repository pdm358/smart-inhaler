
#ifndef __WEARABLE_BLE_H
#define __WEARABLE_BLE_H

#include <stdint.h>

void Inhaler_Init(void);

void Inhaler_On_Connect(void);

void Inhaler_On_Disconnect(void);

uint8_t Get_Inhaler_Service_UUID(uint8_t* uuidPtr);

#endif /*__WEARABLE_BLE_H */

