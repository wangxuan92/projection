package io.kuban.projection.model;

import java.util.List;

/**
 * Created by wangxuan on 16/11/21.
 */
public class TabletInformationModel extends BaseModel {


    /**
     * area_id : 268
     * location_id : 2
     * space_id : 3
     * device_id : 12323214412
     * device_name : null
     * theme_type : null
     * notes : null
     * space_name : 酷办空间
     * location_name : 三元桥店
     * area_name : 视频会议室
     * assets : [{"url":"缺省URL","category":"image"},{"url":"缺省URL2","category":"image"}]
     */

    public String area_id;
    public String location_id;
    public String space_id;
    public String device_id;
    public String device_name;
    public String theme_type;
    public String notes;
    public String space_name;
    public String location_name;
    public String area_name;
    public String app_version;
    public String app_download_url;
    /**
     * url : 缺省URL
     * category : image
     */

    public List<AssetsBean> assets;


    public static class AssetsBean extends BaseModel {
        public String url;
        public String category;

    }

    public enum Progress {
        one(
                "1"
        ),
        two(
                "2"
        );

        public String id;

        Progress(String id) {
            this.id = id;
        }
    }
}
