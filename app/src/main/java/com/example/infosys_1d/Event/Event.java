package com.example.infosys_1d.Event;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Event implements Parcelable {
    private String name;
    private String description;
    private String location;
    private long startTime; // Store time as long (milliseconds)
    private long endTime;   // Store time as long (milliseconds)
    private String date;
    private List<String> tags;
    private int color;
    private String title;
    private String subtitle;
    private int imageResId;

    // Constructor with all fields, accepting long for startTime and endTime
    public Event(String name, String description, String location, long startTime, long endTime, String date, List<String> tags, int color, String title, String subtitle, int imageResId) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.color = (color == -1) ? getRandomPastelColor() : color;
        this.title = title;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
    }

    // Constructor without image, accepting long for startTime and endTime
    public Event(String name, String description, String location, long startTime, long endTime, String date, List<String> tags, int color, String title, String subtitle) {
        this(name, description, location, startTime, endTime, date, tags, color, title, subtitle, -1);
    }

    // Parcelable implementation
    protected Event(Parcel in) {
        name = in.readString();
        description = in.readString();
        location = in.readString();
        startTime = in.readLong(); // Read long for startTime
        endTime = in.readLong();   // Read long for endTime
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
        dest.writeLong(startTime); // Write long for startTime
        dest.writeLong(endTime);   // Write long for endTime
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

    public long getStartTime() {
        return startTime; // Return startTime as long
    }

    public long getEndTime() {
        return endTime; // Return endTime as long
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

    private int getRandomPastelColor() {
        java.util.Random random = new java.util.Random();
        final int base = 128; // Light base to ensure pastel
        int red = base + random.nextInt(128);
        int green = base + random.nextInt(128);
        int blue = base + random.nextInt(128);
        return android.graphics.Color.rgb(red, green, blue);
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

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    // Optional: If you want to allow adding individual tags
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
        }
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

}

