package com.dnion.app.android.injuriesapp.dao;

/**
 * Created by 卫华 on 2017/7/12.
 */

public class RecordImage {
    private Integer id;

    private Integer selectId;

    private Integer recordId;

    private String imageType;

    private String imagePath;

    private Integer positionId;

    private boolean selected = false;

    private String createTime;

    private String describe;//描述

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public RecordImage() {

    }
    public RecordImage(Integer id, Integer selectId, Integer positionId, String describe) {
        this.id = id;
        this.selectId = selectId;
        this.positionId = positionId;
        this.describe = describe;
    }

    public Integer getSelectId() {
        return selectId;
    }

    public void setSelectId(Integer selectId) {
        this.selectId = selectId;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
