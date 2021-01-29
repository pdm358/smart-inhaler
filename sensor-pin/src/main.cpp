/*
    Video: https://www.youtube.com/watch?v=oCMOYS71NIU
    Based on Neil Kolban example for IDF: https://github.com/nkolban/esp32-snippets/blob/master/cpp_utils/tests/BLE%20Tests/SampleNotify.cpp
    Ported to Arduino ESP32 by Evandro Copercini
    updated by chegewara
   Create a BLE server that, once we receive a connection, will send periodic notifications.
   The service advertises itself as: 4fafc201-1fb5-459e-8fcc-c5c9c331914b
   And has a characteristic of: beb5483e-36e1-4688-b7f5-ea07361b26a8
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

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include "DHT.h"

#define SERVICE_UUID "25380284-e1b6-489a-bbcf-97d8f7470aa4"
#define TEMP_CHARACTERISTIC_UUID "c3856cfa-4af6-4d0d-a9a0-5ed875d937cc"
#define HUMIDITY_CHARACTERISTIC_UUID "e36d8858-cac3-4b03-9356-98b40fdd122e"

BLEServer *serverPtr = nullptr;
BLECharacteristic *TempCharacteristicPtr = nullptr;
BLECharacteristic *HumidityCharacteristicPtr = nullptr;
bool deviceConnected = false;
uint64_t last = 0;

//pin, type
DHT dht(5, DHT11);

class ServerCallbacks : public BLEServerCallbacks
{
  void onConnect(BLEServer *serverPtr)
  {
    Serial.println("Established Connection");
    deviceConnected = true;
  };

  void onDisconnect(BLEServer *serverPtr)
  {
    Serial.println("Connection Lost");
    deviceConnected = false;

    delay(500);                    // give the bluetooth stack the chance to get things ready
    serverPtr->startAdvertising(); // restart advertising
    Serial.println("Advertising");
  }
};

class HumidityCallBackHandler : public BLECharacteristicCallbacks
{
  void onRead(BLECharacteristic *ptr)
  {
    uint8_t humidity = dht.readHumidity();
    Serial.print("Sending Humidity:");
    Serial.println(humidity);
    ptr->setValue(&humidity, 1);
  }
};

void setup()
{
  Serial.begin(115200);

  dht.begin();

  // Create the BLE Device
  BLEDevice::init("Chicken-BLE");

  // Create the BLE Server
  serverPtr = BLEDevice::createServer();
  serverPtr->setCallbacks(new ServerCallbacks());

  // Create the BLE Service
  BLEService *pService = serverPtr->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  TempCharacteristicPtr = pService->createCharacteristic(
      TEMP_CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_NOTIFY);

  // Descriptor for notifications.
  TempCharacteristicPtr->addDescriptor(new BLE2902());

  HumidityCharacteristicPtr = pService->createCharacteristic(
      HUMIDITY_CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_READ);

  HumidityCharacteristicPtr->setCallbacks(new HumidityCallBackHandler());

  // Start the service
  pService->start();

  last = millis();
  // Start advertising
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(false);
  pAdvertising->setMinPreferred(0x0); // set value to 0x00 to not advertise this parameter
  BLEDevice::startAdvertising();
  Serial.println("Awaiting a client connection...");
}

void loop()
{
  // indicate changed value
  if (deviceConnected && millis() - last >= 5000)
  {
    uint8_t v = dht.readTemperature(true);
    Serial.print("Sending Temp: ");
    Serial.println(v);
    TempCharacteristicPtr->setValue((uint8_t *)&v, 1);
    TempCharacteristicPtr->notify();
    last = millis();
  }

}