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

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yohpapa.overlaymusicplayer.R;
import com.yohpapa.overlaymusicplayer.activity.MainActivity;
import com.yohpapa.tools.MetaDataRetriever;

/**
 * @author YohPapa
 */
public class OverlayViewManager {
	
	private Context _context = null;
	
	private View _panelView = null;
	private View _openView = null;
	private View _frontView = null;
	
	private WindowManager.LayoutParams _panelParams = null;
	private WindowManager.LayoutParams _openParams = null;
	
	private WindowManager _windowManager = null;

	public OverlayViewManager(Context context) {
		_context = context;
		
		_windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		
		_panelParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSLUCENT);
		_panelParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		
		_openParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSLUCENT);
		_openParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final int[] panelButtonIds = new int[] {
			R.id.image_artwork,
			R.id.button_hide,
			R.id.button_track_down,
			R.id.button_play_or_pause,
			R.id.button_track_up,
			R.id.button_stop,
		};
		final View.OnClickListener[] panelListeners = new View.OnClickListener[] {
			_onArtworkClickListener,
			_onHideClickListener,
			_onTrackDownClickListener,
			_onPlayPauseClickListener,
			_onTrackUpClickListener,
			_onStopClickListener,
		};
		_panelView = inflater.inflate(R.layout.overlay_play_panel, null);
		setupButtonListener(_panelView, panelButtonIds, panelListeners);
		
		setupPlayPauseButton(_panelView, false);
		
		final int[] openButtonIds = new int[] {
			R.id.button_open,
		};
		final View.OnClickListener[] openlListeners = new View.OnClickListener[] {
			_onOpenClickListener,
		};
		_openView = inflater.inflate(R.layout.overlay_open_button, null);
		setupButtonListener(_openView, openButtonIds, openlListeners);
	}
	
	private void setupButtonListener(View parent, int[] ids, View.OnClickListener[] listeners) {
		
		for(int i = 0; i < ids.length; i ++) {
			View button = parent.findViewById(ids[i]);
			button.setOnClickListener(listeners[i]);
		}
	}
	
	private void setupPlayPauseButton(View parent, boolean isPlaying) {
		Button button = (Button)_panelView.findViewById(R.id.button_play_or_pause);
		button.setTag(R.id.tag_play_state, isPlaying);
		if(isPlaying) {
			button.setBackgroundResource(android.R.drawable.ic_media_pause);
		} else {
			button.setBackgroundResource(android.R.drawable.ic_media_play);
		}
	}
	
	private final View.OnClickListener _onPlayPauseClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Button button = (Button)_panelView.findViewById(R.id.button_play_or_pause);
			boolean isPlaying = (Boolean)button.getTag(R.id.tag_play_state);
			
			Intent intent = new Intent(_context, OverlayMusicPlayerService.class);
			if(isPlaying) {
				intent.setAction(OverlayMusicPlayerService.ACTION_PAUSE);
			} else {
				intent.setAction(OverlayMusicPlayerService.ACTION_PLAY);
			}
			_context.startService(intent);
		}
	};
	
	private final View.OnClickListener _onTrackDownClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(_context, OverlayMusicPlayerService.class);
			intent.setAction(OverlayMusicPlayerService.ACTION_TRACKDOWN);
			_context.startService(intent);
		}
	};
	
	private final View.OnClickListener _onTrackUpClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(_context, OverlayMusicPlayerService.class);
			intent.setAction(OverlayMusicPlayerService.ACTION_TRACKUP);
			_context.startService(intent);
		}
	};
	
	private final View.OnClickListener _onStopClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(_context, OverlayMusicPlayerService.class);
			intent.setAction(OverlayMusicPlayerService.ACTION_STOP);
			_context.startService(intent);
		}
	};
	
	private final View.OnClickListener _onHideClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showView(_openView, _openParams);
		}
	};
	
	private final View.OnClickListener _onArtworkClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(_context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			_context.startActivity(intent);
			
			showView(_openView, _openParams);
		}
	};
	
	private final View.OnClickListener _onOpenClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showView(_panelView, _openParams);
		}
	};

	public void show() {
		showView(_panelView, _openParams);
	}
	
	private void showView(View view, WindowManager.LayoutParams params) {
		if(_frontView == view) {
			return;
		}
		
		hide();
		_windowManager.addView(view, params);
		_frontView = view;
	}

	public void hide() {
		if(_frontView == null) {
			return;
		}
		
		_windowManager.removeView(_frontView);
		_frontView = null;
	}
	
	public void setMetaInformation(MetaDataRetriever.MetaData meta) {
		
		if(meta == null) {
			return;
		}
		
		ImageButton artwork = (ImageButton)_panelView.findViewById(R.id.image_artwork);
		if(meta.artwork != null) {
			artwork.setImageBitmap(meta.artwork);
		} else {
			artwork.setImageResource(R.drawable.ic_launcher);
		}
		
		TextView text = (TextView)_panelView.findViewById(R.id.text_title);
		text.setText(meta.title);
		
		text = (TextView)_panelView.findViewById(R.id.text_artist_name);
		text.setText(meta.artistName);
	}
	
	public void setPlayState(boolean isPlaying) {
		setupPlayPauseButton(_panelView, isPlaying);
	}
}
