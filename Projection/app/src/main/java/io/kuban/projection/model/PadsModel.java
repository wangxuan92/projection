package io.kuban.projection.model;

/**
 * Created by wangxuan on 17/11/16.
 */

public class PadsModel extends BaseModel {

    /**
     * device_id : 58A39FF02
     * app_version : 1.0.1
     * os : android
     * os_version : 5.1.1
     * model : Huawei M2
     * apptype : visitor
     * subtype : front_desk
     * screen_size : null
     * location_id : 343
     * space_id : 957
     * info : null
     * location : {"id":343,"name":"望京店","physical_address":"望京"}
     * space : {"id":957,"name":"酷办"}
     */

    public String device_id;
    public String app_version;
    public String os;
    public String os_version;
    public String model;
    public String apptype;
    public String subtype;
    public String screen_size;
    public String passcode;
    public int location_id;
    public int space_id;
    public String info;
    public LocationModel location;
    public SpaceModel space;
    public MeetingScreenModel meeting_screen;


}
