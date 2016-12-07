package com.vukoye.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieDataObject implements Parcelable{
    private int id;

    private String posterPath;

    private boolean adult;

    private String overview;

    private String releaseDate;

    private String genreId; //move to list in next iteration

    private String originalTitle;

    private String originalLanguage;

    private String title;

    private String backDropPath;

    private double popularity;

    private int voteCount;

    private boolean hasVideo;

    private double voteAverage;

    public MovieDataObject(final int id, final String posterPath, final double voteAverage, final String title, final String releaseDate, final String
            overview) {
        this.id = id;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.title = title;
        this.releaseDate = releaseDate;
        this.overview = overview;
    }

    protected MovieDataObject(Parcel in) {
        id = in.readInt();
        posterPath = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        genreId = in.readString();
        originalTitle = in.readString();
        originalLanguage = in.readString();
        title = in.readString();
        backDropPath = in.readString();
        popularity = in.readDouble();
        voteCount = in.readInt();
        hasVideo = in.readByte() != 0;
        voteAverage = in.readDouble();
    }

    public static final Creator<MovieDataObject> CREATOR = new Creator<MovieDataObject>() {
        @Override
        public MovieDataObject createFromParcel(Parcel in) {
            return new MovieDataObject(in);
        }

        @Override
        public MovieDataObject[] newArray(int size) {
            return new MovieDataObject[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeInt(id);
        parcel.writeString(posterPath);
        parcel.writeByte((byte) (adult ? 1 : 0));
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(genreId);
        parcel.writeString(originalTitle);
        parcel.writeString(originalLanguage);
        parcel.writeString(title);
        parcel.writeString(backDropPath);
        parcel.writeDouble(popularity);
        parcel.writeInt(voteCount);
        parcel.writeByte((byte) (hasVideo ? 1 : 0));
        parcel.writeDouble(voteAverage);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static class Builder {
        private int id;
        private String posterPath;
        private double voteAverage;
        private String title;
        private String releaseDate;
        private String overview;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withPosterPath(String posterPath) {
            this.posterPath = posterPath;
            return this;
        }

        public Builder withVoteAverage(double voteAverage) {
            this.voteAverage = voteAverage;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder withOverView(String overView) {
            this.overview = overView;
            return this;
        }

        public MovieDataObject build() {
            return new MovieDataObject(id, posterPath, voteAverage, title, releaseDate, overview);
        }
    }
}
