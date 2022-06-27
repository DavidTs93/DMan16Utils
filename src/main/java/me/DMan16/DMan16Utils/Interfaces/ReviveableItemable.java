package me.DMan16.DMan16Utils.Interfaces;

public interface ReviveableItemable<V extends Itemable<V> & Reviveable<V>> extends Itemable<V>,Reviveable<V> {
}