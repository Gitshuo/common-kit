package com.ws.common.kit;

import com.ws.common.kit.ExcelUtil.PropParse;
import java.io.Serializable;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author wangshuo
 * @version 2018-03-02
 */
public class PoiDataVo implements Serializable {

    private static final long serialVersionUID = -8138544240151664655L;
    @PropParse(name = "poiId", index = 0)
    private String poiId;
    @PropParse(name = "bdid", index = 1)
    private String bdid;
    @PropParse(name = "posName", index = 2)
    private String posName;
    @PropParse(name = "flId", index = 3)
    private String flId;
    @PropParse(name = "flName", index = 4)
    private String flName;

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getBdid() {
        return bdid;
    }

    public void setBdid(String bdid) {
        this.bdid = bdid;
    }

    public String getPosName() {
        return posName;
    }

    public void setPosName(String posName) {
        this.posName = posName;
    }

    public String getFlId() {
        return flId;
    }

    public void setFlId(String flId) {
        this.flId = flId;
    }

    public String getFlName() {
        return flName;
    }

    public void setFlName(String flName) {
        this.flName = flName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
