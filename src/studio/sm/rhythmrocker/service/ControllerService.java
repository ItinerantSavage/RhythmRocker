package studio.sm.rhythmrocker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ControllerService extends Service
{
	private static final String TAG = "ControllerService";
	private LocalBinderController mLocalBinderController;
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mLocalBinderController = new LocalBinderController();
		Log.v(TAG, "onCreate()");
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.v(TAG, "onBind()");
		return mLocalBinderController;
	}

	
	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.v(TAG, "onBind()");
		return super.onUnbind(intent);
	}


	/**
	 * return a iBinder that references to a RhythmRockerService object.
	 * @author Savage F. Morgan
	 */
	public class LocalBinderController extends Binder
	{
		public ControllerService getService()
		{
			return ControllerService.this;
		}
	}
}
