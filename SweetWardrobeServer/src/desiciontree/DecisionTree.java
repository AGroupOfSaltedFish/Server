package desiciontree;

import desiciontree.TreeNode.*;

import java.io.*;
import java.util.*;

import database.Main;
import database.Suit;
import database.UserInfo;
import database.Weather;


public class DecisionTree {
    private Integer attrSelMode;    //最佳分裂属性选择模式，0表示以信息增益度衡量，1表示以信息增益率衡量
    private Map<String, List<Integer>> table;
    private Node root;
    private static final List<String> attrList = new ArrayList<>();

    private DecisionTree() {
        attrSelMode = 0;
        attrList.add("天气");
        attrList.add("最高温度");
        attrList.add("最低温度");
        attrList.add("最高湿度");
        attrList.add("最低湿度");
        attrList.add("最大风力");
        attrList.add("最小风力");
        attrList.add("外套");
        attrList.add("上衣");
        attrList.add("裤装");
        attrList.add("鞋子");
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
     * 构建决策树
     *
     * @param   attrName   将要学习的属性名称
     *
     * @return  所建成的决策树根节点
     *
     */
    private Node buildTree(String attrName) {
        assert attrName.equals("外套") || attrName.equals("上衣") ||
                attrName.equals("裤装") || attrName.equals("鞋子");
        AttributeTree attrTree = new AttributeTree(table, attrName);
        return attrTree.buildTreeRecursion();
    }

    /**
     *  用ByteArray的方法实现Java的深度拷贝
     *  需要用try...catch...处理可能出现的异常
     *
     *  @param  src     需要被拷贝的List
     *
     *  @return 拷贝后得到新的List的引用
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
     *  将weather和clothes有关的数据改变组织形式，
     *  以<属性-列表>的对应表存储起来
     *
     *  @param  data    与天气、衣物搭配有关的数据列表
     *
     *  @return <属性-列表>的对应图结构
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
        System.out.print("请输入需要学习的属性: ");
        String attrToLearn = new Scanner(System.in).next();
        tree.setTable();
        String result = tree.experiment(attrToLearn);
        System.out.println("选择属性: " + result);
    }
}