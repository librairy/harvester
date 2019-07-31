package es.upm.oeg.librairy.harvester.data;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Document {

    private static final Logger LOG = LoggerFactory.getLogger(Document.class);

    private String id;

    private String name;

    private String text;

    private List<String> labels;

    private String format;

    private String language;

    private String source;

    private String date;

    public Document() {
    }

    public boolean isValid(){
        return !Strings.isNullOrEmpty(getId()) &&
                !Strings.isNullOrEmpty(getName()) &&
                !Strings.isNullOrEmpty(getText()) &&
                !Strings.isNullOrEmpty(getFormat()) &&
                !Strings.isNullOrEmpty(getLanguage()) &&
                !Strings.isNullOrEmpty(getSource()) &&
                !Strings.isNullOrEmpty(getDate());
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", labels=" + labels +
                ", format='" + format + '\'' +
                ", language='" + language + '\'' +
                ", source='" + source + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
