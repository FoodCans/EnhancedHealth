package dev.foodcans.enhancedhealth.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.ListIterator;

public class StringUtil
{
    public static String translate(String input)
    {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> translateList(List<String> input)
    {
        if (input == null)
        {
            return null;
        }
        ListIterator<String> stringListIterator = input.listIterator();
        while (stringListIterator.hasNext())
        {
            String s = stringListIterator.next();
            stringListIterator.set(translate(s));
        }
        return input;
    }
}
