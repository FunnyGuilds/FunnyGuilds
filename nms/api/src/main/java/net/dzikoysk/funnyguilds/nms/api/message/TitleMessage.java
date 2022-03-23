package net.dzikoysk.funnyguilds.nms.api.message;

import com.google.common.base.Preconditions;

public class TitleMessage {
    private final String text;
    private final String subText;
    private final int fadeInDuration;
    private final int stayDuration;
    private final int fadeOutDuration;

    private TitleMessage(String text, String subText, int fadeInDuration, int stayDuration, int fadeOutDuration) {
        this.text = text;
        this.subText = subText;
        this.fadeInDuration = fadeInDuration;
        this.stayDuration = stayDuration;
        this.fadeOutDuration = fadeOutDuration;
    }

    public String getText() {
        return this.text;
    }

    public String getSubText() {
        return this.subText;
    }

    public int getFadeInDuration() {
        return this.fadeInDuration;
    }

    public int getStayDuration() {
        return this.stayDuration;
    }

    public int getFadeOutDuration() {
        return this.fadeOutDuration;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private String subText;
        private int fadeInDuration = -1;
        private int stayDuration = -1;
        private int fadeOutDuration = -1;

        private Builder() {
        }

        private Builder(String text, String subText, int fadeInDuration, int stayDuration, int fadeOutDuration) {
            this.text = text;
            this.subText = subText;
            this.fadeInDuration = fadeInDuration;
            this.stayDuration = stayDuration;
            this.fadeOutDuration = fadeOutDuration;
        }

        public Builder text(String text) {
            Preconditions.checkNotNull(text, "text can't be null!");

            this.text = text;
            return this;
        }

        public Builder subText(String subText) {
            Preconditions.checkNotNull(subText, "subText can't be null!");

            this.subText = subText;
            return this;
        }

        public Builder fadeInDuration(int fadeInDuration) {
            Preconditions.checkArgument(fadeInDuration >= -1, "fadeInDuration can't be less than -1!");

            this.fadeInDuration = fadeInDuration;
            return this;
        }

        public Builder stayDuration(int stayDuration) {
            Preconditions.checkArgument(stayDuration >= -1, "stayDuration can't be less than -1!");

            this.stayDuration = stayDuration;
            return this;
        }

        public Builder fadeOutDuration(int fadeOutDuration) {
            Preconditions.checkArgument(fadeOutDuration >= -1, "fadeOutDuration can't be less than -1!");

            this.fadeOutDuration = fadeOutDuration;
            return this;
        }

        public Builder copied() {
            return new Builder(this.text, this.subText, this.fadeInDuration, this.stayDuration, this.fadeOutDuration);
        }

        public TitleMessage build() {
            return new TitleMessage(this.text, this.subText, this.fadeInDuration, this.stayDuration, this.fadeOutDuration);
        }
    }
}
