package com.example.playmusic.musiclist;

public class MusicVO {
    private long mAudioId; //오디오 id
    private long mAlbumId; // 앨범 id
    private String mTitle; // 노래 제목
    private String mArtist; // 아티스트 정보
    private String mAlbum; // 앨범정보
    private long mDuration; // 노래 길이
    private String mDataPath; // 데이터위치

    public long getmAudioId() {
        return mAudioId;
    }

    public void setmAudioId(long mAudioId) {
        this.mAudioId = mAudioId;
    }

    public long getmAlbumId() {
        return mAlbumId;
    }

    public void setmAlbumId(long mAlbumId) {
        this.mAlbumId = mAlbumId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public long getmDuration() {
        return mDuration;
    }

    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public String getmDataPath() {
        return mDataPath;
    }

    public void setmDataPath(String mDataPath) {
        this.mDataPath = mDataPath;
    }
}
