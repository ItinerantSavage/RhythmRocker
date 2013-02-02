package studio.sm.rhythmrocker.global;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

public class MyApp extends Application
{
	/********************************************
	 ***** Global variables *********************
	 ********************************************/
	//--> Set to true to enable verbose logging.
	public final static boolean LOGV_ENABLED = false;
	
	//--> Music data list(from ContentProvider)
	public static ArrayList<HashMap<String,Object>> music_list = null;
	
	//--> get music messages
	public static final int MSG_NO_MUSIC = 0;
	public static final int MSG_GOT_ALL_MUSIC = 1;
	//--> player states messages
	public static final int MSG_LAST_TRACK_DONE = 2;
	public static final int MSG_PLAYING_TRACK = 3;
	public static final int MSG_SEEKER_BAR_MOVING = 4;
	public static final int MSG_RESUME_PLAYING_TRACK = 5;
	//--> head set state messages
	public static final int MSG_HEADSET_PLUGGED = 6;
	public static final int MSG_HEADSET_UNPLUGGED = 7;
	//--> UI update messages
	public static final int MSG_UI_LISTVIEW_UPDATE = 8;
	
	//--> liveView's visibleItemCount
	public static int visibleItemCount = 0;
	public static int position_scroll_to = -2;
	
	/**************************************************/
	
	
	private static MyApp singleton;
	
	public static MyApp getApplication()
	{
		return singleton;
	}
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		singleton = this;
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
	}

} //--> End of class MyApp


//switch(MODE_ORDER)
//{
//	case MODE_DEFAULT_ORDER:
//		
//		break;
//	
//	case MODE_RANDOM_ORDER:
//		
//		break;
//}