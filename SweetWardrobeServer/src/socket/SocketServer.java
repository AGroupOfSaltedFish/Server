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

//服务器端
public class SocketServer {
	
	private static Integer userId=0;		//注册完之后，用户获得的一个id标号。服务器端配置这个id，并返回给客户端他得到的id。
	private static ServerSocket server=null;
	private static Socket socket=null;
	private static OutputStream outputStream=null;
	private static InputStream inputStream=null;
	
	public SocketServer(int port) throws IOException
	{
		//创建ServerSocket对象绑定监听端口
		server=new ServerSocket(port);
		//server将一直等待连接的到来
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
		//通过accept()方法监听客户端的请求
		//等待中，一旦获取到请求就继续执行
		socket=server.accept();
		//建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
		inputStream=socket.getInputStream();
		outputStream=socket.getOutputStream();
		
	}
	
	public static void main(String[] args) throws Exception
	{
		//用于数据库的初始化
		Main userMain = new Main();
        userMain.initDatabase();
		
		//设置指定的端口
		int port=55533;
		SocketServer socketserver=new SocketServer(port);
		
		//这里采用循环来处理多个Socket请求
		while(true) {
			//通过accept()方法监听客户端的请求
			//等待中，一旦获取到请求就继续执行
			//建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
			System.out.println("等待请求！");
			socketserver.Start();
			System.out.println("请求收到！");
			while(true)
			{
				String message=Receive.receiveMessage(inputStream);
				if(message==null)		//表明Socket已经被关闭，此时将不再去读取
				{
					break;
				}
				//将message根据逗号拆分成段
				//每一个message信息为 服务类型(0表示用户注册，1表示用户登陆，2表示用户注销，3表示添加衣物，4表示添加装扮、添加天气，5表示发送图片信息)
				//接着跟用户id(除了用户注册不是通过通信获得的id，通过服务器模块获得，其余服务中都是通过通信获得)
				String[] elements=message.split(",");
				if(elements[0].equals("0"))						//表示用户注册
				{
					assert elements.length==3;		//注册服务中，元素个数为3
					System.out.println("用户注册的信息为:"+elements[1]+" "+elements[2]);
					int id=userId;
					if(userMain.insertUser(id, elements[1], elements[2])==true)
					{
						System.out.println("服务器端注册信息成功！");
						userId++;			//为下一次注册作准备
						Send.sendMessage("1,"+id, outputStream);  //回传，表示注册成功,同时在客户端底层完成id的设置
					}
					else
					{
						System.out.println("服务器端注册信息失败！");
						Send.sendMessage("0", outputStream);  //回传，表示注册失败
					}
				}
				else if(elements[0].equals("1"))					//表示用户登陆，这个可以缓缓
				{
					assert elements.length==3;		//登陆服务，元素个数为3
					System.out.println("服务器端用户登陆的信息为:"+elements[1]+" "+elements[2]);
					//如果在数据库中信息存在，则登陆成功，并将id写回到客户端底层
					//if(userMain.checkUser(elements[1],elements[2])==true)
					//{
					//		System.out.println("登陆成功！");
					//		Send.sendMessage("1,"+id,outputStream);	//回传，表示登陆成功，并将找到的id返回	
					//}
					//else
					//{
					//		System.out.println("登陆失败！");
					//		Send.sendMessage("0",outputStream);	//回传，表示登陆失败
					//}
				}
				else if(elements[0].equals("2"))					//表示用户注销
				{
					assert elements.length==2;		//注销服务，元素个数为2
					int id=Integer.valueOf(elements[1]).intValue();
					System.out.println("服务器端用户注销的id为:"+elements[1]);
					//elements[1]表示用户id
					//如果删除成功
					if(userMain.deleteUser(id)==true)
					{
						System.out.println("删除成功！");
						Send.sendMessage("1",outputStream);	//回传，表示注销成功	
					}
					else
					{
						System.out.println("删除失败！");
						Send.sendMessage("0",outputStream);	//回传，表示注销失败
					}
				}
				else if(elements[0].equals("3"))					//表示添加单件衣物
				{
					assert elements.length==7;		//添加单间衣服服务，元素个数为7
					System.out.println("服务器端用户id为:"+elements[1]+","+"单件衣服信息为："+elements[2]+","+elements[3]+","+elements[4]+","+elements[5]+","+elements[6]);
					int id=Integer.valueOf(elements[1]).intValue();
					int ele1=Integer.valueOf(elements[2]).intValue();
					int ele2=Integer.valueOf(elements[3]).intValue();
					int ele3=Integer.valueOf(elements[4]).intValue();
					int ele4=Integer.valueOf(elements[5]).intValue();
					int ele5=Integer.valueOf(elements[6]).intValue();
					Clothes cloth=new Clothes(ele1,ele2,ele3,ele4,ele5);
					//传给数据库
					userMain.insertClothes(id, cloth);
					//同时应该将数据库的数据给决策树，然后从决策树得到信息，再返回给客户端
					//假设这里已经得到信息了，要进行写回
					//写回的具体信息还没有确定
					Send.sendMessage("hhhh", outputStream);
				}
				else if(elements[0].equals("4"))							//表示传送的是一套套装信息、一天的装扮信息
				{
					System.out.println("服务器端传送到服务器一套套装和一天的天气信息！");
					
					int ele1=Integer.valueOf(elements[1]).intValue();		//用户id
					int ele2=Integer.valueOf(elements[2]).intValue();		//天气信息
					//知识点
					//整形至枚举类型的转化
						/*
						1.  enum<->int
						enum -> int: int i = enumType.value.ordinal();
						int -> enum: enumType b= enumType.values()[i];
						*/
					List<Integer> clothesIdList = new ArrayList<>();				//构造衣物列表来存储单件衣服，以实现一套套装
				    for(int i=3;i<elements.length; i=i+1)
				    {
				    	clothesIdList.add(Integer.valueOf(elements[i]).intValue());
				    }
				    Suit suit = new Suit(clothesIdList);
				  //----------------------------------------------------------------------> 这里有问题
				  //  userMain.insertHistory(ele1,suit,WeatherState.values()[ele2]);
				}
				else if(elements[0].equals("5"))					//表示传送的是一张图片信息
				{
					assert elements.length==3;
					//int ele1=Integer.valueOf(elements[1]).intValue();		//用户id
					//int ele2=Integer.valueOf(elements[2]).intValue();		//该用户的图片编号
					String name=elements[1]+"_"+elements[2]+".jpg";
					Receive.receivePicture(inputStream, name);
					System.out.println("服务器端图片保存成功!保存图片名为:"+name);
				}
					
				
			}
			
		}
		
			
	}
}
