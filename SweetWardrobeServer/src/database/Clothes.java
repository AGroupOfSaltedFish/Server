package database;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clothes {

	public static Integer defaultClothes = -1;
    private List<Integer> clothes;              /*len = 5      ���   ���      ����      ��ɫ      ����ָ��

                                                ����      ����      ��װ          Ь��
                                        0:      �п�      ����      ���ж̿�      ����/�˶�Ь
                                        1:      ����      ë��      ���г���      ѥ��
                                        2:      ����      ��֯��    ţ�п�        ѩ��ѥ
                                        3:      ����      ����      ��װ��        ��Ь/����
                                        4:      Ƥ��      ���      ����
                                        5:      ���޷�     T��
                                        6:      ��װ       POLO
                                        7:      �����     ����
                                        */

    //�����Ϊpublic
    public Clothes(List<Integer> features) {
        assert features.size() == 5;
        clothes = features;
    }

    //�����Ϊpublic
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
