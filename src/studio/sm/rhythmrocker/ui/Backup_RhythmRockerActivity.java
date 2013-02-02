package studio.sm.rhythmrocker.ui;


import studio.sm.rhythmrocker.adapter.MusicAdapter;
import studio.sm.rhythmrocker.global.MyApp;
import studio.sm.rhythmrocker.service.OperationService;
import studio.sm.rhythmrocker.service.RhythmRockerService;
import studio.sm.rhythmrocker.service.RhythmRockerService.LocalBinder;
import studio.sm.rhythmrocker.ui.R;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Savage F. Morgan
 *  adds a progress dialog,I thank it's no need.
 *  adds a MediaScan function
 */
public class Backup_RhythmRockerActivity extends Activity implements OnItemClickListener,OnClickListener,OnSeekBarChangeListener,OnScrollListener
{
	private static final String TAG = "RhythmRocker";
	static final String str_play = "PLAY";
	static final String str_pause = "PAUSE";
	static final String str_shuffle_on = "Shuffle On";
	static final String str_shuffle_off = "Shuffle Off";
	/**
	 * global variables
	 */
	public static MusicHandler mMusicHandler = null;  //--> the handler of messages
//	public static MyMusicHandler handler_music = null;  //--> the handler of messages
	
	//--> UIs
	ListView listView_music;
	Button btn_play_pause;
	Button btn_next;
	Button btn_prev;
	SeekBar seekBar_music;
	TextView textView_music_duration;
	TextView textView_music_playing_time;
	
	RhythmRockerService boundMusicService;
	private boolean bool_bind_connection = false;

	String str_duration = null;
	int int_duration = 0;
	
	String str_playing_time = null;
	int int_seekBar_progress = 0;
	
	private Intent intent_get_music = null;
	private Intent intent_music_service = null;
	
	MusicAdapter mMusicAdapter;
//	private static MusicAdapter mMusicAdapter;
//	private int position_scroll_to = -1;
	
	private void init()
	{
		Log.i(TAG, "init()");
//		mMusicHandler = new MusicHandler(this);

		listView_music = (ListView) this.findViewById(R.id.listView_music);
		listView_music.setOnItemClickListener(this);
		if(MyApp.visibleItemCount == 0)
			listView_music.setOnScrollListener(this);
		
		btn_play_pause = (Button) this.findViewById(R.id.btn_play_pause);
		btn_play_pause.setOnClickListener(this);
		
		btn_next = (Button) this.findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		
		btn_prev = (Button) this.findViewById(R.id.btn_prev);
		btn_prev.setOnClickListener(this);
		
		seekBar_music = (SeekBar) this.findViewById(R.id.seekBar_music);
		seekBar_music.setOnSeekBarChangeListener(this);
		
		textView_music_duration = (TextView) this.findViewById(R.id.textView_music_duration);
		textView_music_playing_time = (TextView) this.findViewById(R.id.textView_music_playing_time);
		
		if(RhythmRockerService.isPlaying == true)
			activeUI(true);
		else
			activeUI(false);
		
//		bindMusicServiceAndThenGetMusic();
		if(bindMusicService())
		{
			getMusicData();
			Log.i(TAG, "MusicService is bound.");
		}
		else
		{
			Log.i(TAG, "MusicService is not bound.");
			Toast.makeText(this, "Error on launching Rhythm Rocker", Toast.LENGTH_LONG).show();
			quitApp();
		}
	}
	
	private boolean bindMusicService()
	{
		intent_music_service =  new Intent();
		intent_music_service.setClass(this, RhythmRockerService.class);
		this.startService(intent_music_service);
		bool_bind_connection = this.bindService(intent_music_service, conn, Context.BIND_AUTO_CREATE);
		return bool_bind_connection;
	}
	
	private void getMusicData()
	{
		intent_get_music = new Intent();
		intent_get_music.setClass(this, OperationService.class);
		this.startService(intent_get_music);
	}
	
