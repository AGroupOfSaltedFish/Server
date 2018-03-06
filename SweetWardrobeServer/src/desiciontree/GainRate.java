package desiciontree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Clothes;

class GainRate {
    private Map<Integer, List<Integer>> function;
    private String attrToDivide;

    private GainRate() {

    }

    GainRate(Map<String, List<Integer>> table, List<Integer> result,
             String attrToDivide) {
        this();
        this.attrToDivide = attrToDivide;
        List<Integer> line = table.get(attrToDivide);
        function = new HashMap<>();
        for(int i = 0; i < line.size(); ++i) {
            if(attrToDivide.equals("天气") || attrToDivide.equals("外套") || attrToDivide.equals("上衣") ||
                    attrToDivide.equals("裤装") || attrToDivide.equals("鞋子")) {
                if(!function.containsKey(line.get(i))) {
                    function.put(line.get(i), new ArrayList<>());
                }
                function.get(line.get(i)).add(result.get(i));
            }
            else {
                Integer mod;
                switch(attrToDivide) {
                    case "最高温度": case "最低温度": mod = 5; break;
                    case "最高湿度": case "最低湿度": mod = 20; break;
                    case "最大风力": case "最小风力": mod = 3; break;
                    default: mod = 0;
                }
                Integer category = line.get(i) - line.get(i) % mod;
                if(!function.containsKey(category)) {
                    function.put(category, new ArrayList<>());
                }
                function.get(category).add(category);
            }
        }
    }

    private Boolean isClothes() {
        return (attrToDivide.equals("外套") || attrToDivide.equals("上衣") ||
                attrToDivide.equals("裤装") || attrToDivide.equals("鞋子"));
    }

    private Boolean isVoidClothes(Integer key) {
        return isClothes() && key.equals(Clothes.defaultClothes);
    }

    Double calculateGain() {
        List<Integer> validSample = new ArrayList<>();
        for(Map.Entry<Integer, List<Integer>> entry: function.entrySet()) {
            if(!isVoidClothes(entry.getKey())) {
                validSample.addAll(entry.getValue());
            }
        }
        Double gain = new EntD(validSample, attrToDivide).calculateEntD();
        for(Map.Entry<Integer, List<Integer>> entry: function.entrySet()) {
            if(!isVoidClothes(entry.getKey())) {
                gain -= new EntD(entry.getValue(), attrToDivide).calculateEntD();
            }
        }
        return gain;
    }
}
