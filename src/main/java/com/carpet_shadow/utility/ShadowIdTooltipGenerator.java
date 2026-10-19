package com.carpet_shadow.utility;

import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.util.Formatting;

public class ShadowIdTooltipGenerator {

    public static final String TOOLTIP_KEY = "shadow_id";

    public static MutableText generateTooltip(ShadowItem shadowItem) {
        MutableText text = MutableText.of(new PlainTextContent.Literal(TOOLTIP_KEY + ": "));
        MutableText sub = MutableText.of(new PlainTextContent.Literal(shadowItem.carpet_shadow$getShadowId()));
        sub.formatted(Formatting.GOLD, Formatting.BOLD);
        text.append(sub);
        return text;
    }
}
