package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.BaseResponseJson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 06/11/15.
 */
public class CampaignJson extends BaseResponseJson{

    private CampaignDataJson data;

    public CampaignDataJson getData() {
        return data;
    }

    public void setData(CampaignDataJson data) {
        this.data = data;
    }

    public class CampaignDataJson {
        private ArrayList<CampaignFeed> sortedFeed;

        public ArrayList<CampaignFeed> getSortedFeed() {
            return sortedFeed;
        }

        public void setSortedFeed(ArrayList<CampaignFeed> sortedFeed) {
            this.sortedFeed = sortedFeed;
        }

        public class CampaignFeed {
            String type;
            long postId;
            String title;
            String author;
            String link;
            String campaignType;
            String campaignPlacement;
            String campaignVideo;
            String campaignImage;
            String campaignText;
            String campaignLink;
            boolean sponsorLogo;
            String SponsorName;
            long startDate;
            long endDate;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCampaignImage() {
                return campaignImage;
            }

            public void setCampaignImage(String campaignImage) {
                this.campaignImage = campaignImage;
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

            public String getCampaignType() {
                return campaignType;
            }

            public void setCampaignType(String campaignType) {
                this.campaignType = campaignType;
            }

            public String getCampaignPlacement() {
                return campaignPlacement;
            }

            public void setCampaignPlacement(String campaignPlacement) {
                this.campaignPlacement = campaignPlacement;
            }

            public String getCampaignVideo() {
                return campaignVideo;
            }

            public void setCampaignVideo(String campaignVideo) {
                this.campaignVideo = campaignVideo;
            }



            public String getCampaignText() {
                return campaignText;
            }

            public void setCampaignText(String campaignText) {
                this.campaignText = campaignText;
            }

            public String getCampaignLink() {
                return campaignLink;
            }

            public void setCampaignLink(String campaignLink) {
                this.campaignLink = campaignLink;
            }

            public boolean isSponsorLogo() {
                return sponsorLogo;
            }

            public void setSponsorLogo(boolean sponsorLogo) {
                this.sponsorLogo = sponsorLogo;
            }

            public String getSponsorName() {
                return SponsorName;
            }

            public void setSponsorName(String sponsorName) {
                SponsorName = sponsorName;
            }

            public long getStartDate() {
                return startDate;
            }

            public void setStartDate(long startDate) {
                this.startDate = startDate;
            }

            public long getEndDate() {
                return endDate;
            }

            public void setEndDate(long endDate) {
                this.endDate = endDate;
            }
        }
    }
}
