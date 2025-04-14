package com.example.infosys_1d.Event;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Event implements Parcelable {
    private String id;
    private String name;
    private String description;
    private String location;
    private long startTime;
    private long endTime;
    private String date;
    private List<String> tags;
    private int color;
    private String title;
    private String subtitle;
    private int imageResId;

    public Event(String name, String description, String location, long startTime, long endTime,
                 String date, List<String> tags, int color, String title, String subtitle, int imageResId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.tags = new ArrayList<>(tags);
        this.color = color;
        this.title = title;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
    }

    // Parcelable constructor
    protected Event(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        location = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
        date = in.readString();
        tags = in.createStringArrayList();
        color = in.readInt();
        title = in.readString();
        subtitle = in.readString();
        imageResId = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeString(date);
        dest.writeStringList(tags);
        dest.writeInt(color);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeInt(imageResId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}