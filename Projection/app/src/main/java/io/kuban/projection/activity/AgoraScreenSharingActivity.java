package io.kuban.projection.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.rtc.video.VideoCanvas;
import io.kuban.projection.base.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.kuban.projection.R;
import io.kuban.projection.event.RefreshEvent;

/**
 * Created by wangxuan on 18/7/5.
 */

public class AgoraScreenSharingActivity extends BaseCompatActivity {
    //    public SurfaceView remoteVideoView;
    @BindView(R.id.rl_channel_name)
    RelativeLayout rlChannelName;
    @BindView(R.id.remote_video_view_container)
    FrameLayout container;
    @BindView(R.id.channel_name)
    TextView channelName;
    public SurfaceView surfaceView;
    public String LOG_TAG = "AgoraScreen";
    public int validateCode;

    private static final int SHOW_VIDEO = 123;
    private static final int HIDE_VIDEO = 124;
    public RtcEngine rtcEngine = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agora_screen_sharing_activity);
        ButterKnife.bind(this);
        EventBus.getDefault().post(new RefreshEvent(Constants.FINISH));
        if (container.getChildCount() >= 1) {
            return;
        }
        surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        initRtcEngine();
        setRtcEngine();
        generateCode();
        Log.e("====================   ", "  eeeeeeeee ");
    }

    //在主线程创建一个Handler对象。
    //重写Handler的handleMessage方法，这个就是接收并处理消息的方法。
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_VIDEO:
                    rlChannelName.setVisibility(View.GONE);
                    break;
                case HIDE_VIDEO:
                    rtcEngine.leaveChannel();
                    generateCode();
                    break;

            }
        }
    };

    public void generateCode() {
        rlChannelName.setVisibility(View.VISIBLE);
        validateCode = (int) ((Math.random() * 9 + 1) * 100000);
        channelName.setText(validateCode + "");
        //加入频道
        rtcEngine.joinChannel(null, validateCode + "", null, Calendar.getInstance().get(Calendar.DATE));
    }

    public void initRtcEngine() {
        //创建 RtcEngine 对象,并填入 App ID
        try {
            rtcEngine = RtcEngine.create(this, "4cb88453960c4ea2b8d772d6ef323f34", new IRtcEngineEventHandler() {
                @Override
                public void onWarning(int warn) {
                    super.onWarning(warn);
                    Log.e(LOG_TAG, "onWarning " + warn);
//                    Message msg = new Message();
//                    msg.what = HIDE_VIDEO;
//                    handler.sendMessage(msg);
                }

                @Override
                public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                    super.onJoinChannelSuccess(channel, uid, elapsed);
                    Log.e(LOG_TAG, "onJoinChannelSuccess " + channel + " " + elapsed);
                }

                @Override
                public void onError(int err) {
                    super.onError(err);
                    Log.e(LOG_TAG, "onError " + err);
                }

                @Override
                public void onAudioRouteChanged(int routing) {
                    super.onAudioRouteChanged(routing);
                    Log.e(LOG_TAG, "onAudioRouteChanged " + routing);
                }

                @Override
                public void onUserJoined(int uid, int elapsed) {
                    super.onUserJoined(uid, elapsed);
                    Log.e(LOG_TAG, uid + " onUserJoined " + elapsed);

                    rtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
                }

                @Override
                public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
                    super.onFirstRemoteVideoFrame(uid, width, height, elapsed);
                    Log.e(LOG_TAG, uid + " 远端视频显示回调 " + width + "  " + height + "  " + elapsed);
                    Message msg = new Message();
                    msg.what = SHOW_VIDEO;
                    handler.sendMessage(msg);
                }

                @Override
                public void onLeaveChannel(RtcStats stats) {
                    super.onLeaveChannel(stats);
                    Log.e(LOG_TAG, " 离开频道回调 ");
                }

                @Override
                public void onUserMuteVideo(int uid, boolean muted) {
                    super.onUserMuteVideo(uid, muted);
                    Log.e(LOG_TAG, " 其他用户已停发/已重发视频流回调 ");
                }

                @Override
                public void onUserEnableVideo(int uid, boolean enabled) {
                    super.onUserEnableVideo(uid, enabled);
                    Log.e(LOG_TAG, " 其他用户启用/关闭视频 ");
                }

                @Override
                public void onConnectionBanned() {
                    super.onConnectionBanned();
                    Log.e(LOG_TAG, " 连接已被禁止回调 ");
                }

                @Override
                public void onVideoStopped() {
                    super.onVideoStopped();
                    Log.e(LOG_TAG, " 视频功能停止回调 ");
                }

                @Override
                public void onConnectionLost() {
                    super.onConnectionLost();
                    Log.e(LOG_TAG, " 连接丢失回调 ");
                }

                @Override
                public void onConnectionInterrupted() {
                    super.onConnectionInterrupted();
                    Log.e(LOG_TAG, " 连接中断回调  ");
                }

                @Override
                public void onUserOffline(int uid, int reason) {
                    super.onUserOffline(uid, reason);
                    Log.e(LOG_TAG, " 其他用户离开当前频道回调  ");
                    Message msg = new Message();
                    msg.what = HIDE_VIDEO;
                    handler.sendMessage(msg);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRtcEngine() {
        rtcEngine.setChannelProfile(1);
        rtcEngine.setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
//        设置音质
        rtcEngine.setAudioProfile(5, 2);
        rtcEngine.stopAudioRecording();
        rtcEngine.stopPlayingStream();
        rtcEngine.stopEchoTest();
        rtcEngine.disableAudio();//关闭Audio
//        该方法设定外放(扬声器)最小为 0，最大为 255
        rtcEngine.setSpeakerphoneVolume(30);
        //打开视频模式
        rtcEngine.enableVideo();
        //设置是否开启本地摄像头
        rtcEngine.enableLocalVideo(false);
        rtcEngine.enableWebSdkInteroperability(true);
        rtcEngine.enableAudioVolumeIndication(1000, 3);
        //设置视频分辨率
        rtcEngine.setVideoProfile(io.agora.rtc.Constants.VIDEO_PROFILE_720P_6, true);
    }
}
