package desiciontree;

import desiciontree.TreeNode.*;

import java.io.*;
import java.util.*;

import database.Main;
import database.Suit;
import database.UserInfo;
import database.Weather;


public class DecisionTree {
    private Integer attrSelMode;    //��ѷ�������ѡ��ģʽ��0��ʾ����Ϣ����Ⱥ�����1��ʾ����Ϣ�����ʺ���
    private Map<String, List<Integer>> table;
    private Node root;
    private static final List<String> attrList = new ArrayList<>();

    private DecisionTree() {
        attrSelMode = 0;
        attrList.add("����");
        attrList.add("����¶�");
        attrList.add("����¶�");
        attrList.add("���ʪ��");
        attrList.add("���ʪ��");
        attrList.add("������");
        attrList.add("��С����");
        attrList.add("����");
        attrList.add("����");
        attrList.add("��װ");
        attrList.add("Ь��");
    }

    public DecisionTree(Integer userId) {
        this();
        Main user=new Main();
        UserInfo userData=user.getUserInfoById(userId);

        List<List<Integer>> weatherData = new ArrayList<>();
        List<List<Integer>> clothesData = new ArrayList<>();
        for(Weather weather: userData.getClothesInfo().getWeatherHisory()) {
            weatherData.add(weather.formatWeather());
        }

        for(Suit suit: userData.getClothesInfo().getSuitHistory()) {
            clothesData.add(suit.getClothesIdList());
        }

        List<List<Integer>> data = null;
        try {
            List<List<Integer>> data1 = deepCopy(weatherData);
            List<List<Integer>> data2 = deepCopy(clothesData);
            data = data1;
            assert data1.size() == data2.size();
            for(int i = 0; i < data.size(); ++i) {
                data.get(i).addAll(data2.get(i));
            }

        } catch(Exception e) {
            e.printStackTrace();
            assert false;
        }

        table = listToMap(data);
    }

    public DecisionTree(Integer userId, Integer attrSelMode) {
        this(userId);
        this.attrSelMode = attrSelMode;
    }

    public void setAttrSelMode(Integer attrSelMode) {
        this.attrSelMode = attrSelMode;
    }

    private void studyAttribute(String attrName) {
        root = buildTree(attrName);
    }

    /**
     * ����������
     *
     * @param   attrName   ��Ҫѧϰ����������
     *
     * @return  �����ɵľ��������ڵ�
     *
     */
    private Node buildTree(String attrName) {
        assert attrName.equals("����") || attrName.equals("����") ||
                attrName.equals("��װ") || attrName.equals("Ь��");
        AttributeTree attrTree = new AttributeTree(table, attrName);
        return attrTree.buildTreeRecursion();
    }

    /**
     *  ��ByteArray�ķ���ʵ��Java����ȿ���
     *  ��Ҫ��try...catch...������ܳ��ֵ��쳣
     *
     *  @param  src     ��Ҫ��������List
     *
     *  @return ������õ��µ�List������
     *
     */
    private static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }


    /**
     *  ��weather��clothes�йص����ݸı���֯��ʽ��
     *  ��<����-�б�>�Ķ�Ӧ��洢����
     *
     *  @param  data    ����������������йص������б�
     *
     *  @return <����-�б�>�Ķ�Ӧͼ�ṹ
     *
     */
    private static Map<String, List<Integer>> listToMap(List<List<Integer>> data) {
        Map<String, List<Integer>> table = new HashMap<>();
        for(String name: attrList) {
            table.put(name, new ArrayList<>());
        }

        for(List<Integer> line: data) {
            for(int i = 0; i < line.size(); ++i) {
                table.get(attrList.get(i)).add(line.get(i));
            }
        }
        return table;
    }

    private void setTable() {
        table = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter lines of data: ");
        Integer lines = sc.nextInt();
        for(String attrName: attrList) {
            System.out.print("Enter " + attrName + ": ");
            table.put(attrName, new ArrayList<>());
            for(int i = 0; i < lines; ++i) {
                table.get(attrName).add(sc.nextInt());
            }
        }
    }

    private String experiment(String attrName) {
        AttributeTree attrTree = new AttributeTree(table, attrName);
        return attrTree.selectDivideAttr();
    }

    public static void main(String[] args) {
        DecisionTree tree = new DecisionTree();
        System.out.print("��������Ҫѧϰ������: ");
        String attrToLearn = new Scanner(System.in).next();
        tree.setTable();
        String result = tree.experiment(attrToLearn);
        System.out.println("ѡ������: " + result);
    }
}