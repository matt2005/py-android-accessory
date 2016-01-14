
py-android-accessory
====================

Android Accessory in Python

Proof of conception of data exchange between an Android device and a computer through USB, based on the [Android Open Accessory][aoa] protocol and Python. This can be used to employ the sensors of the smart device from a standard computer application, in the context of a robotics project for example.

## Installation and setup

To install the Android application, have a look at the [latest release][releases] and download the APK from your smartphone.

For the client, clone this repository to your local environment. The main dependancies is ``PyUSB``, which is a project now [hosted on GitHub][pysub]. I am used to install it with ``sudo pip install pyusb`` on a Linux box. Some reports that it used more involved to install it on other OS.

## Quick start

First of all, plug your Android device to a USB port of the computer. Superuser priviliges are required to start the client, since the USB node is assigned to the root user. This will be improved in a next version.

From the repository root folder, start the client with the command:

````
$ sudo python client/pyaccessory.py
````

A pop-up should be displayed the first time, and the application will be automatically launched. You are ready to communicate from the application to the Python client, and the other way around.

Imagine a sensor and an actuator. The sensor variable is managed by the client. The value of the senor is read by the application but it is never modified by the application directly. From the [application][screenshot-app], you can control a virtual actuator that will send a command to change the value of the sensor.

There is sometime a random deviation, and the value of the sensor is modified without command (as a proof that the application reads the sensor value). The [client][screenshot-client] displays the value of the sensor, and the commands received from the application.

## Contributions

Contributions are welcome. The code is totally unstable, so there are many ways to improve it. Several flaws have been identified, have a look at the issues.

To get an hand-on the code and the Android Open Accessory protocol, you can refer to my [blog post][ab-android-accessory]. A list of usefull links is provided at the end.

## License

Copyright 2016 Arn-O under the [MIT license][license].

[aoa]: https://source.android.com/devices/accessories/protocol.html
[releases]: https://github.com/Arn-O/py-android-accessory/releases
[pysub]: https://walac.github.io/pyusb/
[screenshot-app]: https://raw.githubusercontent.com/Arn-O/py-android-accessory/master/assets/img/screenshot-app.png
[screenshot-client]: https://raw.githubusercontent.com/Arn-O/py-android-accessory/master/assets/img/screenshot-client.png
[ab-android-accessory]: http://arnaudbertrand.com/blog/2013/07/28/python-android-accessory-usb-communication-proof-of-concept/
[license]: https://github.com/Arn-O/py-android-accessory/blob/master/LICENSE
