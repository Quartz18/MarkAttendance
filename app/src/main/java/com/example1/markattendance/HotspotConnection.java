package com.example1.markattendance;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

public class HotspotConnection {
    String ipAddress,macAddress;
    public HotspotConnection() {
        String getmac = getListOfConnectedDevice();
    }

    public String getListOfConnectedDevice() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                BufferedReader br = null;
                boolean isFirstLine = true;

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;

                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }

                        String[] splitted = line.split(" +");

                        if (splitted != null && splitted.length >= 4) {

                            ipAddress = splitted[0];
                            macAddress = splitted[3];

                            boolean isReachable = InetAddress.getByName(
                                    splitted[0]).isReachable(500);  // this is network call so we cant do that on UI thread, so i take background thread.
                            if (isReachable) {
                                Log.d("Device Information", ipAddress + " : " + macAddress);
                                return ;
                            }

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {

                        e.printStackTrace();

                    }
                }
            }
        });
        thread.start();
        return ipAddress + " "+macAddress;
    }
}
