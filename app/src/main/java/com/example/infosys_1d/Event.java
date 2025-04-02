package com.example.infosys_1d;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Event implements Parcelable {
    private String title;
    private String date;
    private String description;
    private String startTime;
    private String endTime;
    private String location;
    private int imageResId;
    private List<String> tags;
    private String subtitle;

    // Constructor with all fields
    public Event(String title, String date, String description, String startTime, String endTime, String location, int imageResId, List<String> tags, String subtitle) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.imageResId = imageResId;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.subtitle = subtitle;
    }

    // Constructor without image
    public Event(String title, String date, String description, String startTime, String endTime, String location, List<String> tags, String subtitle) {
        this(title, date, description, startTime, endTime, location, -1, tags, subtitle);
    }

    // Parcelable implementation
    protected Event(Parcel in) {
        title = in.readString();
        date = in.readString();
        description = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        location = in.readString();
        imageResId = in.readInt();
        tags = new ArrayList<>();
        in.readStringList(tags);
        subtitle = in.readString();
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
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(location);
        dest.writeInt(imageResId);
        dest.writeStringList(tags);
        dest.writeString(subtitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public int getImageResId() {
        return imageResId;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getSubtitle() {
        return subtitle;
    }
}