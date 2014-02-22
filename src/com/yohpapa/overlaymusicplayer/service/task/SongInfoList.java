/*
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

package com.yohpapa.overlaymusicplayer.service.task;

/**
 * @author YohPapa
 */
public class SongInfoList {

	public long[] songIds = null;
	public String[] titles = null;
	
	private int _position = 0;
	
	public SongInfoList(int length) {
		songIds = new long[length];
		titles = new String[length];
		_position = 0;
	}
	
	public void addSongInfo(long songId, String title) {
		songIds[_position] = songId;
		titles[_position] = title;
		_position ++;
	}
	
	public int getCount() {
		return _position;
	}
}
