package studio.sm.rhythmrocker.service;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import studio.sm.rhythmrocker.global.MyApp;
import studio.sm.rhythmrocker.ui.RhythmRockerActivity;
import studio.sm.rhythmrocker.ui.R.drawable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
//import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

@SuppressWarnings("deprecation")
public class RhythmRockerService extends Service implements OnCompletionListener, OnErrorListener, OnSeekCompleteListener, OnPreparedListener
{
	/***** Global static variable *****/
	public static boolean isPlaying = false;
//	public static boolean isShuffleOn = false;
//	public static int default_position_music = -1;
//	public static ArrayList<HashMap<String,Object>> MyApp.MyApp.music_list = null;
	/******************************************/
	
	private boolean isTheLastSong = false;
	private static final String TAG = "RhythmRockerService";
//	private MyBinder myBinder = new MyBinder();
	private MediaPlayer mp = null;
	private Message msg;
	
	private static final String tip_app_title = "RhythmRocker is serving you";
	private static final String tip_welcome = "Welcome back";
	private static final String tip_play_completely = "All songs played completely";
	private static final String tip_say_goodbye = "Goodbye";
	
	private NotificationManager notificationManager;
	private PendingIntent pendingIntent;
	private static Notification notification;
	private String last_tickerText = null;
	private String current_tickerText = null;
	private int times_click = 0;
	private static final int notification_play_completely = 0;
	private static final int notification_play_next_track = 1;
	private static final int notification_say_goodbye = 2;
	private int id_notification = 1;
	
	private boolean isOnItemClickedInRandomMode = false;
	
	private static String currentMusicFile = null;
	private int duration_in_seconds = 0;
	private Timer timer = new Timer();;
	private TimerTask timerTask = null;
		
	private int[] DEFAULT_TRACK_PLAY_ORDER = null;
	private static int[] RANDOM_TRACK_PLAY_ORDER = null;
	private int size_order = -1;
	
	private int default_position_music = -1;
	private int random_position_music = -1;
	
	public static final int MODE_DEFAULT_ORDER = 0;
	public static final int MODE_RANDOM_ORDER = 1;
	
	public static int MODE_ORDER = MODE_DEFAULT_ORDER;
	
	private int position_item = -1;  //--> when the customer click the item of the list view on player,this variable is the item position
	
//	private boolean isPaused = false;
	private MusicServiceBroadcastReceiver broadcastReceiver = null;
//	private AudioManager audioManager = null;
	
	private int clearTrackIconUI = -2; //--> BaseAdapter
	
	/*********************************************************
	 * for singleton.
	 *********************************************************/
//	private static RhythmRockerService singletonService;
	private LocalBinder mLocalBinder;
	/********************************************************/
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.v(TAG , "onCreate()");
//		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		initMP();
		initNotificationConfig();
		initBroadcastReceiver();
		
