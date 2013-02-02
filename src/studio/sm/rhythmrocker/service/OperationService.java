package studio.sm.rhythmrocker.service;

import java.util.ArrayList;
import java.util.HashMap;

import studio.sm.rhythmrocker.global.MyApp;
import studio.sm.rhythmrocker.ui.RhythmRockerActivity;
import studio.sm.rhythmrocker.ui.R;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

public class OperationService extends IntentService
{
	private static final String TAG = "OperationService";
	private ArrayList<HashMap<String,Object>> arrayList_music = null;
	
	private void getAllAudioFiles()
	{
		arrayList_music = new ArrayList<HashMap<String,Object>>();
		Cursor cursor =getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  
		if(cursor.moveToFirst())
		{
			int track_index = 1;
			
			String tilte;
			String album;   
			String artist;  
			String url;
			String file_name;  
			int duration;
			
			while (!cursor.isAfterLast())
			{
			   Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			   // --> if the file size >= 2MB and < 25MB,we think the file is a general track file
			   if(size >= 1024 * 2000 && size < 1024 * 25000)
			   {  
				   tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));   
				   album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));   
				   artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));   
				   url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				   file_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));   
				   duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)); 
				   
//				   String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));   
//				   String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));   
//				   String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));   
//				   String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
//				   String file_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));   
//				   int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)); 
				   
				   HashMap<String,Object> map = new HashMap<String,Object>(); 
//				   map.put("musicId", id);
				   map.put("musicTitle", tilte);
				   map.put("musicAlbum", album);
				   map.put("musicArtist", artist);
				   map.put("musicFileUrl",url);
				   map.put("music_file_name", file_name);
				   map.put("musicDuration", duration);
				   //--> put index in to the map
				   map.put("musicTrackIndex", String.format(getString(R.string.track_index_pattern), track_index));
//				   map.put("musicTrackIndex", String.valueOf(track_index) + ". ");
				   
				   arrayList_music.add(map);
				   
				   track_index++;
			   }
			   cursor.moveToNext(); 
			}
		}
		Log.i(TAG, "arrayList_music.size() is : " + String.valueOf(arrayList_music.size()));
		cursor.close();
	}
	
	//--> default constructor
	public OperationService()
	{
		super(TAG);
//		super("OperationService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		getAllAudioFiles();
		Message msg = Message.obtain();
		if(arrayList_music.size() < 1)
		{
			arrayList_music = null;
			msg.what = MyApp.MSG_NO_MUSIC;
		}
		else
		{
//			System.out.println(arrayList_music.toString());
//			RhythmRockerService.music_list = arrayList_music;
			MyApp.music_list = arrayList_music;
			msg.what = MyApp.MSG_GOT_ALL_MUSIC;
		}
		RhythmRockerActivity.mMusicHandler.sendMessage(msg);
	}
}
