package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import database.Clothes;
import database.ClothesInfo;
import database.UserInfo;
import database.WeatherState;
//�ͻ���
public class SocketClient {
	
	private Socket client = null;
	private static OutputStream outputStream=null;
	private static InputStream inputStream=null;
	private String host;
	private int post;
	
	public SocketClient(String host,int post) 
	{
		this.host=host;
		this.post=post;
	}
	
	public void Start() throws IOException
	{
		//���������������
		client=new Socket(host,post);
		//�������Ӻ��õ����롢�����
		outputStream=client.getOutputStream();
		inputStream=client.getInputStream();
	}
	
	public void Close() throws IOException
	{
		outputStream.close();
		inputStream.close();
		client.close();
	}
	
	
	private void sendMessage(String message) throws IOException 
	{
		Send.sendMessage(message, outputStream);
	}
	
	private void sendPicture(String addr) throws IOException
	{
		Send.sendPicture(addr, outputStream);
	}
	
	private String receiveMessage() throws IOException
	{
		String result=Receive.receiveMessage(inputStream);
		return result;
	}
	
	private void receivePicture(String addr) throws IOException
	{
		Receive.receivePicture(inputStream, addr);
	}
	
	//�����ⲿ�����û�ע���ǵ���
	//�ͻ��˴������������������������ݸ����ݿ⣬���ݿ���룬���ж��Ƿ��ظ�
	//����ظ�������false����ע��ʧ��
	//���ظ�������true����ע��ɹ�
	public boolean sendUserInfo_register(String name,String password) throws IOException
	{
		String message="0,"+name+","+password;
		Start();
		sendMessage(message);	//�ڴ��õ����ص���Ϣ
		String[] result=receiveMessage().split(",");	//���ص���Ϣ
		if(result[0].equals("1"))
		{
			System.out.println("�ͻ���ע��ɹ������õ�id��Ϊ��"+result[1]);
			//���ܻ�Ҫ�������������ͻ����²�
			Close();
			return true;
		}
		else
		{
			System.out.println("�ͻ���ע��ʧ�ܣ�");
			Close();
			return false;
		}
	}
	
	//�����û���½
	public boolean sendUserInfo_login(String name,String password) throws IOException
	{
		String message="1,"+name+","+password;
		Start();
		sendMessage(message);	//�ڴ��õ����ص���Ϣ
		String[] result=receiveMessage().split(",");	//���ص���Ϣ
		if(result[0].equals("1"))
		{
			System.out.println("�ͻ��˵�½�ɹ������õ�id��Ϊ��"+result[1]);
			//������ҲҪ���ؿͻ����²�
			//��Ҫ���صĲ�ֹid������������Ϣ
			Close();
			return true;
		}
		else
		{
			System.out.println("�ͻ��˵�½ʧ�ܣ�");
			Close();
			return false;
		}
	}
	
	//�����û�ע��
	//���庬�����ƺ����Ĳ���
	//һ����������ɾ�����û�����Ϣ��ͬʱ�ͻ��˽�������Ϣɾ��
	//����ֻ�Ǽ򵥵ص��롢�ǳ�������QQ
	//����ʵ�ֵ���һ
	public boolean sendUserInfo_logoff(Integer id,String name,String password) throws IOException
	{
		String message="2,"+id+","+name+","+password;
		Start();
		sendMessage(message);	//�ڴ��õ����ص���Ϣ
		String result=receiveMessage();	//������Ϣ
		if(result.equals("1"))
		{
			System.out.println("�ͻ���ע���ɹ���");
			Close();
			return true;
		}
		else
		{
			System.out.println("�ͻ���ע��ʧ�ܣ�");
			Close();
			return false;
		}
	}
	
	//���͵����·���������Ϣ
	public void sendClothInfo_text(Integer user_id,Clothes cloth) throws IOException
	{
		String message="3,"+user_id+","+cloth.getClothesId()+","+cloth.getClothesClass()+","+cloth.getClothesType()+","+cloth.getClothesColor()+","+cloth.getClothesDirtyDegree();
		Start();
		sendMessage(message);					//���͵���һ���·�����Ϣ
		String result=receiveMessage();		//�õ�����ϢӦ�����Ƽ���Ϣ���������ǿ��ж�Ӧ���߼�
		System.out.println("�ͻ����Ƽ���ϢΪ��"+result);
		Close();
	}
	
