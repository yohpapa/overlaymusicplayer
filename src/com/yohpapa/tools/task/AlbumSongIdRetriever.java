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

package com.yohpapa.tools.task;

import com.yohpapa.tools.CursorHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

/**
 * @author YohPapa
 *
 */
public class AlbumSongIdRetriever extends AsyncTask<Void, Void, SongIdList> {

	private final long _albumId;
	private final Context _context;
	private final OnFinishRetrievingInfo _onFinish;
	
	public AlbumSongIdRetriever(Context context, long albumId, String albumName, OnFinishRetrievingInfo onFinish) {
		_context = context;
		_albumId = albumId;
		_onFinish = onFinish;
	}
	
	@Override
	protected SongIdList doInBackground(Void... params) {
		
		ContentResolver resolver = _context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[] {
                        MediaStore.Audio.Media._ID,
                    },
                    MediaStore.Audio.Media.ALBUM_ID + "=?", new String[] {String.valueOf(_albumId)},
                    MediaStore.Audio.Media.TRACK + " ASC");

	        if(cursor == null || !cursor.moveToFirst()) {
	        	return null;
	        }
	        
	        long[] songIds = new long[cursor.getCount()];
	        int index = 0;
	        
	        do {
	        	songIds[index ++] = CursorHelper.getLong(cursor, MediaStore.Audio.Media._ID);
	        } while(cursor.moveToNext());
	        
			return new SongIdList(songIds);
			
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	protected void onPostExecute(SongIdList result) {
		if(_onFinish != null) {
			_onFinish.onFinishRetrieving(result);
		}
	}
}
