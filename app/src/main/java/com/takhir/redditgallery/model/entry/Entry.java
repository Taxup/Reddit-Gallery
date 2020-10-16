package com.takhir.redditgallery.model.entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name = "entry", strict = false)
public class Entry implements Serializable {

    @Element(name = "content")
    private String content;

    public Entry() {
    }

    public Entry(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "\n\nEntry{" +
                "content='" + content + '\n' +
                "----------------------------------------------------------------------------------------------------------------------------------" +
                "splitter";
//        ОБРАТИ ВНМАНИЕ сюда
    }
}