	//���͵����·���ͼƬ��Ϣ
	//addr��ʾͼƬ�ڱ��صĵ�ַ
	//����user_id��cloth_id������ڷ������˵�ͼƬ�洢��
	public void sendClothInfo_picture(String addr,Integer user_id,Integer cloth_id) throws IOException
	{
		String message="5,"+user_id+","+cloth_id;
		Start();
		sendMessage(message);	//"���ݱ�ʾ6���������˾ݴ��жϳ������ڷ���ͼƬ��"
		sendPicture(addr);
		System.out.println("�ͻ���ģ�鷢��ͼƬ��Ϣ���!");
		Close();
	}
	
	public void sendHistoryInfo(Integer user_id,WeatherState weather,List<Integer> clothesIdList) throws IOException
	{
		String message="4,"+user_id+","+weather.ordinal()+",";
		for(int i=0;i<clothesIdList.size();i++)
		{
			message+=clothesIdList.get(i);
			if(i!=(clothesIdList.size()-1))
			{
				message+=",";
			}
		}
		Start();
		sendMessage(message);
		System.out.println("�ͻ��˷���һ�����ʷ��Ϣ�ɹ�!");
		Close();
	}
	
	public static void main(String args[]) throws Exception
	{
		//���ӵķ�����IP��ַ�Ͷ˿�
		String host="127.0.0.1";
		int post=55533;
		//UserInfo(Integer u_id, String u_name, String u_password, ClothesInfo u_clothesInfo)
		UserInfo userinfo=new UserInfo(1,null,null,null);
		SocketClient socketclient=new SocketClient(host,post);
		//��ʵ����Ŀ�У����Ͻ������Ӳ���Ӧ����MainActivity�оͽ��е��ã�ע��Ӧ��Server��������Ȼ�����Client����
		
		//Ŀǰ���ҹ涨���͵�����ֻ�����࣬һ�����û��������룬һ�����·���Ϣ
		//ÿһ����Ϣ�в�ͬԪ�ض����ö��ŷָ����������Ǳ�ڵ��û������������Ϣ������ֶ���������Ԫ��
		//���ڶ�Ԫ�ش���ʱ��ͨ���ָ����ʵ�ֲ�ͬԪ�صĻ�ȡ
		
		String name="lgy";
		String password="1234567";
		socketclient.sendUserInfo_register(name, password);
		
		Clothes cloth=new Clothes(1,1,1,1,1);
		socketclient.sendClothInfo_text(userinfo.getId(), cloth);
		
		String addr="client.jpg";
		socketclient.sendClothInfo_picture(addr, userinfo.getId(), cloth.getClothesId());
		/*Ϊ�˵ڶ��β���
		String message="0,1,lgy,1234567";				//0��ʾע�ᣬ1��ʾ��½ ��2��ʾע�� ��������û���id���û�����������
		socketclient.sendUserInfo(message);
		
		message="lgy,s123,1,2,3,4,5,6";
		String addr="client.jpg";
		socketclient.sendClothInfo(message, addr);
		*/
		
		
		/*����
		String message="lgy,1234567";		//�û�����Ϣ�����ڴ��õ�������Ϣ
		socketclient.Start();
		socketclient.sendMessage(message);
		socketclient.Close();
		

		message="lgy,s123,1,2,3,4,5,6";		//һ���·���Ϣ���ڴ��õ�������Ϣ
		socketclient.Start();
		socketclient.sendMessage(message);
		String result=socketclient.receiveMessage();
		System.out.println(result);
		socketclient.Close();
		*/
		
		/*
		message="lgy,s123,1,2,3,4,5,6";		//һ���·���Ϣ���ڴ��õ�������Ϣ
		socketclient.sendMessage(message);
		
		//�õ��ķ�����Ϣ
		String result=socketclient.receiveMessage();
		System.out.println(result);
		*/
		
		/*����
		message="picture";
		socketclient.Start();
		socketclient.sendMessage(message);	//"picture���Ǳ�ʶ���������˾ݴ��жϳ������ڷ���ͼƬ��"
		message="client.jpg";
		socketclient.sendPicture(message);
		socketclient.Close();
		*/
		
		//�ڴ����ص���Ϣ
	}
}
