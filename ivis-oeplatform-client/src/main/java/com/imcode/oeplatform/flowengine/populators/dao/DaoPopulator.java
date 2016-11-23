package com.imcode.oeplatform.flowengine.populators.dao;

import se.unlogic.standardutils.populators.BaseStringPopulator;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.*;
import java.util.Base64;
import java.util.function.Function;

/**
 * Created by vitaly on 25.09.15.
 */
public interface DaoPopulator<T extends Serializable> extends BeanStringPopulator<T>, QueryParameterPopulator<T> {
//    Function<?, String> DEFAULT_TO_STRING_MAPPER = o -> {
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
//            oos.writeObject(o);
//            return Base64.getEncoder().encodeToString(baos.toByteArray());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    };


//    Function<String, ?> DEFAULT_FROM_STRING_MAPPER = s -> {
//        byte[] data = Base64.getDecoder().decode(s);
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
//            Object o = ois.readObject();
//            return o;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    };

    @SuppressWarnings("unchecked")
    static <T extends Serializable> T fromBase64String(String s) {
        byte[] data = Base64.getDecoder().decode(s);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));) {
            Object o = ois.readObject();
            return (T) o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Write the object to a Base64 string.
     */
    static <T extends Serializable>String toBase64String(Object o) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);) {

            oos.writeObject(o);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends Serializable> Function<String, Object> getBase64StringMapper() {
        return s -> {
            byte[] data = Base64.getDecoder().decode(s);
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                Object o = ois.readObject();
                return o;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <T extends Serializable> Function<T, String> getBase64ObjectMapper() {
        return o -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(o);
                return Base64.getEncoder().encodeToString(baos.toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }


}
