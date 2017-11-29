package fan.soundrecordingdemo.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by 卫华 on 2017/7/20.
 */

public class RecordPlayer {

    private static MediaPlayer mediaPlayer;

    private Context mcontext;

    public RecordPlayer(Context context) {
        this.mcontext = context;
    }

    // 播放录音文件
    public void playRecordFile(File file) {
        if (file != null &&  file.exists()) {
            Uri uri = Uri.fromFile(file);
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    //监听MediaPlayer播放完成
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer paramMediaPlayer) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                            //弹窗提示
                            Toast.makeText(mcontext, "播放完成", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                mediaPlayer.setDataSource(mcontext, uri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 暂停播放录音
    public void pausePalyer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.e("TAG", "暂停播放");
        }

    }

    // 停止播放录音
    public void stopPalyer() {
        // 这里不调用stop()，调用seekto(0),把播放进度还原到最开始
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            //mediaPlayer.pause();
            //mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            Log.e("TAG", "停止播放");
        }
    }

    public void releasePalyer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }
}
