package me.DMan16.DMan16Utils.Interfaces;

public interface ItemableReviveable<V extends Itemable<V> & Reviveable<V>> extends Itemable<V>,Reviveable<V> {}