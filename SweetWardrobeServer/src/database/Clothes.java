package database;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clothes {

	public static Integer defaultClothes = -1;
    private List<Integer> clothes;              /*len = 5      编号   类别      种类      颜色      脏衣指数

                                                外套      上衣      裤装          鞋子
                                        0:      夹克      卫衣      休闲短裤      休闲/运动鞋
                                        1:      大衣      毛衣      休闲长裤      靴子
                                        2:      风衣      针织衫    牛仔裤        雪地靴
                                        3:      棉衣      衬衫      工装裤        凉鞋/凉拖
                                        4:      皮衣      马甲      西裤
                                        5:      羽绒服     T恤
                                        6:      西装       POLO
                                        7:      冲锋衣     背心
                                        */

    //这里改为public
    public Clothes(List<Integer> features) {
        assert features.size() == 5;
        clothes = features;
    }

    //这里改为public
    public Clothes(Integer c_id, Integer c_class, Integer c_type, Integer c_color, Integer c_dirtyDegree) {
        clothes = new ArrayList<>(5);
        clothes.add(c_id);
        clothes.add(c_class);
        clothes.add(c_type);
        clothes.add(c_color);
        clothes.add(c_dirtyDegree);
    }

    public Integer getClothesId() {
        return clothes.get(0);
    }

    public Integer getClothesClass() {
        return clothes.get(1);
    }

    public Integer getClothesType() {
        return clothes.get(2);
    }

    public Integer getClothesColor() {
        return clothes.get(3);
    }

    public Integer getClothesDirtyDegree() {
        return clothes.get(4);
    }

    public String transformClothesToString() {
        return getClothesId().toString() + ",";
    }
    
    public void getInfo()
    {
    	String message="";
    	for(int i=0;i<clothes.size();i++)
    	{
    		message+=clothes.get(i);
    		message+=",";
    	}
    	System.out.println(message);
    }
}
