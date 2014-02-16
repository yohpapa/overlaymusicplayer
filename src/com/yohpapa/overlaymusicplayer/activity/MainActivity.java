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

package com.yohpapa.overlaymusicplayer.activity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.yohpapa.overlaymusicplayer.R;
import com.yohpapa.overlaymusicplayer.fragment.AlbumListFragment;
import com.yohpapa.overlaymusicplayer.fragment.ArtistListFragment;
import com.yohpapa.overlaymusicplayer.fragment.GenreListFragment;
import com.yohpapa.overlaymusicplayer.fragment.PlayListFragment;
import com.yohpapa.overlaymusicplayer.fragment.SongListFragment;
import com.yohpapa.tools.PrefUtils;

public class MainActivity extends Activity {

	private Fragment[] fragments = new Fragment[] {
		GenreListFragment.getInstance(),
		ArtistListFragment.getInstance(),
		AlbumListFragment.getInstance(),
		PlayListFragment.getInstance(),
		SongListFragment.getInstance(),
	};
	private final int[] fragmentNames = {
		R.string.tab_genres,
		R.string.tab_artists,
		R.string.tab_albums,
		R.string.tab_playlists,
		R.string.tab_songs,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupActionBar();
	}
	
	private void setupActionBar() {
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
		int lastPosition = PrefUtils.getInt(this, R.string.pref_last_tab_position, 0);
		
		for(int i = 0; i < fragmentNames.length; i ++) {
			
			final Fragment fragment = fragments[i];
			
			ActionBar.Tab tab = bar.newTab();
			tab.setText(fragmentNames[i]);
			tab.setTabListener(new ActionBar.TabListener() {
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
					Log.d("DEBUG", "onTabUnselected");
				}
				
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					ft.replace(R.id.fragment_list, fragment);
					PrefUtils.setInt(MainActivity.this, R.string.pref_last_tab_position, tab.getPosition());
				}
				
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
					Log.d("DEBUG", "onTabReselected");
				}
			});
			boolean isSelected = false;
			if(i == lastPosition) {
				isSelected = true;
			}
			bar.addTab(tab, isSelected);
		}
	}
}
