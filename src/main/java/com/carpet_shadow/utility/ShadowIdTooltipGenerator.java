package com.carpet_shadow.utility;

import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ShadowIdTooltipGenerator {

    private static final String TOOLTIP_KEY = "shadow_id: ";

    public static MutableText generateTooltip(ShadowItem shadowItem) {
        MutableText text = MutableText.of(new PlainTextContent.Literal(TOOLTIP_KEY));
        MutableText sub = MutableText.of(new PlainTextContent.Literal(shadowItem.carpet_shadow$getShadowId()));
        sub.formatted(Formatting.GOLD, Formatting.BOLD);
        text.append(sub);
        return text;
    }

    public static boolean isShadowIdTooltip(MutableText text) {
        if (text == null)
            return false;

        if (text.getContent() instanceof PlainTextContent.Literal content) {
            if (!content.string().equals(TOOLTIP_KEY))
                return false;

            List<Text> siblings = text.getSiblings();
            if (siblings == null)
                return false;

            if (siblings.size() != 1)
                return false;

            if (siblings.get(0).getContent() instanceof PlainTextContent.Literal)
                return true;
        }

        return false;
    }
}
