package com.ybeltagy.breathe;

import com.ybeltagy.breathe.ble.BLEFinals;

/**
 * App level final variables.
 */
public class Finals {

    /**
     * The channel id of the notification channel for the BLEService. Must be unique in the whole app.
     */
    public static final String BLE_NOTIFICATION_CHANNEL_ID = BLEFinals.BLE_NOTIFICATION_CHANNEL_ID;

    /**
     * The notification id of the notification for the BLEService.
     */
    public static final int BLE_SERVICE_NOTIFICATION_ID = BLEFinals.BLE_SERVICE_NOTIFICATION_ID;

    /**
     * The package name as a string. Use everywhere you need the the package name instead of hardcoding the package name.
     */
    public static final String PACKAGE_NAME =   "com.ybeltagy.breathe";

    /** todo: consider including in the collection package too.
     * The private file where CSV content is generated for temporary sharing.
     *
     * This must be in a folder listed inside res/xml/provider_paths.xml so the FileProvider can share it.
     */
    public static final String IUE_DATA_FILE_NAME = "iue_data.csv";

    public static final String FILE_PROVIDER_AUTHORITY_STRING = PACKAGE_NAME + ".fileprovider";

}
