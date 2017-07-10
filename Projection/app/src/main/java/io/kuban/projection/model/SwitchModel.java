package io.kuban.projection.model;


/**
 * Created by wangxuan on 16/10/26.
 */
public class SwitchModel extends BaseModel {
    public String name;
    public int imgResources;
    public int themeColor;
    public int themeLayout;

    public SwitchModel(String id, String name, int imgResources, int themeColor, int themeLayout) {
        this.id = id;
        this.name = name;
        this.imgResources = imgResources;
        this.themeColor = themeColor;
        this.themeLayout = themeLayout;
    }

    public SwitchModel() {
    }
}
