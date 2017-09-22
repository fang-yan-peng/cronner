package cronner.jfaster.org.util;

import cronner.jfaster.org.exeception.JobConfigurationException;
import org.assertj.core.util.Strings;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * @author fangyanpeng
 */
public class BeanConfigCopyUtil {

    public static <M,N> M copy(Class<M> targetClz,N sourceObj,Class<N> sourceClz){
        try {
            M targetObj = targetClz.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(targetClz);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors){
                String name = descriptor.getName();
                if("class".equals(name)){
                    continue;
                }
                Method writeMethod = descriptor.getWriteMethod();
                PropertyDescriptor sourceDesc = new PropertyDescriptor(name, sourceClz);
                Method readMethod = sourceDesc.getReadMethod();
                Object val = readMethod.invoke(sourceObj);

                Class returnType = readMethod.getReturnType();
                if(val == null || Strings.isNullOrEmpty(String.valueOf(val))){
                    continue;
                }
                String valStr = String.valueOf(val);
                if (returnType == String.class) {
                    writeMethod.invoke(targetObj, valStr);
                } else if (returnType == Integer.class || returnType == int.class) {
                    writeMethod.invoke(targetObj, Integer.parseInt(valStr));
                } else if (returnType == Long.class || returnType == long.class) {
                    writeMethod.invoke(targetObj, Long.parseLong(valStr));
                } else if (returnType == Boolean.class || returnType == boolean.class) {
                    writeMethod.invoke(targetObj, Boolean.parseBoolean(valStr));
                }
            }
            return targetObj;
        } catch (Exception e) {
            throw new  JobConfigurationException(e);
        }
    }
}
