package com.github.yanhua365.md5calc;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

/**
 * 文件信息
 */
public class FileInfoBean implements Serializable{

    private String url;
    private String md5;
    private String size;

    public FileInfoBean() {

    }

    public FileInfoBean(String url, String md5, String size) {
        this.url = url;
        this.md5 = md5;
        this.size = size;
    }

    @XmlAttribute
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlAttribute
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @XmlAttribute
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }



}
