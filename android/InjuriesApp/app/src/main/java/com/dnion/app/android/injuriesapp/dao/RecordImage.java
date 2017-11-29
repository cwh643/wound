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

    public RecordImage() {

    }
    public RecordImage(Integer id, Integer selectId, Integer positionId) {
        this.id = id;
        this.selectId = selectId;
        this.positionId = positionId;
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
}
