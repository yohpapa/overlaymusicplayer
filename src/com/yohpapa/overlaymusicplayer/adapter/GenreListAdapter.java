package com.yohpapa.overlaymusicplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.yohpapa.overlaymusicplayer.R;
import com.yohpapa.tools.CursorHelper;
import com.yohpapa.tools.task.GenreCursorLoader;

public class GenreListAdapter extends CursorAdapter {

	private LayoutInflater _inflater = null;
	private View.OnClickListener _listener = null;
	
	public class ViewHolder {
		public TextView genre;
	}
	
	public GenreListAdapter(Context context, Cursor cursor, View.OnClickListener listener) {
		super(context, cursor, true);
		
		_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_listener = listener;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		long genreId = CursorHelper.getLong(cursor, GenreCursorLoader._ID);
		String genreName = CursorHelper.getString(cursor, GenreCursorLoader.NAME);
		
		ViewHolder holder = (ViewHolder)view.getTag();
		holder.genre.setText(genreName);
		
		view.setTag(R.id.tag_genre_id, genreId);
		view.setTag(R.id.tag_genre_name, genreName);
		
		view.setOnClickListener(_listener);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View layout = _inflater.inflate(R.layout.list_genre_item, null);
		
		ViewHolder holder = new ViewHolder();
		holder.genre = (TextView)layout.findViewById(R.id.text_genre);
		layout.setTag(holder);
		
		return layout;
	}
}
