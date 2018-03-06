package desiciontree;

import desiciontree.TreeNode.LeafNode;
import desiciontree.TreeNode.Node;
import desiciontree.TreeNode.RootNode;
import org.omg.CORBA.INTERNAL;

import java.util.*;

class AttributeTree {
    private Map<String, List<Integer>> table;
    private List<Integer> result;

    private AttributeTree() {
        table = new HashMap<>();
        result = new ArrayList<>();
    }

    AttributeTree(Map<String, List<Integer>> table, String attrToLearn) {
        result = table.get(attrToLearn);
        table.remove(attrToLearn);
        this.table = table;
    }

    Node buildTreeRecursion() {
        Boolean resultSame = true;
        for(int i = 0; i < result.size() - 1; ++i) {
            if(!result.get(i).equals(result.get(i + 1))) {
                resultSame = false;
                break;
            }
        }
        if(resultSame) {
            return new LeafNode(result.get(0));
        }

        if(table.keySet().isEmpty()) {
            return new LeafNode(findMaxResult());
        }
        Boolean attrSame = true;
        for(String attrName: table.keySet()) {
            List<Integer> line = table.get(attrName);
            for(int i = 0; i < line.size() - 1; ++i) {
                if(!line.get(i).equals(line.get(i + 1))) {
                    attrSame = false;
                    break;
                }
            }
        }
        if(attrSame) {
            return new LeafNode(findMaxResult());
        }

        String attrName = selectDivideAttr();
        assert attrName != null;
        System.out.println("最佳划分属性为: " + attrName);
        System.out.print("剩余分类属性为: ");
        for(String name: table.keySet()) {
            System.out.print(name + "\t");
        }
        System.out.println();

        RootNode node = new RootNode(attrName);
        Map<Integer, AttributeTree> function = divideSample(attrName);
        for(Integer value: function.keySet()) {
            node.addSubTree(value, function.get(value).buildTreeRecursion());
        }
        return node;
    }

    private Integer findMaxResult() {
        Map<Integer, Integer> counter = new HashMap<>();
        for(Integer integer: result) {
            if(counter.containsKey(integer)) {
                Integer value = counter.get(integer) + 1;
                counter.put(integer, value);
            }
            else {
                counter.put(integer, 1);
            }
        }

        Integer flag = -1, max = -1;
        for(int i = 0; i < result.size(); ++i) {
            if(counter.get(result.get(i)) > max) {
                max = counter.get(result.get(i));
                flag = i;
            }
        }
        return result.get(flag);
    }

    String selectDivideAttr() {
        Map<String, Double> record = new HashMap<>();
        for(String attrToDivide: table.keySet()) {
            GainRate gain = new GainRate(table, result, attrToDivide);
            record.put(attrToDivide, gain.calculateGain());
        }

        Double maxValue = Collections.max(record.values());
        String selectedAttr = null;
        for(Map.Entry<String, Double> entry: record.entrySet()) {
            if(entry.getValue().equals(maxValue)) {
                selectedAttr = entry.getKey();
            }
        }
        return selectedAttr;
    }

    private void addToResult(Integer sample) {
        result.add(sample);
    }

    private void addToTable(Map<String, List<Integer>> superTable,
                            String attrToIgnore, Integer index) {
        for(String attr: table.keySet()) {
            if(!attr.equals(attrToIgnore)) {
                table.get(attr).add(superTable.get(attr).get(index));
            }
        }
    }

    private Map<Integer, AttributeTree> divideSample(String attrName) {
        List<Integer> line = table.get(attrName);
        Map<Integer, AttributeTree> function = new HashMap<>();
        for(int i = 0; i < line.size(); ++i) {
            Integer key;
            if(EntD.successValue(attrName)) {
                key = EntD.transferKey(line.get(i), attrName);
            }
            else {
                key = line.get(i);
            }
            if(!function.containsKey(key)) {
                function.put(key, new AttributeTree());
            }
            function.get(key).addToResult(result.get(i));
            function.get(key).addToTable(this.table, attrName, i);
        }
        return function;
    }
}
