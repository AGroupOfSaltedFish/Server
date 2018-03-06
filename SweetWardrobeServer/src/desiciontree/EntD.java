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
        return (attrName.equals("����¶�") || attrName.equals("����¶�") ||
                attrName.equals("���ʪ��") || attrName.equals("���ʪ��") ||
                attrName.equals("������") || attrName.equals("��С����"));
    }

    static Integer transferKey(Integer integer, String attrName) {
        Integer key;
        if(!successValue(attrName)) {
            key = integer;
        }
        else {
            Integer mod;
            switch(attrName) {
                case "����¶�": case "����¶�": mod = 5; break;
                case "���ʪ��": case "���ʪ��": mod = 20; break;
                case "������": case "��С����": mod = 3; break;
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