		/*********************************************************
		 * for singleton.No need to do this,because Android has done.
		 *********************************************************/
		mLocalBinder = new LocalBinder();
//		singletonService = this;
		/************************/
	}

	@Override
	public void onDestroy()
	{
		mp.release();
		this.unregisterReceiver(broadcastReceiver);
		Log.v(TAG , "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent)
	{
		Log.v(TAG , "onRebind()");
		super.onRebind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if(intent == null)
			Log.v(TAG , "onStartCommand(),someone killed me.damn!");
		else
			Log.v(TAG , "onStartCommand()");
		
//		return super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
//		mp.release();
		Log.v(TAG , "onUnbind()");
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.v(TAG , "onBind(),so return the Object IBinder");
		Log.v(TAG, "onBind(),intent --> " + intent.toString());
//		return this.myBinder;
		return mLocalBinder;
	}

	
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		Log.v(TAG, "The Android is on Low Memory Situation.");
	}


	/**
	 * return a iBinder that references to a RhythmRockerService object.
	 * @author Savage F. Morgan
	 */
	public class LocalBinder extends Binder
	{
		public RhythmRockerService getService()
		{
//			return singletonService;
			return RhythmRockerService.this;
		}
	}
	
	/*
	 * These methods are the interface of this service
	 * and will be invoked by the main activity
	 */
	private void initMP()
	{
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);
		mp.setOnSeekCompleteListener(this);
		mp.setOnPreparedListener(this);
	}
	
	private void initNotificationConfig()
	{
		notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(this, RhythmRockerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

		notification = new Notification();
		notification.icon = drawable.ic_notification;
		notification.tickerText = tip_welcome;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(this, notification.tickerText, tip_app_title, pendingIntent);
		
//		notificationManager.notify(1, notification);
		startForeground(id_notification, notification);
	}
	
	
	private void initBroadcastReceiver()
	{
		broadcastReceiver = new MusicServiceBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		this.registerReceiver(broadcastReceiver, intentFilter);
	}
	
	private void playTrack(String file)
	{
//		Log.i(TAG, "playTrack(),postion_music --> " + String.valueOf(default_position_music));
//		Log.i(TAG, "playTrack(),currentMusicFile --> " + currentMusicFile);
//		Log.i(TAG, "playTrack(),size_order -->," + String.valueOf(size_order));
		isPlaying = true;

		switch(MODE_ORDER)
		{
			case MODE_DEFAULT_ORDER:
				if(default_position_music == size_order - 1)
				{
					isTheLastSong = true;
					Log.v(TAG, "playTrack(),MODE_DEFAULT_ORDER,default_position_music(isTheLastSong turns into true now.) --> " + String.valueOf(default_position_music));
				}
				else
					isTheLastSong = false;
				break;
			
			case MODE_RANDOM_ORDER:
				if(!isOnItemClickedInRandomMode)
				{
					if(random_position_music == size_order - 1)
					{
						isTheLastSong = true;
						Log.v(TAG, "playTrack(),MODE_RANDOM_ORDER,random_position_music(isTheLastSong turns into true now.) --> " + String.valueOf(random_position_music));
					}
					else 
						isTheLastSong = false;
				}
				break;
		}

		try
		{
			mp.reset();
			mp.setDataSource(file);
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.prepare(); //--> maybe I need to add a prepare listener for mp
//			mp.start(); //--> let the OnPreparedListener handles the mp.start(),it would be better huh.
			
			showNotification(notification_play_next_track);
			updateDurationUI();
			updateUI_TimerStart2Work();
			
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (IllegalStateException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void pauseTrack()
	{
		 Log.v(TAG, "pauseTrack()");
		 if(mp.isPlaying() == true)
		 {
			 stopUpdatingSeekerBarAndPlayingTimeUI();
			 mp.pause();
			 isPlaying = false;
//			 isPaused = true;
		 }
	}
	
	
	public void resumePlayingTrack()
	{
		Log.v(TAG, "resumePlayingTrack()");
		isPlaying = true;
		mp.start();
		updateUI_TimerStart2Work();
		sendMessage2UI(MyApp.MSG_RESUME_PLAYING_TRACK, -1, -1, null);
		
//		isPaused = false;
	}
	
	public void stopTrack()
	{
		Log.d(TAG, "stopTrack()");
		mp.stop();
	}
	
	
	public void playNextTrackManually()
	{
		if(isTheLastSong)
		{
			isTheLastSong = false;
			switch(MODE_ORDER)
			{
				case MODE_DEFAULT_ORDER:
					default_position_music = 0;
					currentMusicFile = MyApp.music_list.get(default_position_music).get("musicFileUrl").toString();
					updateUIListView(default_position_music);
					break;
				
				/**
				 *  random again and start to play the new random,index 0;
				 */
				case MODE_RANDOM_ORDER:
					randomTrackPlayOrder(DEFAULT_TRACK_PLAY_ORDER);
					random_position_music = 0;
					currentMusicFile = MyApp.music_list.get(RANDOM_TRACK_PLAY_ORDER[random_position_music]).get("musicFileUrl").toString();
					updateUIListView(RANDOM_TRACK_PLAY_ORDER[random_position_music]);
					Log.v(TAG, "playNextTrack(),MODE_RANDOM_ORDER,last song,so i need to random again!");	
					break;
			}
		}
		else if(!isTheLastSong)
		{
			switch(MODE_ORDER)
			{
				case MODE_DEFAULT_ORDER:
					default_position_music++;
					currentMusicFile = MyApp.music_list.get(default_position_music).get("musicFileUrl").toString();
					updateUIListView(default_position_music);
					Log.v(TAG, "playNextTrack(),MODE_DEFAULT_ORDER,play next track");	
					break;
				
				case MODE_RANDOM_ORDER:
					random_position_music++;
					currentMusicFile = MyApp.music_list.get(RANDOM_TRACK_PLAY_ORDER[random_position_music]).get("musicFileUrl").toString();
					updateUIListView(RANDOM_TRACK_PLAY_ORDER[random_position_music]);
					Log.v(TAG, "playNextTrack(),MODE_RANDOM_ORDER,play next random track");	
					break;
			}
		}
		playTrack(currentMusicFile);
	}
	
	
	private void playNextTrackOnCompletion()
	{
		//--> last song played
		if(isTheLastSong)
		{
			isTheLastSong = false;
			switch(MODE_ORDER)
			{
				case MODE_DEFAULT_ORDER:
					default_position_music = -1;
					break;
				
				case MODE_RANDOM_ORDER:
					random_position_music = -1;
					break;
			}
			sendMessage2UI(MyApp.MSG_LAST_TRACK_DONE, -1, -1, null);
//			mp.reset(); //--> no need to reset mp.if do so,it will case an error,and OnCompletionListener will be called.
			isPlaying = false;
			showNotification(notification_play_completely);
			updateUIListView(clearTrackIconUI);
		}
		else if(!isTheLastSong)
		{
			switch(MODE_ORDER)
			{
				case MODE_DEFAULT_ORDER:
					default_position_music++;
					currentMusicFile = MyApp.music_list.get(default_position_music).get("musicFileUrl").toString();
					updateUIListView(default_position_music);
					break;
				
				case MODE_RANDOM_ORDER:
					random_position_music++;
					currentMusicFile = MyApp.music_list.get(RANDOM_TRACK_PLAY_ORDER[random_position_music]).get("musicFileUrl").toString();
					updateUIListView(RANDOM_TRACK_PLAY_ORDER[random_position_music]);
					break;
			}
			playTrack(currentMusicFile);
		}
	}
	
	
	public void playPrevTrackManually()
	{
//		Log.i(TAG, "playPrevTrack()<at the beginning>,postion_music --> " + String.valueOf(default_position_music));
		int temp_position = -2;
		
		if(MODE_ORDER == MODE_DEFAULT_ORDER)
			temp_position = default_position_music;
		else if(MODE_ORDER == MODE_RANDOM_ORDER)
			temp_position = random_position_music;
		
		if(temp_position == -1)
			temp_position++;
		else if(temp_position == 0)
			temp_position = 0;
		else if(temp_position <= size_order - 1)
			temp_position--;
		
		if(MODE_ORDER == MODE_DEFAULT_ORDER)
		{
			default_position_music = temp_position;
			updateUIListView(default_position_music);
			currentMusicFile = MyApp.music_list.get(default_position_music).get("musicFileUrl").toString();
		}
		else if(MODE_ORDER == MODE_RANDOM_ORDER)
		{
			random_position_music = temp_position;
			currentMusicFile = MyApp.music_list.get(RANDOM_TRACK_PLAY_ORDER[random_position_music]).get("musicFileUrl").toString();
			updateUIListView(RANDOM_TRACK_PLAY_ORDER[random_position_music]);
		}
	
		playTrack(currentMusicFile);
	}
	
	
	public void playTrackOnItemClickFromUI(int position_item)
	{
		isOnItemClickedInRandomMode = true;
		this.position_item = position_item;
		switch(MODE_ORDER)
		{
			case MODE_DEFAULT_ORDER:
				default_position_music = position_item;
				break;
			
			//--> in random mode,the item click event doesn't increase the random_position_music,so does nothing.
			case MODE_RANDOM_ORDER:
//				if(random_position_music == -1)
//				{
//					random_position_music = 0;
//				}
				break;
		}
		currentMusicFile = MyApp.music_list.get(position_item).get("musicFileUrl").toString();
		playTrack(currentMusicFile);
	}
	
	
	private void showNotification(int notificationType)
	{
		notification = new Notification();
		notification.icon = drawable.ic_notification;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		switch(notificationType)
		{
			case notification_play_next_track:
				
				String track_index = null;
				String track_title = null;
				String track_artist = null;
				//--> if the customer clicks the ListView in RANDOM mode
				if(isOnItemClickedInRandomMode)
				{
					isOnItemClickedInRandomMode = false;
					track_index = MyApp.music_list.get(position_item).get("musicTrackIndex").toString();
					track_title = MyApp.music_list.get(position_item).get("musicTitle").toString();
					track_artist = MyApp.music_list.get(position_item).get("musicArtist").toString();
					position_item = 0;
				}
				else //--> from onCompletion();
				{
					switch(MODE_ORDER)
					{
						case MODE_DEFAULT_ORDER:
							track_index = MyApp.music_list.get(default_position_music).get("musicTrackIndex").toString();
							track_title = MyApp.music_list.get(default_position_music).get("musicTitle").toString();
							track_artist = MyApp.music_list.get(default_position_music).get("musicArtist").toString();
							break;
							
						case MODE_RANDOM_ORDER:
							track_index = MyApp.music_list.get(RANDOM_TRACK_PLAY_ORDER[random_position_music]).get("musicTrackIndex").toString();
							track_title = MyApp.music_list.get(RANDOM_TRACK_PLAY_ORDER[random_position_music]).get("musicTitle").toString();
							track_artist = MyApp.music_list.get(RANDOM_TRACK_PLAY_ORDER[random_position_music]).get("musicArtist").toString();
							break;
					}
				}

				if(last_tickerText == null)
				{
					last_tickerText = track_index + track_title + " - " + track_artist;
					notification.tickerText = last_tickerText;
				}
				else
				{
					current_tickerText = track_index + track_title + " - " + track_artist;;
					if(last_tickerText.equalsIgnoreCase(current_tickerText))
					{
						if(times_click == 0)
						{
							times_click++;
							current_tickerText = track_index + track_title + " - " + track_artist + " ";
							notification.tickerText = current_tickerText;
						}
						else if(times_click == 1)
						{
							times_click++;
							current_tickerText = track_index + track_title + " - " + track_artist;
							notification.tickerText = current_tickerText;
						}
						else if(times_click > 1 && times_click % 2 == 0)
						{
							times_click++;
							current_tickerText = track_index + track_title + " - " + track_artist + " ";
							notification.tickerText = current_tickerText;
						}
						else
						{
							times_click++;
							current_tickerText = track_index + track_title + " - " + track_artist;
							notification.tickerText = current_tickerText;
						}
					}
					else
					{
						times_click = 0;
						current_tickerText = track_index + track_title + " - " + track_artist;
						notification.tickerText = current_tickerText;
					}
					last_tickerText = track_index + track_title + " - " + track_artist;
				}
				break;
				
			case notification_play_completely:
				notification.tickerText = tip_play_completely;
				break;
				
			case notification_say_goodbye:
				notification.tickerText = tip_say_goodbye;
				break;
		}
		notification.setLatestEventInfo(this, notification.tickerText, tip_app_title, pendingIntent);
//		notificationManager.notify(id_notification, notification);
		startForeground(id_notification, notification);
		
	}
	
	
	
	public void shuffleOn()
	{
		MODE_ORDER = MODE_RANDOM_ORDER;
		randomTrackPlayOrder(DEFAULT_TRACK_PLAY_ORDER);
//		random_position_music = RANDOM_TRACK_PLAY_ORDER[0];
		random_position_music = -1;
		
		isTheLastSong = false;

	}
	
	public void shuffleOff()
	{
		MODE_ORDER = MODE_DEFAULT_ORDER;
//		default_position_music = DEFAULT_TRACK_PLAY_ORDER[0];
		default_position_music = -1;
		
		isTheLastSong = false;
	}
	
	public void seekTo(int progress)
	{
		if(progress == 0)
		{
			mp.seekTo(0);
		}
		else if(progress > 0)
		{
			int seek_in_msec = progress * 1000;
			Log.v(TAG, "seekTo,seek_in_msec is " + String.valueOf(seek_in_msec));
			if(seek_in_msec > mp.getDuration())
			{
				mp.seekTo(mp.getDuration());
			}
			else
			{
				mp.seekTo(seek_in_msec);
			}
		}
//		if(!mp.isPlaying()) resumePlayingTrack();
	}
	
	
	private void updateDurationUI()
	{
		String duration = null;
		duration_in_seconds = mp.getDuration() / 1000;
		Log.v(TAG, "updateDurationUI,duration in seconds is " + String.valueOf(duration_in_seconds));
		if(duration_in_seconds >= 60)
		{
			int minutes = duration_in_seconds / 60;
			int seconds = duration_in_seconds % 60;
			if(seconds < 10)
			{
				duration = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
			}
			else
			{
				duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
			}
				
		}
		else if(duration_in_seconds >= 10)
		{
			duration = "0:" + String.valueOf(duration_in_seconds);
		}
		else if (duration_in_seconds < 10)
		{
			duration = "0:0" + String.valueOf(duration_in_seconds);
		}
			
		sendMessage2UI(MyApp.MSG_PLAYING_TRACK, duration_in_seconds, -1, duration);
	}
	
	private void updateSeekerBarAndPlayingTimeUI()
	{
		//--> progress in second
		String str_playing_time = null;
		try
		{
			int current_playing_time_in_second = mp.getCurrentPosition() / 1000;
			if(current_playing_time_in_second >= 60)
			{
				int minutes = current_playing_time_in_second / 60;
				int seconds = current_playing_time_in_second % 60;
				if(seconds < 10)
				{
					str_playing_time = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
				}
				else
				{
					str_playing_time = String.valueOf(minutes) + ":" + String.valueOf(seconds);
				}
				
			}
			else if(current_playing_time_in_second < 10)
			{
				str_playing_time = "0:0" + String.valueOf(current_playing_time_in_second);
			}
			else
			{
				str_playing_time = "0" + ":" + String.valueOf(current_playing_time_in_second);
			}
			sendMessage2UI(MyApp.MSG_SEEKER_BAR_MOVING, -1, current_playing_time_in_second, str_playing_time);
		}
		catch(java.lang.IllegalStateException e)
		{
			e.printStackTrace();
			if(timerTask != null)
				timerTask.cancel();
			timer.cancel();
		}
	}
	
	
	private void stopUpdatingSeekerBarAndPlayingTimeUI()
	{
		Log.v(TAG, "stopUpdatingSeekerBarAndPlayingTimeUI");
		if(timerTask != null)
		{
			timerTask.cancel();
			timer.purge();
//			timer.cancel();
//			timerTask = null;
//			timer = null;
		}
	}
	
	private void updateUI_TimerStart2Work()
	{
//		timer = new Timer();
		timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				updateSeekerBarAndPlayingTimeUI();
			}
			
		};
//		timer.scheduleAtFixedRate(timerTask, 0, 1000);
		timer.schedule(timerTask, 0, 1000);
	}
	
	
	public void sayGoodbye()
	{
		showNotification(notification_say_goodbye);
		notificationManager.cancelAll();
	}
	
	
	/**
	 * @param what --> the what is necessary
	 * @param arg1 --> set -1; if u don't need
	 * @param arg2 --> set -1; if u don't need
	 * @param obj  --> set null; if u don't need
	 */
	private void sendMessage2UI(int what, int arg1, int arg2, Object obj)
	{
		this.msg = Message.obtain();
		this.msg.what = what;
		if(arg1 != -1)
		{
			this.msg.arg1 = arg1;
		}
		if(arg2 != -1)
		{
			this.msg.arg2 = arg2;
		}
		if(obj != null)
		{
			this.msg.obj = obj;
		}
		RhythmRockerActivity.mMusicHandler.sendMessage(msg);
	}
	
	
	public void initDefaultTrackPlayOrder()
	{
		size_order = MyApp.music_list.size();
		this.DEFAULT_TRACK_PLAY_ORDER = new int[size_order];
		
		for(int i = 0; i < size_order; i++)
		{
			DEFAULT_TRACK_PLAY_ORDER[i] = i;
		}
	}
	
	
	private void randomTrackPlayOrder(int[] default_order_as_seed)
	{
		int[] temp_order = default_order_as_seed.clone();
		if (RANDOM_TRACK_PLAY_ORDER == null)
		{
			RANDOM_TRACK_PLAY_ORDER = new int[size_order];
		}
		Random random = new Random();   
		for (int i = 0; i < size_order; i++)
		{   
		  int r = random.nextInt(size_order - i); 
//		  System.out.println("the random int r is --> " + r);
		  RANDOM_TRACK_PLAY_ORDER[i] = temp_order[r];   
		  temp_order[r] = temp_order[size_order - 1 - i];   
		}   
//		System.out.println("RANDOM_TRACK_PLAY_ORDER:" + Arrays.toString(RANDOM_TRACK_PLAY_ORDER)); 
//		System.out.println("DEFAULT_TRACK_PLAY_ORDER:" + Arrays.toString(DEFAULT_TRACK_PLAY_ORDER)); 
//		System.out.println("temp_order:" + Arrays.toString(temp_order)); 
	}
	
	private void updateUIListView(int position)
	{
		sendMessage2UI(MyApp.MSG_UI_LISTVIEW_UPDATE, position , -1 , null);
	}
	
//	private void muteMP()
//	{
//		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//	}
//	
//	private void unmuteMP()
//	{
//		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//	}
	
	
	
	@Override
	public void onCompletion(MediaPlayer mp)
	{
		Log.v(TAG, "onCompletion()");
		stopUpdatingSeekerBarAndPlayingTimeUI();
		playNextTrackOnCompletion();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		/**
		 * Returns
		 *	True if the method handled the error, false if it didn't. Returning false, 
		 *  or not having an OnErrorListener at all, will cause the OnCompletionListener to be called. 
		 */
		Log.v(TAG, "onError()");
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp)
	{
		if(!mp.isPlaying()) resumePlayingTrack();
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		mp.start();
	}
	
	
	/**
	 * @author Savage F. Morgan
	 * receive Intent.ACTION_HEADSET_PLUG, being calling, being messaging
	 */
	private class MusicServiceBroadcastReceiver extends BroadcastReceiver
	{
		private final int plugged = 1;
		private final int unplugged= 0;
		
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG))
			{
				Log.v(TAG, "onReceive() --> ACTION_HEADSET_PLUG");
				if(intent.getIntExtra("state", -1) == plugged)
				{
					Log.v(TAG, "state 1 --> plugged");
//					if(SManMusicService.this.isPaused)
						
				}
				else if(intent.getIntExtra("state", -1) == unplugged)
				{
					Log.v(TAG, "state 0 --> unplugged");
					
					if(RhythmRockerService.this.mp.isPlaying())
					{
//						SManMusicService.this.muteMP();
						RhythmRockerService.this.pauseTrack();
						RhythmRockerService.this.sendMessage2UI(MyApp.MSG_HEADSET_UNPLUGGED, -1, -1, null);
//						SManMusicService.this.unmuteMP();
//						SManMusicService.this.isPaused = true;
					}
				}
				else
				{
					Log.v(TAG, "state default value --> impossible!");
				}
			}
		}
	} //--> End of class MusicServiceBroadcastReceiver
} //--> End of class MusicService
