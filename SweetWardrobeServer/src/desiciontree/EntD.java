package desiciontree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EntD {
    private String attrName;
    private List<Integer> result;

    private EntD() {

    }

    EntD(List<Integer> result, String attrName) {
        this();
        this.attrName = attrName;
        this.result = result;
    }

    static Boolean successValue(String attrName) {
        return (attrName.equals("最高温度") || attrName.equals("最低温度") ||
                attrName.equals("最高湿度") || attrName.equals("最低湿度") ||
                attrName.equals("最大风力") || attrName.equals("最小风力"));
    }

    static Integer transferKey(Integer integer, String attrName) {
        Integer key;
        if(!successValue(attrName)) {
            key = integer;
        }
        else {
            Integer mod;
            switch(attrName) {
                case "最高温度": case "最低温度": mod = 5; break;
                case "最高湿度": case "最低湿度": mod = 20; break;
                case "最大风力": case "最小风力": mod = 3; break;
                default: mod = 0;
            }
            key = integer - integer % mod;
        }
        return key;
    }

    Double calculateEntD() {
        Map<Integer, Integer> counter = new HashMap<>();
        for(Integer integer: result) {
            Integer key = transferKey(integer,attrName);
            if(!counter.containsKey(key)) {
                counter.put(key, 0);
            }
            Integer times = counter.get(key);
            counter.put(key, times + 1);
        }

        Double entD = 0.0;
        for(Map.Entry<Integer, Integer> entry: counter.entrySet()) {
            Double pk = entry.getValue().doubleValue()/result.size();
            entD -= pk*(Math.log(pk)/Math.log(2));
        }
        return entD;
    }
}
