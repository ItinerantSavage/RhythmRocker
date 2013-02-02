package studio.sm.rhythmrocker.util;

public class AvoidFastClicking extends Thread
{
	private static boolean bool_clickable = true; 
//	private boolean processFlag = true; //Ĭ�Ͽ��Ե��  
	
    public static synchronized void setNotClickable() 
    {  
    	bool_clickable = false;  
    }
    
    public static synchronized boolean isClickable()
    {
    	return bool_clickable;
    }
    
    
    @Override
	public void run()
	{
		super.run();
        try
		{
			sleep(3000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}  
        bool_clickable = true;
	}

    
	
    //b_next �Ǹ�������Ŀؼ�  
//    b_next.setOnClickListener(new OnClickListener() {  
//              public void onClick(View v) {  
//                  if (processFlag) {  
//                      setProcessFlag();//  
//                      toNext();// ȥִ�еľ������  
//                      new TimeThread().start();  
//                  }  
//              }  
//          });  
    /** 
       * ���ð�ť�ڶ�ʱ���ڱ��ظ��������Ч��ʶ��true��ʾ�����Ч��false��ʾ�����Ч�� 
       */  
  
     
      /** 
       * ��ʱ�̣߳���ֹ��һ��ʱ������ظ������ť�� 
       */  
//      private class TimeThread extends Thread {  
//          public void run() {  
//              try {  
//                  sleep(1000);  
//                  processFlag = true;  
//              } catch (Exception e) {  
//                  e.printStackTrace();  
//              }  
//          }  
//      }  
      
}
