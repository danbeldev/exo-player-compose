package com.example.exo_player_compose.exoPlayerFilter;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.exo_player_compose.exoPlayerFilter.chooser.EConfigChooser;
import com.example.exo_player_compose.exoPlayerFilter.contextfactory.EContextFactory;
import com.example.exo_player_compose.exoPlayerFilter.filter.GlFilter;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by sudamasayuki on 2017/05/16.
 */
public class EPlayerView extends GLSurfaceView implements Player.Listener {

    private final static String TAG = EPlayerView.class.getSimpleName();

    private final EPlayerRenderer renderer;
    private ExoPlayer player;

    private float videoAspect = 1f;
    private PlayerScaleType playerScaleType = PlayerScaleType.RESIZE_FIT_WIDTH;

    public EPlayerView(Context context) {
        this(context, null);
    }

    public EPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextFactory(new EContextFactory());
        setEGLConfigChooser(new EConfigChooser());

        renderer = new EPlayerRenderer(this);
        setRenderer((Renderer) renderer);
    }

    public EPlayerView setSimpleExoPlayer(ExoPlayer player) {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
        this.player = player;
        this.player.addListener(this);
        this.renderer.setSimpleExoPlayer(player);
        return this;
    }

    public void setGlFilter(GlFilter glFilter) {
        renderer.setGlFilter(glFilter);
    }

    public void setPlayerScaleType(PlayerScaleType playerScaleType) {
        this.playerScaleType = playerScaleType;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int viewWidth = measuredWidth;
        int viewHeight = measuredHeight;

        switch (playerScaleType) {
            case RESIZE_FIT_WIDTH:
                viewHeight = (int) (measuredWidth / videoAspect);
                break;
            case RESIZE_FIT_HEIGHT:
                viewWidth = (int) (measuredHeight * videoAspect);
                break;
        }

        // Log.d(TAG, "onMeasure viewWidth = " + viewWidth + " viewHeight = " + viewHeight);

        setMeasuredDimension(viewWidth, viewHeight);

    }

    @Override
    public void onPause() {
        super.onPause();
        renderer.release();
    }

    //////////////////////////////////////////////////////////////////////////
    // SimpleExoPlayer.VideoListener


    @Override
    protected void onSizeChanged(int width, int height, int unappliedRotationDegrees, int pixelWidthHeightRatio) {
        // Log.d(TAG, "width = " + width + " height = " + height + " unappliedRotationDegrees = " + unappliedRotationDegrees + " pixelWidthHeightRatio = " + pixelWidthHeightRatio);
        videoAspect = ((float) width / height) * pixelWidthHeightRatio;
        // Log.d(TAG, "videoAspect = " + videoAspect);
        requestLayout();
    }

    @Override
    public void onRenderedFirstFrame() {
        // do nothing
    }
}

