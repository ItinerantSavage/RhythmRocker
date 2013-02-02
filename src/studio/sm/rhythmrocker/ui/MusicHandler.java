package studio.sm.rhythmrocker.ui;

import java.lang.ref.WeakReference;

import studio.sm.rhythmrocker.global.MyApp;
import studio.sm.rhythmrocker.service.RhythmRockerService;
import studio.sm.rhythmrocker.ui.RhythmRockerActivity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MusicHandler extends Handler
{
	private final static String TAG = "MusicHandler";

	private final WeakReference<RhythmRockerActivity> mActivity;
	private RhythmRockerActivity mRhythmRocker;
	
	//--> constructor
	public MusicHandler(RhythmRockerActivity activity)
	{
		mActivity = new WeakReference<RhythmRockerActivity>(activity);
	}

	@Override
	public void handleMessage(Message msg)
	{
//		super.handleMessage(msg);
		mRhythmRocker = mActivity.get();
//		RhythmRockerActivity mRhythmRocker = mActivity.get();
		switch(msg.what)
		{
		case MyApp.MSG_GOT_ALL_MUSIC:
			Log.i(TAG, "--> MSG_GOT_ALL_MUSIC");
			mRhythmRocker.updateListViewMusic();
			if(RhythmRockerService.isPlaying == false)
			{
				mRhythmRocker.btn_play_pause.setText(RhythmRockerActivity.str_play);
			}
			else
			{
				mRhythmRocker.btn_play_pause.setText(RhythmRockerActivity.str_pause);
			}
			mRhythmRocker.boundMusicService.initDefaultTrackPlayOrder();
			break;
			
		case MyApp.MSG_NO_MUSIC:
			Log.v(TAG, "--> MSG_NO_MUSIC");
			mRhythmRocker.activeUI(false);
			break;
			
		case MyApp.MSG_LAST_TRACK_DONE:
			Log.v(TAG, "--> MSG_LAST_TRACK_DONE");
			mRhythmRocker.btn_play_pause.setText(RhythmRockerActivity.str_play);
			mRhythmRocker.activeUI(false);
			break;
			
		case MyApp.MSG_PLAYING_TRACK:
			Log.v(TAG, "--> MSG_PLAYING_TRACK");
			mRhythmRocker.str_duration = msg.obj.toString();
			mRhythmRocker.int_duration = msg.arg1;
			mRhythmRocker.seekBar_music.setMax(mRhythmRocker.int_duration);
			mRhythmRocker.textView_music_duration.setText(mRhythmRocker.str_duration);
			mRhythmRocker.btn_play_pause.setText(RhythmRockerActivity.str_pause);
//			mRhythmRocker.activeUI(true); //--> ensure that when the mp is playing,the UIs must show
			break;
			
		case MyApp.MSG_SEEKER_BAR_MOVING:
			mRhythmRocker.str_playing_time = msg.obj.toString();
			mRhythmRocker.int_seekBar_progress = msg.arg2;
			mRhythmRocker.textView_music_playing_time.setText(mRhythmRocker.str_playing_time);
			mRhythmRocker.seekBar_music.setProgress(mRhythmRocker.int_seekBar_progress);
			break;
			
		case MyApp.MSG_RESUME_PLAYING_TRACK:
			mRhythmRocker.btn_play_pause.setText(RhythmRockerActivity.str_pause);
			break;
			
		case MyApp.MSG_HEADSET_UNPLUGGED:
			mRhythmRocker.btn_play_pause.setText(RhythmRockerActivity.str_play);
			break;
			
		case MyApp.MSG_UI_LISTVIEW_UPDATE:
			Log.v(TAG, "--> MSG_UI_LISTVIEW_UPDATE");
			mRhythmRocker.mMusicAdapter.setSelectedItem(msg.arg1);
			mRhythmRocker.mMusicAdapter.notifyDataSetInvalidated();
			MyApp.position_scroll_to = msg.arg1 - (MyApp.visibleItemCount / 2);
			mRhythmRocker.listView_music.setSelection(MyApp.position_scroll_to);
//			mRhythmRocker.listView_music.setSelection(msg.arg1 - (MyApp.visibleItemCount / 2));
//			mRhythmRocker.latest_item_postion = msg.arg1;
			break;
		}
	}
}
