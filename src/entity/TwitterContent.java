package entity;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TwitterContent {
    private String text;
    private List<String> tagList;
    private Date createTime;

    private String location;

    public List<String> genLowerCaseTagList() {
        List<String> tagListTmp = new ArrayList<>();
        for (String tag : tagList) {
            tagListTmp.add(tag.toLowerCase());
        }
        return tagListTmp;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
