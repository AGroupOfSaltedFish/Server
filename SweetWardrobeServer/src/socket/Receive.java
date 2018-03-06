package socket;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Receive {

	public Receive() {
		// TODO Auto-generated constructor stub
	}
	
	public static String receiveMessage(InputStream inputStream) throws IOException
	{
		byte[] bytes;	//��Ϊ���Ը��ã�Socket�����жϳ��ȣ����Կ���һ���õ���
		//���ȶ�ȡ�����ֽڱ�ʾ�ĳ���
		int first=inputStream.read();	//��ȡһ���ֽ�
		//�����ȡ����ֵΪ-1 ˵����������ĩβ
		//����Socket�Ѿ����رգ���ʱ������ȥ��ȡ
		if(first==-1)
		{
			return null;
		}
		int second=inputStream.read();
		int length=(first<<8)+second;		//�����ֽڱ�ʾ����
		//Ȼ�����һ��ָ�����ȵ���Ϣ����
		bytes=new byte[length];
		//�ٶ�ȡָ�����ȵ���Ϣ
		inputStream.read(bytes);
		return new String(bytes,"UTF-8");			//����������
	}
	//���ܣ��ڷ������˴���Ϊname��ͼƬ��name������jpg��׺
	public static void receivePicture(InputStream inputStream,String name) throws IOException
	{
		FileOutputStream fileoutputStream=new FileOutputStream(name);
		byte[] buf=new byte[1024];
		int len=0;
		//���ֽ�����дͼƬ����
		while((len=inputStream.read(buf))!=-1)
		{
			fileoutputStream.write(buf,0,len);
		}
		fileoutputStream.close();
	}
}
