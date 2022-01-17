<h1 align="center">Ringer</h1>

<p align="center">
Ringer is an Android SDK based on Contacts
</p>

## Download SDK
Go to the [Releases](https://github.com/developer-espark/Ringer-Interactive-Android) to download latest SDK

### Here is the SDK instruction how to use

##Step 1
If you already using the firebase than use below code

Add below function in the FirebaseMessagingService

```onMessageReceived

        try {
                LibrarySDKMessagingService().sendNotification(this,remoteMessage)

        } catch (e: Exception) {
            e.printStackTrace()

        }
```

##Step 2
If you are not using firebase than use below code

```gradle
implementation ('com.ringer.interactive.version_name'){
           transitive = true
        // Use the consuming application's FireBase module, so exclude it
        // from the dependency. (not totally necessary if you use compileOnly
        // when declaring the dependency in the library project).
        exclude group: 'com.google.firebase'
        // Exclude the "plain java" json module to fix build warnings.
        exclude group: 'org.json', module: 'json'
 }
    implementation("com.google.firebase:firebase-messaging:22.0.0") {
        // Exclude the "plain java" json module to fix build warnings.
        exclude group: 'org.json', module: 'json'
    }
```

##Step 2.1
In the Manifest File add below code
To Continue to get notified

```Manifest

    <uses-permission android:name="android.permission.READ_CONTACTS"/>
    <uses-permission android:name="android.permission.WRITE_CONTACTS"/>

        <service
            android:name="com.ringer.interactive.firebase.LibrarySDKMessagingService"
            android:exported="true">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
```

##Now Setup is done now you have to Add Credential in order to use this SDK

##Step 2.2
### Add username and password in a ###string.xml### file in your project

```string.xml
<string name="ringer_user_name">Your Registered Email Address</string>
<string name="ringer_password">Your Password</string>
```

After adding this credentials you can have access of the SDK , now follow Step 2.3 to continue

## Step 2.3

In your MainActivity call below function to continue
This is required to use the SDK

```YourActivity

   InitializeToken(this,resources.getString(R.string.ringer_user_name),resources.getString(R.string.ringer_password),"YOUR APP NAME")

```

## Step 2.4

Final Step to finish SDK Setup
This step is for the permission you need to granted.

```YourActivity

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        RingerInteractive().onRequestPermissionsResult(requestCode, permissions, grantResults,this)

    }

```


