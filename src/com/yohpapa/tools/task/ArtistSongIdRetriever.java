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
 */
public class ArtistSongIdRetriever extends AsyncTask<Void, Void, SongIdList> {

	private Context _context = null;
	private long _artistId = -1L;
	private OnFinishRetrievingInfo _listener = null;
	
	public ArtistSongIdRetriever(Context context, long artistId, String artistName, OnFinishRetrievingInfo listener) {
		_context = context;
		_artistId = artistId;
		_listener = listener;
	}
	
	@Override
	protected SongIdList doInBackground(Void... args) {
		ContentResolver resolver = _context.getContentResolver();
		if(resolver == null) {
			return null;
		}
		
		Cursor cursor = null;
		try {
			cursor = resolver.query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						new String[] {
							MediaStore.Audio.Media._ID,
						},
						MediaStore.Audio.Media.ARTIST_ID + "=?", new String[] {String.valueOf(_artistId)},
						MediaStore.Audio.Media.ARTIST + " ASC");
			
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
		if(_listener != null) {
			_listener.onFinishRetrieving(result);
		}
	}
}
