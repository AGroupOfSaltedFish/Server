package desiciontree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootNode extends Node {
    private String attrName;
    private Map<Integer, Node> subTree;
    /*
     *  attrNameΪ�������������Ӧ����ѷ�����������
     *  subTree�洢�ý�����и����Ե�ֵ�����Ӧ�ķ�֧
     */

    RootNode() {
        end = false;
        subTree = new HashMap<>();
    }

    public RootNode(String attrName) {
        this();
        this.attrName = attrName;
    }
    public void addSubTree(Integer key, Node value) {
        if(!subTree.containsKey(key)) {
            subTree.put(key, value);
        }
        else {
            System.out.println("�ü�ֵ�ѱ�����ͼ��");
            assert false;
        }
    }
}
