package com.example.infosys_1d.Event;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;

import com.example.infosys_1d.R;

import java.util.ArrayList;
import java.util.List;

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
                 String date, List<String> tags, @ColorRes int color, String title,
                 String subtitle, int imageResId) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.color = color != 0 ? color : R.color.light_blue; // Default color
        this.title = title;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
    }

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

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public List<String> getTags() {
        return tags;
    }

    @ColorRes
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

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}