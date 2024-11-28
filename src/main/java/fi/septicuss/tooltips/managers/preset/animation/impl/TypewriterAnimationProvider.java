package fi.septicuss.tooltips.managers.preset.animation.impl;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.animation.Animation;
import fi.septicuss.tooltips.managers.preset.animation.AnimationProvider;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TypewriterAnimationProvider implements AnimationProvider {

    @Override
    public Animation create(String text, Arguments arguments) {
        return new TypewriterAnimation(text, arguments);
    }

    static class TypewriterAnimation implements Animation {

        final String text;
        final boolean loop;
        final int speed;
        final String cursor;
        final int delay;
        final String sound;

        final List<String> frames = new ArrayList<>();

        int frame = 0;
        int skippedFrames = 0;

        boolean firstFrameSkipped = false;

        boolean finished = false;

        protected TypewriterAnimation(String text, Arguments arguments) {
            this.text = text;
            this.loop = arguments.has("loop") && arguments.get("loop").getAsBool();
            this.speed = arguments.has("speed") && arguments.get("speed").isNumber() ? arguments.get("speed").getAsInt() - 1 : 3;
            this.cursor = arguments.has("cursor") ? arguments.get("cursor").getAsString() : null;
            this.delay = arguments.has("delay", "d") && arguments.get("delay", "d").isNumber() ? arguments.get("delay", "d").getAsInt() : 0;
            this.sound = arguments.has("sound") ? arguments.get("sound").getAsString() : null;
            this.skippedFrames = delay;
            this.generateFrames();
        }

        private void generateFrames() {
            this.frames.add("");
            for (int i = 0; i < this.text.length(); i++) {
                final char character = this.text.charAt(i);

                if (character == '<') {
                    final int closingSign = this.text.indexOf('>', i);
                    if (closingSign != -1) {
                        final String between = this.text.substring(0, closingSign + 1);
                        this.frames.add(between);
                        i = closingSign;
                        continue;
                    }
                }

                if (character == '{') {
                    final int closingBracket = this.text.indexOf('}', i);
                    if (closingBracket != -1) {
                        final String between = this.text.substring(0, closingBracket + 1);
                        this.frames.add(between);
                        i = closingBracket;
                        continue;
                    }
                }

                this.frames.add(text.substring(0, i + 1));
            }
        }

        @Override
        public void tick(Player player) {
            if (finished) return;

            // Skip first frame to display first letter properly
            if (!firstFrameSkipped) {
                firstFrameSkipped = true;
                return;
            }

            // Skip given amount of ticks
            if (skippedFrames > 0) {
                skippedFrames -= 1;
                return;
            }

            if (this.speed <= -1) {
                this.finished = true;
                if (this.sound != null) {
                    player.playSound(player.getLocation(), this.sound, SoundCategory.MASTER, 1f, 1f);
                }
                return;
            }

            skippedFrames = speed;

            this.frame += 1;
            if (this.sound != null) {
                player.playSound(player.getLocation(), this.sound, SoundCategory.MASTER, 1f, 1f);
            }

            if (this.frame >= this.frames.size()) {
                if (loop) {
                    this.frame = 0;
                } else {
                    finished = true;
                }
            }        }

        @Override
        public boolean finished() {
            if (loop) return true;
            return finished;
        }

        @Override
        public String text() {
            if (finished)
                return text;
            boolean showCursor = (cursor != null && frame != 0 && frame != this.frames.size()- 1);
            return this.frames.get(Math.min(frame, this.frames.size())) + (showCursor ? cursor : "");
        }

    }

}
