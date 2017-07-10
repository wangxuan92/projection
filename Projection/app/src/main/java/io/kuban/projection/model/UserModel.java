package io.kuban.projection.model;

import java.util.List;

/**
 * Created by wang on 2016/8/9.
 */

public class UserModel extends BaseModel {

    /**
     * id : 163
     * name : 新员工
     * name_pinyin : xin yuan gong
     * email : li@kuban.io
     * phone_num : 99912345670
     * jwt_token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MTYzLCJ2ZXJzaW9uIjoxLCJleHAiOjE0NzA5OTE5Njd9.KtnLX0YigMz7AWo1rchyyDhN0m_U7nCp5li6jtBVBUo
     * avatar : null
     * gender : male
     * organization : {"id":30,"name":"之丸 ","name_pinyin":"zhi wan","full_name":"之丸科技有限公司 ","full_name_pinyin":"ke ji you xian gong si gong si","logo":null}
     * is_first_sign_in : null
     * organization_id : 30
     * location_id : 2
     * nickname : 小赤赤
     * title : 后端
     * wechat : 13896542324
     * birthplace : 湖北黄冈
     * birthday : 1992-9-12
     * interest : 绘画,游泳
     * experience : null
     */


    public String name;
    public String name_pinyin;
    public String email;
    public String phone_num;
    public String jwt_token;
    public String avatar;
    public String yunxin_token;
    public String accid;
    public String gender;
    public String space_title;
    public String organization_title;
    public boolean isSelected;


    public List<LocationModel> locations;
    public YunXinModel yunxin;


    public Object is_first_sign_in;
    public int organization_id;
    public int location_id;
    public String nickname;
    public String title;
    public String wechat;
    public String birthplace;
    public String birthday;
    public String interest;
    public Object experience;

    public LocationModel getFirstLocation() {
        if (locations == null || locations.size() <= 0) {
            return null;
        }

        return locations.get(0);
    }

    public String getFirstLocationName() {

        LocationModel location = getFirstLocation();
        if (location != null) {
            return location.name;
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "name='" + name + '\'' +
                ", name_pinyin='" + name_pinyin + '\'' +
                ", email='" + email + '\'' +
                ", phone_num='" + phone_num + '\'' +
                ", jwt_token='" + jwt_token + '\'' +
                ", avatar='" + avatar + '\'' +
                ", yunxin_token='" + yunxin_token + '\'' +
                ", gender='" + gender + '\'' +
                ", space_title='" + space_title + '\'' +
                ", organization_title='" + organization_title + '\'' +
                ", locations=" + locations +
                ", yunxin=" + yunxin +
                ", is_first_sign_in=" + is_first_sign_in +
                ", organization_id=" + organization_id +
                ", location_id=" + location_id +
                ", nickname='" + nickname + '\'' +
                ", title='" + title + '\'' +
                ", wechat='" + wechat + '\'' +
                ", birthplace='" + birthplace + '\'' +
                ", birthday='" + birthday + '\'' +
                ", interest='" + interest + '\'' +
                ", experience=" + experience +
                '}';
    }
}
