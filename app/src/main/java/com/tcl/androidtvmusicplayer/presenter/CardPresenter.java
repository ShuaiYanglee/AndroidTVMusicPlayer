package com.tcl.androidtvmusicplayer.presenter;

import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.entity.Artist;
import com.tcl.androidtvmusicplayer.entity.PlayList;
import com.tcl.androidtvmusicplayer.entity.Song;

import java.util.List;

/**
 * Created by yangshuai on 2018/9/23.
 *
 * @Description: 展示歌单等信息
 */

public class CardPresenter extends Presenter {

    private static final String TAG = "CardPresenter";

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private static int selectedBackgroundColor;//被选中后的卡片背景色
    private static int defaultBackgroundColor;//默认的卡片背景色
    private Drawable defaultCardImage;//卡片默认图片

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder: ");
        defaultBackgroundColor = parent.getResources().getColor(R.color.card_default_color);
        selectedBackgroundColor = parent.getResources().getColor(R.color.card_selected_color);
        defaultCardImage = parent.getResources().getDrawable(R.drawable.ic_app_logo);

        ImageCardView imageCardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };
        imageCardView.setFocusable(true);
        imageCardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(imageCardView, false);
        return new ViewHolder(imageCardView);

    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        String titleText = null;
        String picUrl = null;
        String contentText = null;
        if (item instanceof PlayList) {
            PlayList playList = (PlayList) item;
            titleText = playList.getName();
            picUrl = playList.getCoverImgUrl();
        }

        if (item instanceof Artist) {
            Artist artist = (Artist) item;
            titleText = artist.getName();
            picUrl = artist.getPicUrl();
        }

        if (item instanceof Song) {
            contentText = new String(" ");
            titleText = ((Song) item).getName();
            picUrl = ((Song) item).getAlbum().getPicUrl();
            List<Artist> artistList = ((Song) item).getArtists();
            for (Artist artist : artistList
                    ) {
                contentText += artist.getName() + " ";
            }
        }

        if (picUrl != null) {
            cardView.setTitleText(titleText);
            if (contentText != null) {
                cardView.setContentText(contentText);
            }
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
            Glide.with(viewHolder.view.getContext()).load(picUrl)
                    .centerCrop().error(defaultCardImage).into(cardView.getMainImageView());
        }

    }


    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onUnbindViewHolder: ");
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        //移除图片的引用，以便垃圾回收器释放内存
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);

    }


    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? selectedBackgroundColor : defaultBackgroundColor;
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }


}
