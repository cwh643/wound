package com.dnion.app.android.injuriesapp;

import android.util.Pair;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫华 on 2017/4/18.
 */

public class ItemObj {

    private ItemType type;

    private String label;

    private String unit;

    private String value;

    private List<Pair> dict;

    private EditText editText;

    private String category;

    public ItemObj(ItemType type, String label) {
        this.type = type;
        this.label = label;
    }

    public ItemObj(ItemType type, String label, String value) {
        this.type = type;
        this.label = label;
        this.value = value;
    }

    public ItemObj(String category, ItemType type, String label, List<Pair> dict) {
        this.category = category;
        this.type = type;
        this.label = label;
        this.dict = dict;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public List<String> getDict() {
        List<String> items = new ArrayList<String>();
        for (Pair p : dict) {
            items.add("" + p.second);
        }
        return items;
    }

    public void setDict(List<Pair> dict) {
        this.dict = dict;
    }
}
