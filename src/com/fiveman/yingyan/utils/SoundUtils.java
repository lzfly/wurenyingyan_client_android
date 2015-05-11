package com.fiveman.yingyan.utils;

import com.fiveman.yingyan.R;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;

public class SoundUtils {

	private static boolean S_INITED = false;
	
	private static Ringtone S_RIGNTONE;
	
	private static int S_SOUND_COUNT = 1;
	private static SoundPool S_SOUND_POOL;
	
	public static void init()
	{
		if (!S_INITED)
		{
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			S_RIGNTONE = RingtoneManager.getRingtone(GlobalContext.getInstance(), notification);
	
			S_SOUND_POOL = new SoundPool(3, AudioManager.STREAM_MUSIC, 3);
			S_SOUND_POOL.load(GlobalContext.getInstance(), R.raw.fandaobaojing, 1);
			
			S_INITED = true;
		}
	}
	
	public static void playRingtone()
	{
		if (S_RIGNTONE != null)
		{
			S_RIGNTONE.play();
		}
	}
	
	public static void playSoundPool()
	{
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				if (S_SOUND_POOL != null)
				{
					for (int i = 1; i <= S_SOUND_COUNT; i++)
					{
						S_SOUND_POOL.play(i, 1, 1, 0, 3, 1);
					}
				}
			}
			
		});
		t.start();

	}
	
	public static void playSoundPool(int soundIndex)
	{
		if (S_SOUND_POOL != null)
		{
			if (soundIndex > 0 && soundIndex <= S_SOUND_COUNT)
			{
				S_SOUND_POOL.play(soundIndex, 1, 1, 0, 0, 1);
			}
		}
	}
	
	public static void play()
	{
		playRingtone();
		playSoundPool();
	}
	
	public static void play(int soundIndex)
	{
		playRingtone();
		playSoundPool(soundIndex);
	}
	
}
