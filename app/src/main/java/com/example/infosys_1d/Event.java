package com.example.infosys_1d;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Event implements Parcelable {
    private String name;
    private String description;
    private String location;
    private String startTime;
    private String endTime;
    private String date;
    private List<String> tags;
    private int color;
    private String title;
    private String subtitle;
    private int imageResId;

    // Constructor with all fields
    public Event(String name, String description, String location, String startTime, String endTime, String date, List<String> tags, int color, String title, String subtitle, int imageResId) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.color = color;
        this.title = title;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
    }

    // Constructor without image
    public Event(String name, String description, String location, String startTime, String endTime, String date, List<String> tags, int color, String title, String subtitle) {
        this(name, description, location, startTime, endTime, date, tags, color, title, subtitle, -1);
    }

    // Parcelable implementation
    protected Event(Parcel in) {
        name = in.readString();
        description = in.readString();
        location = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        date = in.readString();
        tags = new ArrayList<>();
        in.readStringList(tags);
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
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeString(startTime);
        dest.writeString(endTime);
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

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getImageResId() {
        return imageResId;
    }
}