	void updateListViewMusic()
//	private void updateListViewMusic()
	{
//		mMusicAdapter = new MusicAdapter(this,RhythmRockerService.music_list);
		listView_music.setAdapter(mMusicAdapter);
//		adapter.notifyDataSetChanged();
	}
	
	
	void activeUI(boolean bool)
//	private void activeUI(boolean bool)
	{
		if(bool)
		{
			this.btn_play_pause.setVisibility(View.VISIBLE);
			this.btn_next.setVisibility(View.VISIBLE);
			this.btn_prev.setVisibility(View.VISIBLE);
			this.seekBar_music.setVisibility(View.VISIBLE);
			textView_music_duration.setVisibility(View.VISIBLE);
			textView_music_playing_time.setVisibility(View.VISIBLE);
		}
		else
		{
			this.btn_play_pause.setVisibility(View.INVISIBLE);
			this.btn_next.setVisibility(View.INVISIBLE);
			this.btn_prev.setVisibility(View.INVISIBLE);
			this.seekBar_music.setVisibility(View.INVISIBLE);
			textView_music_duration.setVisibility(View.INVISIBLE);
			textView_music_playing_time.setVisibility(View.INVISIBLE);
		}
	}
	
	private void back2Home()
	{
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		this.startActivity(i);
	}
	
	private void quitApp()
	{
		finish(); //--> finish this Activity
		android.os.Process.killProcess(android.os.Process.myPid()); //--> this app quits completely
	}
	
