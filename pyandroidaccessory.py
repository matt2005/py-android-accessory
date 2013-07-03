#!/usr/bin/python

import usb.core
import sys
import time

VID_GALAXY_NEXUS_DEBUG = 0x04e8
PID_GALAXY_NEXUS_DEBUG = 0x6860

VID_ANDROID_ACCESSORY = 0x18d1
PID_ANDROID_ACCESSORY = 0x2d01

# To develop :
#   - remove Accessory mode ?
#   - start application when already in accessory mode

NB_ITERATION = 100

def get_accessory():
    dev = usb.core.find(idVendor=VID_ANDROID_ACCESSORY, 
                        idProduct=PID_ANDROID_ACCESSORY)
    if dev:
        print('Accessory mode started')
    else:
        print('No Android accessory found')

    time.sleep(0.5)
    return dev

def get_android_device():

    print('Looking for VID 0x%0.4x and PID 0x%0.4x'
        % (VID_GALAXY_NEXUS_DEBUG, PID_GALAXY_NEXUS_DEBUG))

    android_dev = usb.core.find(idVendor=VID_GALAXY_NEXUS_DEBUG, 
        idProduct=PID_GALAXY_NEXUS_DEBUG)

    if android_dev:
        print('Samsung Galaxy nexus (debug) found')
    else:
        sys.exit('No Android device found')

    return android_dev


def get_protocol(ldev):

    try:
        ldev.set_configuration()
    except usb.core.USBError as e:
        if  e.errno == 16:
            print('Device already configured')
    ret = ldev.ctrl_transfer(0xC0, 51, 0, 0, 2)
# Dunno how to translate: array('B', [2, 0])
    protocol = ret[0]
    print('Protocol version: %i' % protocol)
    if protocol < 2:
        sys.exit('Android Open Accessory protocol v2 not supported')

    return protocol

    
def send_string(dev, str_id, str_val):

    print('Sending identifying string %i: ' % str_id),
    ret = dev.ctrl_transfer(0x40, 52, 0, str_id, str_val, 0)
    if ret == len(str_val):
        print('OK')
    
    return 
    
def wait_for_command(ldev):

    print('Device: 0x%0.4x' % ldev.idVendor)

    for i in range(NB_ITERATION):
        time.sleep(0.2)
        print('Iteration %i / %i' % (i, NB_ITERATION))
# OUT
        print('\t' + 'OUT: '),
        msg='test'
        try:
            ret = ldev.write(0x02, msg, 0, 200)
            print(ret)
        except usb.core.USBError as e:
            print e
# IN
#        print('\t' + 'IN: '),
#        try:
#            ret = ldev.read(0x81, len(msg), 0, 200)
#            print ret
#        except usb.core.USBError as e:
#            print('error '),
#            print e       
       
    return

# Define a main() function 
def main():

    dev = get_accessory()

    if not dev:
        print('Try to start accessory mode')
        dev = get_android_device()
        protocol = get_protocol(dev)
        send_string(dev, 0, '_ArnO_')
        send_string(dev, 1, 'PyAndroidAccessory')
        send_string(dev, 2, 'A Python based Android accessory')
        send_string(dev, 3, '0.1.0')
        send_string(dev, 4, 
            'http://zombiebrainzjuice.cc/py-android-accessory/')
        send_string(dev, 5, '2254711SerialNo.')

        ret = dev.ctrl_transfer(0x40, 53, 0, 0, '', 0)
        print ret
        if ret:
            print('Start-up failed')

        time.sleep(1)
        dev = get_accessory()

    time.sleep(1)
    dev = get_accessory()

    wait_for_command(dev)

# 'This is the standard boilerplate that calls the main() function.
if __name__ == '__main__':
    main()
