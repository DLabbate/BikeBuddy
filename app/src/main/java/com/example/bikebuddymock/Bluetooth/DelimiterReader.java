package com.example.bikebuddymock.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.reader.SocketReader;

/*
By default, the library used considers newline as the delimiter
However, the HxM data packet ends the message with ETX. Therefore we need to implement our own DelimiterReader
The ETX field (End of Text) is a standard ASCII control character (0x03)
For more documentation, see https://github.com/OmarAflak/Bluetooth-Library/blob/master/app/src/main/java/me/aflak/libraries/ChatActivity.java
 */
public class DelimiterReader extends SocketReader {
    private PushbackInputStream reader;
    private byte delimiter;

    public DelimiterReader(InputStream inputStream) {
        super(inputStream);
        reader = new PushbackInputStream(inputStream);
        delimiter = 0x03;
    }

    @Override
    public byte[] read() throws IOException {
        List<Byte> byteList = new ArrayList<>();
        byte[] tmp = new byte[1];

        while(true) {
            int n = reader.read();
            reader.unread(n);

            int count = reader.read(tmp);
            if(count > 0) {
                if(tmp[0] == delimiter){
                    //byte[] returnBytes = new byte[byteList.size()];
                    byte[] returnBytes = new byte[60]; //Allocate 60 bytes for incoming message
                    for(int i=0 ; i<byteList.size() ; i++){
                        returnBytes[i] = byteList.get(i);
                    }
                    return returnBytes;
                } else {
                    byteList.add(tmp[0]);
                }
            }
        }
    }
}
