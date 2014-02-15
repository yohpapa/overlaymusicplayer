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

import java.util.HashSet;

import com.yohpapa.tools.CursorHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

public class GenreSongIdRetriever extends AsyncTask<Void, Void, SongIdList> {

	private final long _genreId;
	private final Context _context;
	private final OnFinishRetrievingInfo _onFinish;
	
	public GenreSongIdRetriever(Context context, long genreId, String genreName, OnFinishRetrievingInfo onFinish) {
		_context = context;
		_genreId = genreId;
		_onFinish = onFinish;
	}
	
	@Override
	protected SongIdList doInBackground(Void... args) {
		
		ContentResolver resolver = _context.getContentResolver();
		if(resolver == null) {
			return null;
		}
		
		Cursor genreCursor = null;
		Cursor mediaCursor = null;
		try {
			genreCursor = resolver.query(
					MediaStore.Audio.Genres.Members.getContentUri("external", _genreId),
					new String[] {
						MediaStore.Audio.Genres.Members.DATA,
					},
					null, null, null);
			
			if(genreCursor == null || !genreCursor.moveToFirst()) {
				return null;
			}
			
			int index = 0;
			HashSet<String> songPaths = new HashSet<String>();
			do {
				songPaths.add(CursorHelper.getString(genreCursor, MediaStore.Audio.Genres.Members.DATA));
			} while(genreCursor.moveToNext());
			
			mediaCursor = resolver.query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] {
						MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DATA,
					},
					null, null,
					MediaStore.Audio.Media.TITLE + " ASC");
			
			if(mediaCursor == null || !mediaCursor.moveToFirst()) {
				return null;
			}
			
			long[] songIds = new long[songPaths.size()];
			index = 0;
			do {
				
				String path = CursorHelper.getString(mediaCursor, MediaStore.Audio.Media.DATA);
				if(songPaths.contains(path)) {
					songIds[index ++] = CursorHelper.getLong(mediaCursor, MediaStore.Audio.Media._ID);
					if(index >= songIds.length) {
						break;
					}
				}
				
			} while(mediaCursor.moveToNext());
			
			return new SongIdList(songIds);
			
		} finally {
			if(genreCursor != null) {
				genreCursor.close();
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
