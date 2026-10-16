package com.carpet_shadow;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.api.settings.Validators;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import static carpet.api.settings.RuleCategory.*;

public class CarpetShadowSettings {
    public static final String SHADOW = "shadow_items";

    @Rule(categories = {SHADOW, COMMAND}, validators = Validators.CommandLevel.class)
    public static String shadowItemCommand = "ops";

    // TODO: remove in later versions
    @Rule(categories = {SHADOW, BUGFIX})
    public static boolean shadowTrackUntrackedItems = true;

    @Rule(categories = {SHADOW}, validators = IdSizeValidator.class)
    public static int shadowItemIdSize = 10;

    @Rule(categories = {SHADOW, FEATURE})
    public static boolean shadowSuppressionGeneration = false;

    @Rule(categories = {SHADOW, FEATURE})
    public static boolean shadowCraftingGeneration = false;

    @Rule(categories = {SHADOW, FEATURE})
    public static boolean shadowItemTooltip = false;

    @Rule(categories = {SHADOW, BUGFIX})
    public static boolean shadowItemInventoryFragilityFix = false;

    @Rule(categories = {SHADOW, BUGFIX})
    public static boolean shadowItemTransferFragilityFix = false;

    @Rule(categories = {SHADOW, BUGFIX, EXPERIMENTAL})
    public static boolean shadowItemUpdateFix = false;

    @Rule(categories = {SHADOW, OPTIMIZATION, FEATURE})
    public static boolean shadowItemPreventCombine = false;

    @Rule(categories = {SHADOW, OPTIMIZATION, FEATURE})
    public static boolean shadowItemUseFix = false;

    @Rule(categories = {SHADOW, FEATURE})
    public static boolean shadowItemDropFix = false;

    private static class IdSizeValidator extends Validator<Integer> {
        @Override
        public Integer validate(@Nullable ServerCommandSource source, CarpetRule<Integer> changingRule, Integer newValue, String userInput) {
            int MIN_ID_SIZE = 4;
            return newValue >= MIN_ID_SIZE ? newValue : changingRule.value();
        }
    }
}
