package socket;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Send {

	public Send() {
		// TODO Auto-generated constructor stub
	}
	
	//��ʵ����Ŀ�У����ظ��ݵõ���һ���µ�������Ϣ���ͻῪʼ����
	//����������һ���������ñ����ܹ���������
	public static void sendMessage(String message,OutputStream outputStream) throws IOException 
	{
		//���ȼ��㴫����Ϣ�ĳ���
		byte[] sendBytes=message.getBytes("UTF-8");
		//�Ƚ���Ϣ�ĳ������ȷ���
		outputStream.write(sendBytes.length>>8);			//ע��˴�Ĭ����Ϣ�ĳ����Ƕ������ֽھ��Կ��Ա�ʾ�ģ���������Ŀ��Ի���4�ֽ�
																						//������ʡ�ռ䣬����ʹ�ñ䳤�ֽڱ�ʾ��Ϣ�ĳ���
		outputStream.write(sendBytes.length);				//ע��˷���ֻ���ݵ�8λ
		//Ȼ���ٷ�����Ϣ
		outputStream.write(sendBytes);
		//flushˢ�´��������ǿ��д�����л��������ֽ�
		//flush�ĳ���Э����������������ʵ���Ѿ���������ǰд����κ�����
		//����ô˷���Ӧ����Щ�ֽ�����д������Ԥ�ڵ�Ŀ��
		//��֮��flushǿ�ƽ����������е��������
		outputStream.flush();
	}
	public static void sendPicture(String addr,OutputStream outputStream) throws IOException
	{
		FileInputStream fileinputStream=new FileInputStream(addr);
		byte[] buf=new byte[1024];			
		int len=0;
		//�����������Ͷ������
		//ѭ��Ͷ�ţ�Ͷ��Ϊֹ
		while((len=fileinputStream.read(buf))!=-1)
		{
			outputStream.write(buf,0,len);
		}
		outputStream.flush();
	}
}
