#include "wearable_ble.h"
#include "common_blesvc.h"
#include "app_ble.h"
#include "pvd.h"
#include <stdio.h>
#include "rtc.h" // TODO: Move all of the includes somewhere else.

#define SERVICE_UUID "e814c25d7107459eb25d23fec96d49da"
#define IUE_CHARACTERISTIC "d7dc7c5048ce45a49c3e243a5bb75608"

//"015529f7-554c-4138-a71e-40a2dfede10a"

typedef struct{
	uint32_t timestamp;      // 4 bytes
	uint32_t count;
} IUE_t;

typedef struct{
  uint16_t	service_handler;				        /**< Service handle */
  uint16_t	iue_characteristic_handler;	  /**< Characteristic handle */
}Inhaler_Context_t;

PLACE_IN_SECTION("BLE_DRIVER_CONTEXT") static Inhaler_Context_t inhaler_context;

static IUE_t getWearableData(){

	IUE_t data;

	static uint32_t counter = 0;
	counter++;

	data.timestamp = get_timestamp();
	data.count = counter << 24;

	return data;
}

/**
 * @brief  Event handler
 * @param  Event: Address of the buffer holding the Event
 * @retval Ack: Return whether the Event has been managed or not
 */
static SVCCTL_EvtAckStatus_t Wearable_BLE_Event_Handler(void *Event)
{
  SVCCTL_EvtAckStatus_t return_value = SVCCTL_EvtNotAck;

  hci_event_pckt *event_pckt = (hci_event_pckt *)(((hci_uart_pckt*)Event)->data);

  switch(event_pckt->evt)
  {
    case HCI_VENDOR_SPECIFIC_DEBUG_EVT_CODE:
    {
    	  evt_blecore_aci *blecore_evt = (evt_blecore_aci*)event_pckt->data;
      switch(blecore_evt->ecode)
      {
        case ACI_GATT_READ_PERMIT_REQ_VSEVT_CODE:
        {
        	aci_gatt_read_permit_req_event_rp0* read_permit_req = (aci_gatt_read_permit_req_event_rp0*)blecore_evt->data;
			if(read_permit_req->Attribute_Handle == (inhaler_context.iue_characteristic_handler + 1))
			{
				//https://community.st.com/s/question/0D53W000003xw7LSAQ/basic-ble-reading-for-stm32wb

				static IUE_t data;

				data = getWearableData();

				aci_gatt_update_char_value(inhaler_context.service_handler,
						inhaler_context.iue_characteristic_handler,
															0, /* charValOffset */
															sizeof(IUE_t), /* charValueLen */
															(uint8_t*) (&data));

				aci_gatt_allow_read(read_permit_req->Connection_Handle); // todo: consider switching the order.

				 return_value = SVCCTL_EvtNotAck;
			}
			break;
        }

        break;

        default:
        break;
      }
    }
    break; /* HCI_HCI_VENDOR_SPECIFIC_DEBUG_EVT_CODE_SPECIFIC */

    default:
      break;
  }

  return(return_value);
}/* end SVCCTL_EvtAckStatus_t */

uint8_t isWearableConnected(void){
	return APP_BLE_Get_Server_Connection_Status() == APP_BLE_CONNECTED_SERVER;
}

void Wearable_On_Connect(void){

}

void Wearable_On_Disconnect(void){

}


static uint8_t charToInt(char c){

	if( c >= '0' && c <= '9') return c - '0';
	if( c >= 'a' && c <= 'f') return c - 'a' + 10;
	if( c >= 'A' && c <= 'F') return c - 'A' + 10;

	return 0xff; //error;
}

static void Char_Array_To_128UUID(char * charArrayPtr, uint8_t* uuidPtr){

	uint8_t maxSize = 16;
    for (uint8_t count = 0; count < maxSize; count++) {

    	uuidPtr[maxSize - 1 - count] = charToInt(*charArrayPtr) << 4;
    	charArrayPtr ++;

    	uuidPtr[maxSize - 1 - count] += charToInt(*charArrayPtr);
    	charArrayPtr++;

    }

}

uint8_t Get_Wearable_Service_UUID(uint8_t* uuidPtr){
	Char_Array_To_128UUID(SERVICE_UUID, uuidPtr);
	return 16;
}

/**
 * @brief  Service initialization
 * @param  None
 * @retval None
 */
void Wearable_Sensor_Init(void)
{

	init_pvd();

	/**
	*	Register the event handler to the BLE controller
	*/
	SVCCTL_RegisterSvcHandler(Wearable_BLE_Event_Handler);

    /**
     *  Wearable Data Service
     *
     * Max_Attribute_Records = 2*no_of_char + 1
     * service_max_attribute_record = 1 for service
     *                                2 for data characteristic
     *                                3 because I don't know what this is.
     *
     */
  	Char_UUID_t  uuid128;
  	Char_Array_To_128UUID(SERVICE_UUID , (uint8_t*)&uuid128);
    aci_gatt_add_service(UUID_TYPE_128,
                      (Service_UUID_t *) &uuid128,
                      PRIMARY_SERVICE,
                      6,
                      &(inhaler_context.service_handler));

    /**
     *  Add LED Characteristic
     */
  	Char_Array_To_128UUID( IUE_CHARACTERISTIC , (uint8_t*)&uuid128);
    aci_gatt_add_char(inhaler_context.service_handler,
                      UUID_TYPE_128, &uuid128,
                      sizeof(IUE_t),
                      CHAR_PROP_READ,
					  ATTR_PERMISSION_AUTHEN_READ,
					  GATT_NOTIFY_READ_REQ_AND_WAIT_FOR_APPL_RESP, /* gattEvtMask */
                      10, /* encryKeySize */
                      1, /* isVariable */
                      &(inhaler_context.iue_characteristic_handler));

}
