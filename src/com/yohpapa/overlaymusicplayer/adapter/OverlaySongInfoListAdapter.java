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

package com.yohpapa.overlaymusicplayer.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yohpapa.overlaymusicplayer.R;
import com.yohpapa.overlaymusicplayer.service.task.SongInfoList;
import com.yohpapa.tools.PrefUtils;

/**
 * @author YohPapa
 *
 */
public class OverlaySongInfoListAdapter extends BaseAdapter {
	
	public interface OnClickListener {
		void onClick(int position);
	}
	
	private Context _context = null;
	private SongInfoList _songInfoList = null;
	private OnClickListener _listener = null;
	
	private final int TEXT_COLOR_DARK;
	private final int TEXT_COLOR_LIGHT;
	
	public OverlaySongInfoListAdapter(Context context, SongInfoList list, OnClickListener listener) {
		_context = context;
		_songInfoList = list;
		_listener = listener;
		
		Resources res = context.getResources();
		TEXT_COLOR_DARK = res.getColor(R.color.overlay_title_dark);
		TEXT_COLOR_LIGHT = res.getColor(R.color.overlay_title_light);
	}

	@Override
	public int getCount() {
		return _songInfoList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup group) {
		
		if(view == null) {
			LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.overlay_list_song_item, null);
		}
		
		int color;
		int colorMode = PrefUtils.getInt(_context, R.string.pref_foreground_color, 0);
		if(colorMode == 0) {
			color = TEXT_COLOR_DARK;
		} else {
			color = TEXT_COLOR_LIGHT;
		}
		
		TextView index = (TextView)view.findViewById(R.id.text_track_index);
		index.setText(String.valueOf(position + 1));
		index.setTextColor(color);
		
		TextView title = (TextView)view.findViewById(R.id.text_title);
		
		if(_songInfoList.getCount() <= position) {
			title.setText(null);
			return view;
		}
		
		title.setText(_songInfoList.titles[position]);
		title.setTextColor(color);
		
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(_listener != null) {
					_listener.onClick(position);
				}
			}
		});
		
		return view;
	}
	
	public boolean isChanged(SongInfoList list) {
		if(list == null) {
			return false;
		}
		
		if(list.getCount() != _songInfoList.getCount()) {
			return true;
		}
		
		long[] newSongIds = list.songIds;
		String[] newTitles = list.titles;
		if(newSongIds == null || newTitles == null) {
			return false;
		}
		
		long[] prevSongIds = _songInfoList.songIds;
		String[] prevTitles = list.titles;
		
		for(int i = 0; i < list.getCount(); i ++) {
			if(prevSongIds[i] != newSongIds[i]) {
				return false;
			}
			
			if(prevTitles[i] == null) {
				if(newTitles[i] != null) {
					return true;
				} else {
					continue;
				}
			}
			
			if(!prevTitles[i].equals(newTitles[i])) {
				return true;
			}
		}
		
		return false;
	}
}
