package com.yuhengx.IocAssistance.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author white
 */
public class AliothConvertType {
    protected static List<Object> typeConversion(List<Object> typeNames, String[] values, Map<Object, Integer> thm) {
        List<Object> valueList = new ArrayList<>();
        for (int i = 0, len = typeNames.size(); i < len; i++) {
            if (thm.containsKey(typeNames.get(i))) {
                switch (thm.get(typeNames.get(i))) {
                    case 0:
                        Byte by = Byte.decode(values[i]);
                        valueList.add(by);
                        break;
                    case 1:
                        short sh = Short.parseShort(values[i]);
                        valueList.add(sh);
                        break;
                    case 2:
                        int in = Integer.parseInt(values[i]);
                        valueList.add(in);
                        break;
                    case 3:
                        long l = Long.parseLong(values[i]);
                        valueList.add(l);
                        break;
                    case 4:
                        float f = Float.parseFloat(values[i]);
                        valueList.add(f);
                        break;
                    case 5:
                        double d = Double.parseDouble(values[i]);
                        valueList.add(d);
                        break;
                    case 6:
                        boolean b = Boolean.parseBoolean(values[i]);
                        valueList.add(b);
                        break;
                    case 7:
                        char c = values[i].charAt(0);
                        valueList.add(c);
                        break;
                    case 8:
                        if (values.length > i) {
                            valueList.add(values[i]);
                        } else {
                            valueList.add(values[values.length - 1]);
                        }
                        break;
                    default:
                        valueList.add("");
                }
            } else {
                valueList.add(null);
            }
        }
        return valueList;
    }
}
