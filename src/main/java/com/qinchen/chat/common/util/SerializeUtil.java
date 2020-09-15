package com.qinchen.chat.common.util;


import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UserBean: bxl
 */
public class SerializeUtil {
	protected static Logger logger = LoggerFactory.getLogger(SerializeUtil.class);
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
//序列化
            if (object != null && object instanceof Serializable) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                byte[] bytes = baos.toByteArray();
                return bytes;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            close(baos);
            close(oos);
        }
        return null;
    }

    public static Object unSerialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        try {
//反序列化
            if (bytes != null) {
                bais = new ByteArrayInputStream(bytes);
                ois = new ObjectInputStream(bais);
                return ois.readObject();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            close(bais);
            close(ois);
        }
        return null;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
