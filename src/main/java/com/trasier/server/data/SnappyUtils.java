package com.trasier.server.data;

import org.iq80.snappy.Snappy;

import java.util.Base64;

public class SnappyUtils {

    public static String decodeUncompressData(String data) {
        if (data != null && data.getBytes().length > 0) {

            byte[] dataBytes;
            try {
                dataBytes = Base64.getDecoder().decode(data.getBytes());
            } catch (Exception e) {
                //ignore exception
                return data;
            }

            try {
                dataBytes = Snappy.uncompress(dataBytes, 0, dataBytes.length);
            } catch (Exception e) {
                //ignore exception
            }

            return new String(dataBytes);
        }
        return null;
    }

}