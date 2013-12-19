package com.github.yanhua365.md5calc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 根元素
 */
@XmlRootElement(name = "files")
public class FilesInfoBean  implements Serializable{


    @XmlElement(name ="file")
    public List<FileInfoBean> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfoBean> files) {
        this.files = files;
    }

    List<FileInfoBean> files = new ArrayList<FileInfoBean>();
}
