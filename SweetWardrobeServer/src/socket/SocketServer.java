package socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import database.Clothes;
import database.Main;
import database.Suit;
import database.WeatherState;

//��������
public class SocketServer {
	
	private static Integer userId=0;		//ע����֮���û���õ�һ��id��š����������������id�������ظ��ͻ������õ���id��
	private static ServerSocket server=null;
	private static Socket socket=null;
	private static OutputStream outputStream=null;
	private static InputStream inputStream=null;
	
	public SocketServer(int port) throws IOException
	{
		//����ServerSocket����󶨼����˿�
		server=new ServerSocket(port);
		//server��һֱ�ȴ����ӵĵ���
	}
	
	public void Close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
		server.close();
	}
	
	public void Start() throws IOException
	{
		//ͨ��accept()���������ͻ��˵�����
		//�ȴ��У�һ����ȡ������ͼ���ִ��
		socket=server.accept();
		//���������Ӻ󣬴�socket�л�ȡ�����������������������ж�ȡ
		inputStream=socket.getInputStream();
		outputStream=socket.getOutputStream();
		
	}
	
	public static void main(String[] args) throws Exception
	{
		//�������ݿ�ĳ�ʼ��
		Main userMain = new Main();
        userMain.initDatabase();
		
		//����ָ���Ķ˿�
		int port=55533;
		SocketServer socketserver=new SocketServer(port);
		
		//�������ѭ����������Socket����
		while(true) {
			//ͨ��accept()���������ͻ��˵�����
			//�ȴ��У�һ����ȡ������ͼ���ִ��
			//���������Ӻ󣬴�socket�л�ȡ�����������������������ж�ȡ
			System.out.println("�ȴ�����");
			socketserver.Start();
			System.out.println("�����յ���");
			while(true)
			{
				String message=Receive.receiveMessage(inputStream);
				if(message==null)		//����Socket�Ѿ����رգ���ʱ������ȥ��ȡ
				{
					break;
				}
				//��message���ݶ��Ų�ֳɶ�
				//ÿһ��message��ϢΪ ��������(0��ʾ�û�ע�ᣬ1��ʾ�û���½��2��ʾ�û�ע����3��ʾ������4��ʾ���װ�硢���������5��ʾ����ͼƬ��Ϣ)
				//���Ÿ��û�id(�����û�ע�᲻��ͨ��ͨ�Ż�õ�id��ͨ��������ģ���ã���������ж���ͨ��ͨ�Ż��)
				String[] elements=message.split(",");
				if(elements[0].equals("0"))						//��ʾ�û�ע��
				{
					assert elements.length==3;		//ע������У�Ԫ�ظ���Ϊ3
					System.out.println("�û�ע�����ϢΪ:"+elements[1]+" "+elements[2]);
					int id=userId;
					if(userMain.insertUser(id, elements[1], elements[2])==true)
					{
						System.out.println("��������ע����Ϣ�ɹ���");
						userId++;			//Ϊ��һ��ע����׼��
						Send.sendMessage("1,"+id, outputStream);  //�ش�����ʾע��ɹ�,ͬʱ�ڿͻ��˵ײ����id������
					}
					else
					{
						System.out.println("��������ע����Ϣʧ�ܣ�");
						Send.sendMessage("0", outputStream);  //�ش�����ʾע��ʧ��
					}
				}
				else if(elements[0].equals("1"))					//��ʾ�û���½��������Ի���
				{
					assert elements.length==3;		//��½����Ԫ�ظ���Ϊ3
					System.out.println("���������û���½����ϢΪ:"+elements[1]+" "+elements[2]);
					//��������ݿ�����Ϣ���ڣ����½�ɹ�������idд�ص��ͻ��˵ײ�
					//if(userMain.checkUser(elements[1],elements[2])==true)
					//{
					//		System.out.println("��½�ɹ���");
					//		Send.sendMessage("1,"+id,outputStream);	//�ش�����ʾ��½�ɹ��������ҵ���id����	
					//}
					//else
					//{
					//		System.out.println("��½ʧ�ܣ�");
					//		Send.sendMessage("0",outputStream);	//�ش�����ʾ��½ʧ��
					//}
				}
				else if(elements[0].equals("2"))					//��ʾ�û�ע��
				{
					assert elements.length==2;		//ע������Ԫ�ظ���Ϊ2
					int id=Integer.valueOf(elements[1]).intValue();
					System.out.println("���������û�ע����idΪ:"+elements[1]);
					//elements[1]��ʾ�û�id
					//���ɾ���ɹ�
					if(userMain.deleteUser(id)==true)
					{
						System.out.println("ɾ���ɹ���");
						Send.sendMessage("1",outputStream);	//�ش�����ʾע���ɹ�	
					}
					else
					{
						System.out.println("ɾ��ʧ�ܣ�");
						Send.sendMessage("0",outputStream);	//�ش�����ʾע��ʧ��
					}
				}
				else if(elements[0].equals("3"))					//��ʾ��ӵ�������
				{
					assert elements.length==7;		//��ӵ����·�����Ԫ�ظ���Ϊ7
					System.out.println("���������û�idΪ:"+elements[1]+","+"�����·���ϢΪ��"+elements[2]+","+elements[3]+","+elements[4]+","+elements[5]+","+elements[6]);
					int id=Integer.valueOf(elements[1]).intValue();
					int ele1=Integer.valueOf(elements[2]).intValue();
					int ele2=Integer.valueOf(elements[3]).intValue();
					int ele3=Integer.valueOf(elements[4]).intValue();
					int ele4=Integer.valueOf(elements[5]).intValue();
					int ele5=Integer.valueOf(elements[6]).intValue();
					Clothes cloth=new Clothes(ele1,ele2,ele3,ele4,ele5);
					//�������ݿ�
					userMain.insertClothes(id, cloth);
					//ͬʱӦ�ý����ݿ�����ݸ���������Ȼ��Ӿ������õ���Ϣ���ٷ��ظ��ͻ���
					//���������Ѿ��õ���Ϣ�ˣ�Ҫ����д��
					//д�صľ�����Ϣ��û��ȷ��
					Send.sendMessage("hhhh", outputStream);
				}
				else if(elements[0].equals("4"))							//��ʾ���͵���һ����װ��Ϣ��һ���װ����Ϣ
				{
					System.out.println("�������˴��͵�������һ����װ��һ���������Ϣ��");
					
					int ele1=Integer.valueOf(elements[1]).intValue();		//�û�id
					int ele2=Integer.valueOf(elements[2]).intValue();		//������Ϣ
					//֪ʶ��
					//������ö�����͵�ת��
						/*
						1.  enum<->int
						enum -> int: int i = enumType.value.ordinal();
						int -> enum: enumType b= enumType.values()[i];
						*/
					List<Integer> clothesIdList = new ArrayList<>();				//���������б����洢�����·�����ʵ��һ����װ
				    for(int i=3;i<elements.length; i=i+1)
				    {
				    	clothesIdList.add(Integer.valueOf(elements[i]).intValue());
				    }
				    Suit suit = new Suit(clothesIdList);
				  //----------------------------------------------------------------------> ����������
				  //  userMain.insertHistory(ele1,suit,WeatherState.values()[ele2]);
				}
				else if(elements[0].equals("5"))					//��ʾ���͵���һ��ͼƬ��Ϣ
				{
					assert elements.length==3;
					//int ele1=Integer.valueOf(elements[1]).intValue();		//�û�id
					//int ele2=Integer.valueOf(elements[2]).intValue();		//���û���ͼƬ���
					String name=elements[1]+"_"+elements[2]+".jpg";
					Receive.receivePicture(inputStream, name);
					System.out.println("��������ͼƬ����ɹ�!����ͼƬ��Ϊ:"+name);
				}
					
				
			}
			
		}
		
			
	}
}
