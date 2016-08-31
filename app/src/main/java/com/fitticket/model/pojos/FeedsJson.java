package com.fitticket.model.pojos;

import java.util.ArrayList;

/**
 * Created by Fiticket on 06/11/15.
 */
public class FeedsJson {

    private FeedDataJson data;

    public FeedDataJson getData() {
        return data;
    }

    public void setData(FeedDataJson data) {
        this.data = data;
    }

    public class FeedDataJson {
        private String tweetCount;
        private String blogCount;
        private String systemCount;

        private ArrayList<FeedData> sortedFeed;

        public ArrayList<FeedData> getSortedFeed() {
            return sortedFeed;
        }

        public void setSortedFeed(ArrayList<FeedData> sortedFeed) {
            this.sortedFeed = sortedFeed;
        }

        public String getTweetCount() {
            return tweetCount;
        }

        public void setTweetCount(String tweetCount) {
            this.tweetCount = tweetCount;
        }

        public String getBlogCount() {
            return blogCount;
        }

        public void setBlogCount(String blogCount) {
            this.blogCount = blogCount;
        }

        public String getSystemCount() {
            return systemCount;
        }

        public void setSystemCount(String systemCount) {
            this.systemCount = systemCount;
        }

        public class FeedData {
            String type;
            long postId;
            String title;
            String author;
            String link;
            long timeStamp;
            String profileImageURL;
            String text;
            String tweetDate;
            String tweetId;
            String userName;
            String featuredImageURL;
            int featuredImageWidth;
            int featuredImageHeight;
            ArrayList<String> featuredImageLink;
            ArrayList<MediaJson> media;

            public String getFeaturedImageURL() {
                return featuredImageURL;
            }

            public void setFeaturedImageURL(String featuredImageURL) {
                this.featuredImageURL = featuredImageURL;
            }

            public int getFeaturedImageWidth() {
                return featuredImageWidth;
            }

            public void setFeaturedImageWidth(int featuredImageWidth) {
                this.featuredImageWidth = featuredImageWidth;
            }

            public int getFeaturedImageHeight() {
                return featuredImageHeight;
            }

            public void setFeaturedImageHeight(int featuredImageHeight) {
                this.featuredImageHeight = featuredImageHeight;
            }

            public ArrayList<String> getFeaturedImageLink() {
                return featuredImageLink;
            }

            public void setFeaturedImageLink(ArrayList<String> featuredImageLink) {
                this.featuredImageLink = featuredImageLink;
            }

            public ArrayList<MediaJson> getMedia() {
                return media;
            }

            public void setMedia(ArrayList<MediaJson> media) {
                this.media = media;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public long getPostId() {
                return postId;
            }

            public void setPostId(long postId) {
                this.postId = postId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public long getTimeStamp() {
                return timeStamp;
            }

            public void setTimeStamp(long timeStamp) {
                this.timeStamp = timeStamp;
            }

            public String getProfileImageURL() {
                return profileImageURL;
            }

            public void setProfileImageURL(String profileImageURL) {
                this.profileImageURL = profileImageURL;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getTweetDate() {
                return tweetDate;
            }

            public void setTweetDate(String tweetDate) {
                this.tweetDate = tweetDate;
            }

            public String getTweetId() {
                return tweetId;
            }

            public void setTweetId(String tweetId) {
                this.tweetId = tweetId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }



            public class MediaJson {
                String expandedURL;
                String mediaURL;
                String mediaText;
                String mediaType;

                public String getExpandedURL() {
                    return expandedURL;
                }

                public void setExpandedURL(String expandedURL) {
                    this.expandedURL = expandedURL;
                }

                public String getMediaURL() {
                    return mediaURL;
                }

                public void setMediaURL(String mediaURL) {
                    this.mediaURL = mediaURL;
                }

                public String getMediaText() {
                    return mediaText;
                }

                public void setMediaText(String mediaText) {
                    this.mediaText = mediaText;
                }

                public String getMediaType() {
                    return mediaType;
                }

                public void setMediaType(String mediaType) {
                    this.mediaType = mediaType;
                }
            }
        }
    }
}
