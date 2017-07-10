package io.kuban.projection.model;

/**
 * Created by wangxuan on 16/9/21.
 */
public class YunXinModel extends BaseModel {

    /**
     * token : 3a809f2dcab6940093b919d48efd162a
     * accid : user_207
     */

    public String token;
    public String accid;

    @Override
    public String toString() {
        return "YunXinModel{" +
                "token='" + token + '\'' +
                ", accid='" + accid + '\'' +
                '}';
    }
}
