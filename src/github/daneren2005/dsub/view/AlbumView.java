/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package github.daneren2005.dsub.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.util.FileUtil;
import github.daneren2005.dsub.util.ImageLoader;
import github.daneren2005.dsub.util.Util;
import java.io.File;
import java.util.List;

/**
 * Used to display albums in a {@code ListView}.
 *
 * @author Sindre Mehus
 */
public class AlbumView extends UpdateView {
	private static final String TAG = AlbumView.class.getSimpleName();

	private Context context;
	private MusicDirectory.Entry album;
	private File file;

    private TextView titleView;
    private TextView artistView;
    private View coverArtView;

    public AlbumView(Context context) {
        super(context);
		this.context = context;
        LayoutInflater.from(context).inflate(R.layout.album_list_item, this, true);

        titleView = (TextView) findViewById(R.id.album_title);
        artistView = (TextView) findViewById(R.id.album_artist);
        coverArtView = findViewById(R.id.album_coverart);
        starButton = (ImageButton) findViewById(R.id.album_star);
        starButton.setFocusable(false); 
		
		moreButton = (ImageView) findViewById(R.id.album_more);
		moreButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.showContextMenu();
			}
		});
    }
    
    protected void setObjectImpl(Object obj1, Object obj2) {
    	this.album = (MusicDirectory.Entry) obj1;
    	titleView.setText(album.getTitle());
        artistView.setText(album.getArtist());
        artistView.setVisibility(album.getArtist() == null ? View.GONE : View.VISIBLE);
		((ImageLoader)obj2).loadImage(coverArtView, album, false, true);
        file = null;
    }
    
    @Override
	protected void updateBackground() {
		if(file == null) {
			String s = Util.getRestUrl(context, null) + album.getId();
			String cacheName = "directory-" + s.hashCode() + ".ser";
			MusicDirectory dir = FileUtil.deserialize(context, cacheName, MusicDirectory.class);

			if(dir != null) {
				List<MusicDirectory.Entry> songs = dir.getChildren(false, true);
				if(songs.size() > 0) {
					MusicDirectory.Entry firstSong = songs.get(0);
					File songFile = FileUtil.getSongFile(context, firstSong);
					file = songFile.getParentFile();
				}
			}

			// Backup in case cache is null or can't get dir from it
			if(file == null) {
				file = FileUtil.getAlbumDirectory(context, album);
			}
		}

		exists = file.exists();
		isStarred = album.isStarred(); 
	}
}
