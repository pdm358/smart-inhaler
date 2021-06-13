#include "dht_11.h"

#define ERROR_CODE 0xFFFFFFFFU
#define TRUE 1
#define FALSE 0

#define DHT_PORT 		GPIOA
#define DHT_PIN 		GPIO_PIN_1
#define DHT_DATA_SIZE 5

/**
 * Sets the DHT pin as an output pin.
 */
static void Set_Pin_Output()
{
	GPIO_InitTypeDef GPIO_InitStruct;
	GPIO_InitStruct.Pin = DHT_PIN;
	GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
	GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
	HAL_GPIO_Init(DHT_PORT, &GPIO_InitStruct);
}

/**
 * Sets the DHT pin as input pin with a high pullup.
 */
static void Set_Pin_Input ()
{
	GPIO_InitTypeDef GPIO_InitStruct;
	GPIO_InitStruct.Pin = DHT_PIN;
	GPIO_InitStruct.Mode = GPIO_MODE_INPUT;
	GPIO_InitStruct.Pull = GPIO_PULLUP;
	GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
	HAL_GPIO_Init(DHT_PORT, &GPIO_InitStruct);
}

/**
 * Waits for the specified pin state and returns the time (us) it took for that state to be reached.
 *
 * Will return ERROR_CODE when the timeout expires.
 *
 * Assumes DWT->CYCCNT is enabled and ready.
 *
 */
static uint32_t time_to_signal(GPIO_PinState waitFor, uint32_t timoutInTicks){

	uint32_t startTime = DWT->CYCCNT;

	while(DWT->CYCCNT - startTime < timoutInTicks){
		if(HAL_GPIO_ReadPin (DHT_PORT, DHT_PIN) == waitFor){
			return (DWT->CYCCNT - startTime)/(HAL_RCC_GetHCLKFreq()/1000000);
		}
	}

	return ERROR_CODE;

}

/**
 * This function intializes/deinitializes the clk cycle counter: DWT->CYCCNT
 *
 * It is already initialized, so this fucntion doesn't do anything.
 *
 * I left it because maybe after changing some build/debug/project parameters, DWT-> CYCNT
 * will no longer be initialized.
 *
 * I commented out the calls to it rather than deleting them.
 *
 * If the smart-pin freezes every time the dht sensor is queried, especially in a release built, then consider
 * using this function.
 *
 * The smart-pin would freeze because the counter doesn't increment.
 */
static uint8_t init_timer(uint8_t set_counter)
{
	static uint32_t dwt_ctrl_copy = 0;

	if(set_counter){ // Initialize

		/* Enable  clock cycle counter */
		dwt_ctrl_copy = DWT->CTRL;

		DWT->CTRL |=  DWT_CTRL_CYCCNTENA_Msk; //Enable the counter

		uint32_t cur_count = DWT->CYCCNT;

		/* 3 NO OPERATION instructions */
		__ASM volatile ("NOP");
		__ASM volatile ("NOP");
		__ASM volatile ("NOP");

		/* Check if clock cycle counter has started */
		if(DWT->CYCCNT - cur_count > 0) return TRUE; /*clock cycle counter started*/

		return FALSE; /*clock cycle counter not started*/

	}


	// Deinitialize

	// Restore the status of the register.
	// Assumes this function was called first with set_counter == TRUE
	//CoreDebug->DEMCR = demcr_copy;
	DWT->CTRL = dwt_ctrl_copy;

	return TRUE;

}


static uint8_t query_dht11_sensor (uint32_t timeout_per_operation)
{
	Set_Pin_Output (DHT_PORT, DHT_PIN);  // set the pin as output
	HAL_GPIO_WritePin (DHT_PORT, DHT_PIN, GPIO_PIN_RESET);   // pull the pin low

	HAL_Delay(20); // The DHT datasheet says at least 18 ms.

	HAL_GPIO_WritePin (DHT_PORT, DHT_PIN, GPIO_PIN_SET);   // pull the pin high
	Set_Pin_Input(DHT_PORT, DHT_PIN);    // set as input pullup

	if(time_to_signal(GPIO_PIN_RESET, timeout_per_operation) == ERROR_CODE) return FALSE;
	if(time_to_signal(GPIO_PIN_SET, timeout_per_operation) == ERROR_CODE) return FALSE;
	if(time_to_signal(GPIO_PIN_RESET, timeout_per_operation) == ERROR_CODE) return FALSE;

	return TRUE;
}

/**
 * Reads 8 bits from the DHT11 sensor. Must be called immediately after the DHT11 sensor is queried.
 * Must be called consequetively until all 40 bits are read.
 */
static uint8_t read_dht11_data_byte (uint8_t* result_ptr, uint32_t timeout_per_operation)
{
	*result_ptr = 0;

	for(uint8_t i = 0; i < 8; i++){

		(*result_ptr) <<= 1;

		uint32_t low_time = time_to_signal(GPIO_PIN_SET, timeout_per_operation);

		if(low_time == ERROR_CODE) return FALSE;

		uint32_t high_time = time_to_signal(GPIO_PIN_RESET, timeout_per_operation);

		if(high_time == ERROR_CODE) return FALSE;

		*result_ptr |= (high_time >= low_time) ? 0x01 : 0x00;

	}

	return TRUE;
}

static uint8_t parse_data(uint8_t* input_ptr, DHT_11_Data *dht_data_ptr){
	uint8_t sum = 0;

	for(uint8_t i = 0; i < DHT_DATA_SIZE -1; i++){
		sum += input_ptr[i];
	}

	if(sum != input_ptr[DHT_DATA_SIZE - 1]) return FALSE;

	// Temperature
	dht_data_ptr->Temperature = input_ptr[2];

	if (input_ptr[3] & 0x80) {
		dht_data_ptr->Temperature = -1 - dht_data_ptr->Temperature;
	}

	dht_data_ptr->Temperature += (input_ptr[3] & 0x0f) * 0.1;

	// Humidity
	dht_data_ptr->Humidity = input_ptr[0] + input_ptr[1] * 0.1;

	return TRUE;
}

/**
 * To get the DHT11 data, you only need to call this function,
 *
 * Returns 1 on success and 0 on failure.
 */
uint8_t get_dht11_data (DHT_11_Data *DHT_Data)
{


	if(!init_timer(TRUE)) return FALSE; // Please checkout the init_timer function to understand why it is commented out.

	//500 us * (freq/1M) = 500 us * num clk cycles in us = number of clk cycles in 500us.

	uint32_t cyc_timout_per_operation = 500 * (HAL_RCC_GetHCLKFreq() / 1000000);
	uint8_t dht_buffer[DHT_DATA_SIZE];

	if(!query_dht11_sensor(cyc_timout_per_operation)) return FALSE;

	for(uint8_t i = 0; i < DHT_DATA_SIZE; i++){
		if(!read_dht11_data_byte((dht_buffer + i), cyc_timout_per_operation)) return FALSE;
	}

	if(!init_timer(FALSE)) return FALSE; // Please checkout the init_timer function to understand why it is commented out.

	if(!parse_data(dht_buffer, DHT_Data)) return FALSE;

	return TRUE;
}


