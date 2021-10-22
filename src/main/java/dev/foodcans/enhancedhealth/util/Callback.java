package dev.foodcans.enhancedhealth.util;

@FunctionalInterface
public interface Callback<T>
{
    void call(T result);
}
