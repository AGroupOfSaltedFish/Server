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
//客户端
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
		//与服务器建立连接
		client=new Socket(host,post);
		//建立连接后获得的输入、输出流
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
	
	//用于外部进行用户注册是调用
	//客户端传送至服务器，服务器给数据给数据库，数据库插入，并判断是否重复
	//如果重复，返回false，即注册失败
	//不重复，返回true，即注册成功
	public boolean sendUserInfo_register(String name,String password) throws IOException
	{
		String message="0,"+name+","+password;
		Start();
		sendMessage(message);	//期待得到返回的信息
		String[] result=receiveMessage().split(",");	//返回的信息
		if(result[0].equals("1"))
		{
			System.out.println("客户端注册成功！其获得的id号为："+result[1]);
			//可能还要接下来返回至客户端下层
			Close();
			return true;
		}
		else
		{
			System.out.println("客户端注册失败！");
			Close();
			return false;
		}
	}
	
	//用于用户登陆
	public boolean sendUserInfo_login(String name,String password) throws IOException
	{
		String message="1,"+name+","+password;
		Start();
		sendMessage(message);	//期待得到返回的信息
		String[] result=receiveMessage().split(",");	//返回的信息
		if(result[0].equals("1"))
		{
			System.out.println("客户端登陆成功！其获得的id号为："+result[1]);
			//接下来也要返回客户端下层
			//需要返回的不止id，还有所有信息
			Close();
			return true;
		}
		else
		{
			System.out.println("客户端登陆失败！");
			Close();
			return false;
		}
	}
	
	//用于用户注销
	//具体含义我似乎理解的不对
	//一：服务器端删除该用户的信息，同时客户端将所有信息删除
	//二：只是简单地登入、登出，类似QQ
	//这里实现的是一
	public boolean sendUserInfo_logoff(Integer id,String name,String password) throws IOException
	{
		String message="2,"+id+","+name+","+password;
		Start();
		sendMessage(message);	//期待得到返回的信息
		String result=receiveMessage();	//返回信息
		if(result.equals("1"))
		{
			System.out.println("客户端注销成功！");
			Close();
			return true;
		}
		else
		{
			System.out.println("客户端注销失败！");
			Close();
			return false;
		}
	}
	
	//传送单件衣服的数据信息
	public void sendClothInfo_text(Integer user_id,Clothes cloth) throws IOException
	{
		String message="3,"+user_id+","+cloth.getClothesId()+","+cloth.getClothesClass()+","+cloth.getClothesType()+","+cloth.getClothesColor()+","+cloth.getClothesDirtyDegree();
		Start();
		sendMessage(message);					//发送的是一件衣服的信息
		String result=receiveMessage();		//得到的信息应该是推荐信息，服务器那块有对应的逻辑
		System.out.println("客户端推荐信息为："+result);
		Close();
	}
	
	//传送单件衣服的图片信息
	//addr表示图片在本地的地址
	//利用user_id和cloth_id构造出在服务器端的图片存储名
	public void sendClothInfo_picture(String addr,Integer user_id,Integer cloth_id) throws IOException
	{
		String message="5,"+user_id+","+cloth_id;
		Start();
		sendMessage(message);	//"根据表示6，服务器端据此判断出其是在发送图片了"
		sendPicture(addr);
		System.out.println("客户端模块发送图片信息完成!");
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
		System.out.println("客户端发送一天的历史信息成功!");
		Close();
	}
	
	public static void main(String args[]) throws Exception
	{
		//连接的服务器IP地址和端口
		String host="127.0.0.1";
		int post=55533;
		//UserInfo(Integer u_id, String u_name, String u_password, ClothesInfo u_clothesInfo)
		UserInfo userinfo=new UserInfo(1,null,null,null);
		SocketClient socketclient=new SocketClient(host,post);
		//在实际项目中，以上建立连接部分应该在MainActivity中就进行调用，注意应该Server先启动，然后才是Client启动
		
		//目前，我规定发送的数据只有两类，一个是用户名、密码，一个是衣服信息
		//每一个信息中不同元素都采用逗号分隔，这里假设潜在的用户名和密码等信息不会出现逗号这样的元素
		//后期对元素处理时，通过分割逗号来实现不同元素的获取
		
		String name="lgy";
		String password="1234567";
		socketclient.sendUserInfo_register(name, password);
		
		Clothes cloth=new Clothes(1,1,1,1,1);
		socketclient.sendClothInfo_text(userinfo.getId(), cloth);
		
		String addr="client.jpg";
		socketclient.sendClothInfo_picture(addr, userinfo.getId(), cloth.getClothesId());
		/*为了第二次测试
		String message="0,1,lgy,1234567";				//0表示注册，1表示登陆 ，2表示注销 后面跟着用户的id，用户名，和密码
		socketclient.sendUserInfo(message);
		
		message="lgy,s123,1,2,3,4,5,6";
		String addr="client.jpg";
		socketclient.sendClothInfo(message, addr);
		*/
		
		
		/*测试
		String message="lgy,1234567";		//用户名信息，不期待得到反馈信息
		socketclient.Start();
		socketclient.sendMessage(message);
		socketclient.Close();
		

		message="lgy,s123,1,2,3,4,5,6";		//一件衣服信息，期待得到返回信息
		socketclient.Start();
		socketclient.sendMessage(message);
		String result=socketclient.receiveMessage();
		System.out.println(result);
		socketclient.Close();
		*/
		
		/*
		message="lgy,s123,1,2,3,4,5,6";		//一件衣服信息，期待得到返回信息
		socketclient.sendMessage(message);
		
		//得到的返回信息
		String result=socketclient.receiveMessage();
		System.out.println(result);
		*/
		
		/*测试
		message="picture";
		socketclient.Start();
		socketclient.sendMessage(message);	//"picture算是标识，服务器端据此判断出其是在发送图片了"
		message="client.jpg";
		socketclient.sendPicture(message);
		socketclient.Close();
		*/
		
		//期待返回的信息
	}
}
