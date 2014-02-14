/**
 * Copyright 2014 Kensuke Nakai<kemumaki.kemuo@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yohpapa.overlaymusicplayer.service;

import android.content.Intent;
import android.util.Log;

import com.yohpapa.tools.AlbumSongIdRetriever;
import com.yohpapa.tools.MetaDataRetriever;
import com.yohpapa.tools.MusicPlaybackService;
import com.yohpapa.tools.AlbumSongIdRetriever.AlbumSongsInfo;

public class OverlayMusicPlayerService extends MusicPlaybackService {

	private static final String TAG = OverlayMusicPlayerService.class.getSimpleName();
	private static final String BASE_URI = OverlayMusicPlayerService.class.getName() + ".";
	
	public static final String ACTION_SELECT_ALBUM = BASE_URI + "ACTION_SELECT_ALBUM";
	// TODO: SELECT_GENRE, SELECT_ARTIST, SELECT_PLAYLIST, SELECT_SONG
	public static final String PRM_ALBUM_ID = BASE_URI + "PRM_ALBUM_ID";
	public static final String PRM_ALBUM_NAME = BASE_URI + "PRM_ALBUM_NAME";
	public static final String PRM_NEED_TO_PLAY_AFTER_SELECT = BASE_URI + "PRM_NEED_TO_PLAY_AFTER_SELECT";
	
	public static final String ACTION_PLAY = BASE_URI + "ACTION_PLAY";
	public static final String ACTION_PAUSE = BASE_URI + "ACTION_PAUSE";
	public static final String ACTION_STOP = BASE_URI + "ACTION_STOP";
	public static final String ACTION_TRACKUP = BASE_URI + "ACTION_TRACKUP";
	public static final String ACTION_TRACKDOWN = BASE_URI + "ACTION_TRACKDOWN";
	
	public static final String ACTION_SEEK = BASE_URI + "ACTION_SEEK";
	public static final String PRM_SEEK_TIME = BASE_URI + "PRM_SEEK_TIME";
	
	private OverlayViewManager _overlayManager = null;
	private NotificationViewManager _notificationManager = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		_overlayManager = new OverlayViewManager(this);
		_notificationManager = new NotificationViewManager(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		_overlayManager.hide();
		_notificationManager.hide();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null) {
			return START_REDELIVER_INTENT;
		}
		
		String action = intent.getAction();
		if(ACTION_SELECT_ALBUM.equals(action)) {
			onActionSelectAlbum(intent);
		} else if(ACTION_PLAY.equals(action)) {
			onActionPlay(intent);
		} else if(ACTION_PAUSE.equals(action)) {
			onActionPause(intent);
		} else if(ACTION_STOP.equals(action)) {
			onActionStop(intent);
		} else if(ACTION_TRACKUP.equals(action)) {
			onActionTrackUp(intent);
		} else if(ACTION_TRACKDOWN.equals(action)) {
			onActionTrackDown(intent);
		} else if(ACTION_SEEK.equals(action)) {
			onActionSeek(intent);
		} else {
			Log.e(TAG, "Unknown request: " + action);
		}
		
		return START_REDELIVER_INTENT;
	}
	
	private void onActionSelectAlbum(Intent intent) {
		long albumId = intent.getLongExtra(PRM_ALBUM_ID, -1L);
		String albumName = intent.getStringExtra(PRM_ALBUM_NAME);
		if(albumId == -1L || albumName == null) {
			return;
		}
		final boolean needToPlay = intent.getBooleanExtra(PRM_NEED_TO_PLAY_AFTER_SELECT, false);
		
		AlbumSongIdRetriever task = new AlbumSongIdRetriever(this, albumId, new AlbumSongIdRetriever.OnFinishRetrievingInfo() {
			@Override
			public void onFinishRetrieving(AlbumSongsInfo info) {
				selectTrack(0, info.songIds);
				
				if(needToPlay) {
					playTrack();
					_overlayManager.show();
				}
			}
		});
		task.execute();
	}
	
	private void onActionPlay(Intent intent) {
		playTrack();
	}
	
	private void onActionPause(Intent intent) {
		pauseTrack();
	}
	
	private void onActionStop(Intent intent) {
		stopTrack();
		_overlayManager.hide();
	}
	
	private void onActionTrackUp(Intent intent) {
		nextTrack();
	}
	
	private void onActionTrackDown(Intent intent) {
		prevTrack();
	}
	
	private void onActionSeek(Intent intent) {
		int time = intent.getIntExtra(PRM_SEEK_TIME, -1);
		if(time == -1) {
			return;
		}
		
		seekTrack(time);
	}
	
	@Override
	protected void onTrackChanged() {
		MetaDataRetriever.MetaData meta = getCurrentTrackInfo();
		
		_overlayManager.setMetaInformation(meta);
		_notificationManager.updateMetaData(meta);
	}
	
	@Override
	protected void onPlayStateChanged(int playState) {
		boolean isPlaying = playState == PLAY_STATE_PLAYING;
		
		_overlayManager.setPlayState(isPlaying);
		_notificationManager.updatePlayState(isPlaying);
	}
}
