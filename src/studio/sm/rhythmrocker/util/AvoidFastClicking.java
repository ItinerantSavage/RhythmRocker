package studio.sm.rhythmrocker.util;

public class AvoidFastClicking extends Thread
{
	private static boolean bool_clickable = true; 
//	private boolean processFlag = true; //默认可以点击  
	
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

    
	
    //b_next 那个被点击的控件  
//    b_next.setOnClickListener(new OnClickListener() {  
//              public void onClick(View v) {  
//                  if (processFlag) {  
//                      setProcessFlag();//  
//                      toNext();// 去执行的具体操作  
//                      new TimeThread().start();  
//                  }  
//              }  
//          });  
    /** 
       * 设置按钮在短时间内被重复点击的有效标识（true表示点击有效，false表示点击无效） 
       */  
  
     
      /** 
       * 计时线程（防止在一定时间段内重复点击按钮） 
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
