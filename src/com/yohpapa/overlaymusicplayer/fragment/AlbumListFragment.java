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

package com.yohpapa.overlaymusicplayer.fragment;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.yohpapa.overlaymusicplayer.R;
import com.yohpapa.overlaymusicplayer.adapter.AlbumListAdapter;
import com.yohpapa.overlaymusicplayer.service.OverlayMusicPlayerService;

public class AlbumListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	public static AlbumListFragment getInstance() {
		return new AlbumListFragment();
	}
	
    // Use 1/8th of the available memory for this memory cache.
    private final int ARTWORK_CACHE_SIZE = (int)(Runtime.getRuntime().maxMemory() / 1024) / 8;
	
	private LruCache<Long, Bitmap> _artworkCache = null;
	private int _lastPosition = 0;
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		
		if(savedState != null) {
			_lastPosition = savedState.getInt("_lastPosition");
		}
		
		_artworkCache = new LruCache<Long, Bitmap>(ARTWORK_CACHE_SIZE) {
	        @Override
	        protected int sizeOf(Long key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		
		if(savedState != null) {
			_lastPosition = savedState.getInt("_lastPosition");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		ListView list = getListView();
		if(list == null) {
			return;
		}
		
		_lastPosition = list.getFirstVisiblePosition();
		if(outState != null) {
			outState.putInt("_lastPosition", _lastPosition);
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(
				getActivity(),
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                    MediaStore.Audio.Albums.ARTIST,
                    MediaStore.Audio.Albums.ALBUM_ART,
                },
                null, null, MediaStore.Audio.Albums.ALBUM + " ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		ListAdapter adapter = new AlbumListAdapter(getActivity(), cursor, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickAlbum(view);
			}
		}, _artworkCache);
		setListAdapter(adapter);
		
		ListView list = getListView();
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickAlbum(view);
			}
		});
		list.setSelectionFromTop(_lastPosition, 0);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	private void onClickAlbum(View view) {
		long albumId = (Long)view.getTag(R.id.tag_album_id);
		String albumName = (String)view.getTag(R.id.tag_album_name);
		
		Intent intent = new Intent(getActivity(), OverlayMusicPlayerService.class);
		intent.setAction(OverlayMusicPlayerService.ACTION_SELECT_ALBUM);
		intent.putExtra(OverlayMusicPlayerService.PRM_ALBUM_ID, albumId);
		intent.putExtra(OverlayMusicPlayerService.PRM_ALBUM_NAME, albumName);
		intent.putExtra(OverlayMusicPlayerService.PRM_NEED_TO_PLAY_AFTER_SELECT, true);
		getActivity().startService(intent);
	}
}
