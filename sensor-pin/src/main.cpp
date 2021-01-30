/*
    Written by Youssef Beltagy.
    
    Based on the sample code that is written by Neil Kolban,
    ported to Arduino ESP32 by Evandro Copercini, and updated by chegewara.
    
    Check this link for documentation and directions:
    https://github.com/nkolban/ESP32_BLE_Arduino
    
    Here is a video explanation of it.
    Video: https://www.youtube.com/watch?v=oCMOYS71NIU

    I also recommend reading the header files.
    
   This program creates a BLE server that provides temperature and humidity characterists in a GATT server.
   The service advertises itself as: 25380284-e1b6-489a-bbcf-97d8f7470aa4
   The service has a temperature characteristic of: c3856cfa-4af6-4d0d-a9a0-5ed875d937cc
   The service has a humidity characteristic of:e36d8858-cac3-4b03-9356-98b40fdd122e
   The service has a digit characteristic of: 03192130-212a-48c9-b058-ee4dace59d26
   The service has a character characteristic of: 71eec950-3841-4984-83fa-0cfd8b9c901f
   The design of creating the BLE server is:
   1. Create a BLE Server
   2. Create a BLE Service
   3. Create a BLE Characteristic on the Service
   4. Create a BLE Descriptor on the characteristic
   5. Start the service.
   6. Start advertising.
   A connect hander associated with the server starts a background task that performs notification
   every couple of seconds.
*/

// Preprocessors
#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include "DHT.h"
#include <string>

#define SERVICE_UUID "25380284-e1b6-489a-bbcf-97d8f7470aa4"
#define TEMP_CHARACTERISTIC_UUID "c3856cfa-4af6-4d0d-a9a0-5ed875d937cc"
#define HUMIDITY_CHARACTERISTIC_UUID "e36d8858-cac3-4b03-9356-98b40fdd122e"
#define DIGIT_CHARACTERISTIC_UUID "03192130-212a-48c9-b058-ee4dace59d26"
#define ALPHABET_CHARACTERISTIC_UUID "71eec950-3841-4984-83fa-0cfd8b9c901f"

// Comment this line out before releasing.
//#define SMART_PIN_DEBUG


// Global variables

// Bluetooth server global variables.
BLEServer *serverPtr = nullptr;
BLECharacteristic *TempCharacteristicPtr = nullptr;
BLECharacteristic *HumidityCharacteristicPtr = nullptr;
BLECharacteristic *DigitPtr = nullptr;
BLECharacteristic *AlphabetPtr = nullptr;


bool deviceConnected = false;

// The temperature and humidity sensor I used (DHT11).
DHT dht(5, DHT11);


//Helper functions

// Converts a float to a string (represented as null terminated array of characters at arr) and returns the size of the string.
uint8_t doubleToCharArray(double num, uint8_t* arr){
  dtostrf(num, 3, 2, (char*) arr);

  for(uint8_t i = 0; true; i++){
    if(arr[i] == '\0') return i;
  }
}


//Classes
class ServerCallbacks : public BLEServerCallbacks
{
  void onConnect(BLEServer *serverPtr)
  {
    #ifdef SMART_PIN_DEBUG
    Serial.println("Established Connection");
    #endif
    deviceConnected = true;
  };

  void onDisconnect(BLEServer *serverPtr)
  {
    #ifdef SMART_PIN_DEBUG
    Serial.println("Connection Lost");
    #endif

    deviceConnected = false;

    delay(500);                    // give the bluetooth stack the chance to get things ready
    serverPtr->startAdvertising(); // restart advertising

    #ifdef SMART_PIN_DEBUG
    Serial.println("Advertising");
    #endif
  }
};

class CharacteristicCallBackHandler : public BLECharacteristicCallbacks
{
  // A pointer to a function that fills a pointer to a byte array with data and returns the size.
  uint8_t (*function)(uint8_t*);
  uint8_t* buf = new uint8_t[30];


  #ifdef SMART_PIN_DEBUG
  char* label;
  #endif


  void onRead(BLECharacteristic *ptr)
  {
    uint8_t size = (*function)(buf);
    ptr->setValue(buf, size);

    #ifdef SMART_PIN_DEBUG
    Serial.print(label);
    Serial.print("0x");
    for(int i = 0; i < size; i++)
      Serial.print(buf[i],HEX);
    Serial.println();
    #endif
  }

public:
  #ifdef SMART_PIN_DEBUG
  CharacteristicCallBackHandler(uint8_t (*function)(uint8_t*), char* label):
  function(function),
  label(label){};
  #endif

  CharacteristicCallBackHandler(uint8_t (*function)(uint8_t*)):
  function(function){};
};

void setup()
{
  #ifdef  SMART_PIN_DEBUG
  Serial.begin(115200);
  #endif

  // Initialize the temperature sensor.
  dht.begin();

  // Create the BLE Device
  BLEDevice::init("Chicken-BLE");

  // Create the BLE Server
  serverPtr = BLEDevice::createServer();
  serverPtr->setCallbacks(new ServerCallbacks());

  // Create the BLE Service
  BLEService *pService = serverPtr->createService(SERVICE_UUID);

  // Create a BLE Characteristic for temperature
  TempCharacteristicPtr = pService->createCharacteristic(
      TEMP_CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_READ);

  TempCharacteristicPtr->setCallbacks(new CharacteristicCallBackHandler(
    [](uint8_t * ptr)->uint8_t{
      return doubleToCharArray(dht.readTemperature(), ptr);
    }));

  // Create a BLE Characteristic for humidity
  HumidityCharacteristicPtr = pService->createCharacteristic(
      HUMIDITY_CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_READ);

  
  HumidityCharacteristicPtr->setCallbacks(new CharacteristicCallBackHandler(
    [](uint8_t * ptr)->uint8_t{
      return doubleToCharArray(dht.readHumidity(), ptr);
    }));

  // Create a BLE Characteristic for digits
  DigitPtr = pService->createCharacteristic(
      DIGIT_CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_READ);

  DigitPtr->setCallbacks(new CharacteristicCallBackHandler(
    [](uint8_t * ptr)->uint8_t{
      if(*ptr < 48 || *ptr > 57) *ptr = 48;
      else
        *ptr = (*ptr - 48 + 1)%10 + 48;
      return 1;
    }));

  // Create a BLE Characteristic for Alphabet
  AlphabetPtr = pService->createCharacteristic(
      ALPHABET_CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_READ);

  AlphabetPtr->setCallbacks(new CharacteristicCallBackHandler(
    [](uint8_t * ptr)->uint8_t{
      if(*ptr < 65 || *ptr > 90) *ptr = 65;
      else
        *ptr = (*ptr - 65 + 1)%26 + 65;
      return 1;
    }));

  // Start the service
  pService->start();

  // Start advertising
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(false);
  pAdvertising->setMinPreferred(0x0); // set value to 0x00 to not advertise this parameter
  BLEDevice::startAdvertising();

  #ifdef SMART_PIN_DEBUG
  Serial.println("Awaiting a client connection...");
  #endif
}

void loop()
{
  //spin wait.
}