	/**********************************************
	 * ServiceConnection for RhythmRockerService
	 *********************************************/
	private ServiceConnection conn = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			LocalBinder mLocalBinder = (LocalBinder) service;
            boundMusicService = mLocalBinder.getService();
            Log.v(TAG, "got boundMusicService in onServiceConnected()");
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			//--> do nothing here
		}
	}; //--> End of member variable conn(ServiceConnection)
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_sman_player);
		init();
	}

	
	@Override
	protected void onResume()
	{
		super.onResume();
//		Log.v(TAG, "--> onResume(),MyApp.position_scroll_to is " + String.valueOf(MyApp.position_scroll_to));
		if(MyApp.position_scroll_to <= 0)
			listView_music.setSelection(0);
		else
			listView_music.setSelection(MyApp.position_scroll_to);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_sman_player, menu);
		return true;
	}
	

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_play_pause:
//				Log.i(TAG, "--> btn_play_pause has been clicked.");
				String status = btn_play_pause.getText().toString();
				if(status.equals(str_play))
				{
					boundMusicService.resumePlayingTrack();
					btn_play_pause.setText(str_pause);
				}
				else if(status.equals(str_pause))
				{
					boundMusicService.pauseTrack();
					btn_play_pause.setText(str_play);
				}
				break;
				
			case R.id.btn_next:
				btn_play_pause.setText(str_pause);
				boundMusicService.playNextTrackManually();
				break;
				
			case R.id.btn_prev:
				boundMusicService.playPrevTrackManually();
				btn_play_pause.setText(str_pause);
				break;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3)
	{
		MyApp.position_scroll_to = position - (MyApp.visibleItemCount / 2);
		btn_play_pause.setText(str_pause);
		boundMusicService.playTrackOnItemClickFromUI(position);
		activeUI(true);
		
		mMusicAdapter.setSelectedItem(position);
		mMusicAdapter.notifyDataSetInvalidated();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		if(MyApp.visibleItemCount == 0)
			MyApp.visibleItemCount = visibleItemCount;
		else
			this.listView_music.setOnScrollListener(null);
		Log.v(TAG, "onScroll(),MyApp.visibleItemCount is " + String.valueOf(MyApp.visibleItemCount));
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		//--> do nothing here
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(RhythmRockerService.isPlaying == true)
				back2Home();
			else if(RhythmRockerService.isPlaying == false)
			{
				boundMusicService.sayGoodbye();
				if(bool_bind_connection == true)
				{
					bool_bind_connection = false;
					this.unbindService(conn);
					this.stopService(intent_music_service);
					Log.i(TAG, "onDestroy(), try to unbind and stop boundMusicService.");
				}
//				finish(); //--> finish this Activity
//				android.os.Process.killProcess(android.os.Process.myPid()); //--> this app quits completely
				quitApp();
			}
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.menu_settings:
				Toast.makeText(Backup_RhythmRockerActivity.this, "Thank you for using Rhythm Rocker -.-!\nPowered by Savage F. Morgan", Toast.LENGTH_LONG).show();
				break;
				
			case R.id.menu_shuffle:
				Log.v(TAG, "menu_shuffle");
				if(item.getTitle().equals(str_shuffle_on))
				{
					Log.v(TAG, "Shuffle ON");
					boundMusicService.shuffleOn();
					item.setTitle(str_shuffle_off);
					Toast.makeText(Backup_RhythmRockerActivity.this, "Shuffle mode is ON", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Log.v(TAG, "Shuffle OFF");
					boundMusicService.shuffleOff();
					item.setTitle(str_shuffle_on);
					Toast.makeText(Backup_RhythmRockerActivity.this, "Shuffle mode is OFF", Toast.LENGTH_SHORT).show();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		//--> do nothing here
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
		//--> do nothing here
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
//		Log.v(TAG, "onStopTrackingTouch(),progress is --> " + String.valueOf(seekBar.getProgress()));
		boundMusicService.seekTo(seekBar.getProgress());
	}

	
//	public static class MyMusicHandler extends Handler
//	{
//		private final WeakReference<RhythmRockerActivity> mActivity;
//		
//		public MyMusicHandler(RhythmRockerActivity activity)
//		{
//			mActivity = new WeakReference<RhythmRockerActivity>(activity);
//		}
//
//		@Override
//		public void handleMessage(Message msg)
//		{
////			super.handleMessage(msg);
//			RhythmRockerActivity mRhythmRocker = mActivity.get();
//			switch(msg.what)
//			{
//			case DefinedMessages.MSG_GOT_ALL_MUSIC:
//				Log.i(TAG, "--> MSG_GOT_ALL_MUSIC");
//				mRhythmRocker.updateListViewMusic();
//				if(RhythmRockerService.isPlaying == false)
//				{
//					mRhythmRocker.btn_play_pause.setText(str_play);
//				}
//				else
//				{
//					mRhythmRocker.btn_play_pause.setText(str_pause);
//				}
//				mRhythmRocker.boundMusicService.initDefaultTrackPlayOrder();
//				break;
//				
//			case DefinedMessages.MSG_NO_MUSIC:
//				Log.v(TAG, "--> MSG_NO_MUSIC");
//				mRhythmRocker.activeUI(false);
//				break;
//				
//			case DefinedMessages.MSG_LAST_TRACK_DONE:
//				Log.v(TAG, "--> MSG_LAST_TRACK_DONE");
//				mRhythmRocker.btn_play_pause.setText(str_play);
//				mRhythmRocker.activeUI(false);
//				break;
//				
//			case DefinedMessages.MSG_PLAYING_TRACK:
//				Log.v(TAG, "--> MSG_PLAYING_TRACK");
//				mRhythmRocker.str_duration = msg.obj.toString();
//				mRhythmRocker.int_duration = msg.arg1;
//				mRhythmRocker.seekBar_music.setMax(mRhythmRocker.int_duration);
//				mRhythmRocker.textView_music_duration.setText(mRhythmRocker.str_duration);
//				mRhythmRocker.btn_play_pause.setText(str_pause);
////				mRhythmRocker.activeUI(true); //--> ensure that when the mp is playing,the UIs must show
//				break;
//				
//			case DefinedMessages.MSG_SEEKER_BAR_MOVING:
//				mRhythmRocker.str_playing_time = msg.obj.toString();
//				mRhythmRocker.int_seekBar_progress = msg.arg2;
//				mRhythmRocker.textView_music_playing_time.setText(mRhythmRocker.str_playing_time);
//				mRhythmRocker.seekBar_music.setProgress(mRhythmRocker.int_seekBar_progress);
//				break;
//				
//			case DefinedMessages.MSG_RESUME_PLAYING_TRACK:
//				mRhythmRocker.btn_play_pause.setText(str_pause);
//				break;
//				
//			case DefinedMessages.MSG_HEADSET_UNPLUGGED:
//				mRhythmRocker.btn_play_pause.setText(str_play);
//				break;
//				
//			case DefinedMessages.MSG_UI_LISTVIEW_UPDATE:
//				Log.v(TAG, "--> MSG_UI_LISTVIEW_UPDATE");
//				mMusicAdapter.setSelectedItem(msg.arg1);
//				mMusicAdapter.notifyDataSetInvalidated();
//				MyApp.position_scroll_to = msg.arg1 - (MyApp.visibleItemCount / 2);
//				mRhythmRocker.listView_music.setSelection(MyApp.position_scroll_to);
////				mRhythmRocker.listView_music.setSelection(msg.arg1 - (MyApp.visibleItemCount / 2));
////				mRhythmRocker.latest_item_postion = msg.arg1;
//				break;
//			}
//		}
//	}//--> END of class MyMusicHandler
}
